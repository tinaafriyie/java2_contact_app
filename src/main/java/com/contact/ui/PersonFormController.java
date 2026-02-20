package com.contact.ui;

import com.contact.dao.PersonDAO;
import com.contact.dao.PersonDAOImpl;
import com.contact.model.Person;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

// Person 4 — UI forms and CRUD operations
// Currently calls DAO directly; swap to PersonService when ready
public class PersonFormController {

    private final PersonDAO personDAO;

    private TextField lastNameField;
    private TextField firstNameField;
    private TextField nicknameField;
    private TextField phoneField;
    private TextField addressField;
    private TextField emailField;
    private DatePicker birthDatePicker;

    private ObservableList<Person> personList;

    private Integer editingPersonId = null;

    public PersonFormController() {
        this.personDAO = new PersonDAOImpl();
    }

    public PersonFormController(ObservableList<Person> personList) {
        this.personDAO = new PersonDAOImpl();
        this.personList = personList;
    }

    public void setPersonList(ObservableList<Person> personList) {
        this.personList = personList;
    }

    // --- Public methods (called by main controller) ---

    public void showAddForm(Stage ownerStage) {
        editingPersonId = null;
        showFormWindow(ownerStage, "Add New Person", null);
    }

    public void showEditForm(Stage ownerStage, Person person) {
        if (person == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a person from the table first.");
            return;
        }
        editingPersonId = person.getIdperson();
        showFormWindow(ownerStage, "Update Person", person);
    }

    public void showDeleteConfirmation(Person person) {
        if (person == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a person from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Person");
        confirm.setHeaderText("Are you sure you want to delete this person?");
        confirm.setContentText(person.getFullName() + " (ID: " + person.getIdperson() + ")");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean deleted = personDAO.delete(person.getIdperson());
                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Deleted",
                                person.getFullName() + " has been deleted.");
                        refreshPersonList();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "Could not delete person. They may have already been removed.");
                    }
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
                }
            }
        });
    }

    // --- Form window ---

    private void showFormWindow(Stage ownerStage, String title, Person existingPerson) {
        Stage formStage = new Stage();
        formStage.setTitle(title);
        formStage.initModality(Modality.APPLICATION_MODAL);
        if (ownerStage != null) {
            formStage.initOwner(ownerStage);
        }

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("form-title");
        Label subtitleLabel = new Label(existingPerson == null
                ? "Fill in the details below to add a new contact"
                : "Modify the contact information");
        subtitleLabel.getStyleClass().add("form-subtitle");
        VBox headerBox = new VBox(2, titleLabel, subtitleLabel);
        headerBox.getStyleClass().add("form-header");

        GridPane grid = buildFormGrid();

        if (existingPerson != null) {
            populateFields(existingPerson);
        }

        VBox formBody = new VBox(grid);
        formBody.getStyleClass().add("form-body");

        Button saveButton = new Button(existingPerson == null ? "  Add Contact  " : "  Save Changes  ");
        saveButton.getStyleClass().add("btn-save");
        saveButton.setOnAction(e -> {
            if (existingPerson == null) {
                handleAdd(formStage);
            } else {
                handleUpdate(formStage);
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("btn-cancel");
        cancelButton.setOnAction(e -> formStage.close());

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("btn-clear");
        clearButton.setOnAction(e -> clearForm());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonBar = new HBox(10, clearButton, spacer, cancelButton, saveButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(18, 24, 18, 24));

        VBox content = new VBox(12, formBody);
        content.setPadding(new Insets(16, 20, 0, 20));

        VBox root = new VBox(headerBox, content, buttonBar);
        root.getStyleClass().add("form-root");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        formStage.setScene(scene);
        formStage.setResizable(false);
        formStage.showAndWait();
    }

    private GridPane buildFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        grid.setPadding(new Insets(4));
        grid.setMinWidth(440);

        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(100);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setHgrow(Priority.ALWAYS);
        fieldCol.setMinWidth(260);
        grid.getColumnConstraints().addAll(labelCol, fieldCol);

        lastNameField   = new TextField();
        firstNameField  = new TextField();
        nicknameField   = new TextField();
        phoneField      = new TextField();
        addressField    = new TextField();
        emailField      = new TextField();
        birthDatePicker = new DatePicker();

        for (TextField tf : new TextField[]{lastNameField, firstNameField, nicknameField,
                phoneField, addressField, emailField}) {
            tf.getStyleClass().add("form-field");
            tf.setMaxWidth(Double.MAX_VALUE);
        }
        birthDatePicker.setMaxWidth(Double.MAX_VALUE);
        birthDatePicker.getStyleClass().add("form-field");

        lastNameField.setPromptText("Enter last name");
        firstNameField.setPromptText("Enter first name");
        nicknameField.setPromptText("Enter nickname");
        phoneField.setPromptText("e.g. 555-0101");
        addressField.setPromptText("e.g. 123 Main St");
        emailField.setPromptText("e.g. john@email.com");

        Label personalSection = new Label("PERSONAL INFORMATION");
        personalSection.getStyleClass().add("section-title");
        grid.add(personalSection, 0, 0, 2, 1);

        grid.add(requiredLabel("Last Name"),  0, 1);
        grid.add(lastNameField,               1, 1);
        grid.add(requiredLabel("First Name"), 0, 2);
        grid.add(firstNameField,              1, 2);
        grid.add(requiredLabel("Nickname"),   0, 3);
        grid.add(nicknameField,               1, 3);
        grid.add(optionalLabel("Birth Date"), 0, 4);
        grid.add(birthDatePicker,             1, 4);

        Label contactSection = new Label("CONTACT DETAILS");
        contactSection.getStyleClass().add("section-title");
        grid.add(contactSection, 0, 5, 2, 1);

        grid.add(optionalLabel("Phone"),      0, 6);
        grid.add(phoneField,                  1, 6);
        grid.add(optionalLabel("Email"),      0, 7);
        grid.add(emailField,                  1, 7);
        grid.add(optionalLabel("Address"),    0, 8);
        grid.add(addressField,                1, 8);

        return grid;
    }

    private Label requiredLabel(String text) {
        return new Label(text + " *") {{
            getStyleClass().add("form-label");
        }};
    }

    private Label optionalLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    // --- Form actions ---

    private void handleAdd(Stage formStage) {
        String validationError = validateForm();
        if (validationError != null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", validationError);
            return;
        }

        Person person = buildPersonFromFields();
        try {
            personDAO.create(person);
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    person.getFullName() + " has been added.");
            clearForm();
            refreshPersonList();
            formStage.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not add person: " + e.getMessage());
        }
    }

    private void handleUpdate(Stage formStage) {
        String validationError = validateForm();
        if (validationError != null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", validationError);
            return;
        }

        Person person = buildPersonFromFields();
        person.setIdperson(editingPersonId);
        try {
            boolean updated = personDAO.update(person);
            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        person.getFullName() + " has been updated.");
                clearForm();
                refreshPersonList();
                formStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Could not update. Person may have been deleted.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not update person: " + e.getMessage());
        }
    }

    // --- Validation ---

    private String validateForm() {
        if (isBlank(lastNameField.getText())) {
            lastNameField.requestFocus();
            return "Last name is required.";
        }
        if (isBlank(firstNameField.getText())) {
            firstNameField.requestFocus();
            return "First name is required.";
        }
        if (isBlank(nicknameField.getText())) {
            nicknameField.requestFocus();
            return "Nickname is required.";
        }

        if (lastNameField.getText().trim().length() > 45) {
            return "Last name must be 45 characters or less.";
        }
        if (firstNameField.getText().trim().length() > 45) {
            return "First name must be 45 characters or less.";
        }
        if (nicknameField.getText().trim().length() > 45) {
            return "Nickname must be 45 characters or less.";
        }
        if (!isBlank(phoneField.getText()) && phoneField.getText().trim().length() > 15) {
            return "Phone number must be 15 characters or less.";
        }
        if (!isBlank(addressField.getText()) && addressField.getText().trim().length() > 200) {
            return "Address must be 200 characters or less.";
        }

        String email = emailField.getText();
        if (!isBlank(email)) {
            if (email.trim().length() > 150) {
                return "Email must be 150 characters or less.";
            }
            if (!email.trim().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                return "Email format is invalid (e.g. user@example.com).";
            }
        }

        if (birthDatePicker.getValue() != null
                && birthDatePicker.getValue().isAfter(LocalDate.now())) {
            return "Birth date cannot be in the future.";
        }

        return null;
    }

    // --- Helpers ---

    private Person buildPersonFromFields() {
        Person p = new Person();
        p.setLastname(lastNameField.getText().trim());
        p.setFirstname(firstNameField.getText().trim());
        p.setNickname(nicknameField.getText().trim());
        p.setPhoneNumber(isBlank(phoneField.getText()) ? null : phoneField.getText().trim());
        p.setAddress(isBlank(addressField.getText()) ? null : addressField.getText().trim());
        p.setEmailAddress(isBlank(emailField.getText()) ? null : emailField.getText().trim());
        p.setBirthDate(birthDatePicker.getValue());
        return p;
    }

    private void populateFields(Person p) {
        lastNameField.setText(p.getLastname() != null ? p.getLastname() : "");
        firstNameField.setText(p.getFirstname() != null ? p.getFirstname() : "");
        nicknameField.setText(p.getNickname() != null ? p.getNickname() : "");
        phoneField.setText(p.getPhoneNumber() != null ? p.getPhoneNumber() : "");
        addressField.setText(p.getAddress() != null ? p.getAddress() : "");
        emailField.setText(p.getEmailAddress() != null ? p.getEmailAddress() : "");
        birthDatePicker.setValue(p.getBirthDate());
    }

    private void clearForm() {
        lastNameField.clear();
        firstNameField.clear();
        nicknameField.clear();
        phoneField.clear();
        addressField.clear();
        emailField.clear();
        birthDatePicker.setValue(null);
        editingPersonId = null;
    }

    private void refreshPersonList() {
        if (personList == null) return;
        try {
            personList.setAll(personDAO.findAll());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Refresh Error",
                    "Could not refresh list: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
