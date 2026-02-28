package com.contact.ui;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import com.contact.dao.PersonDAO;
import com.contact.dao.PersonDAOImpl;
import com.contact.model.Person;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PersonFormController {

    private final PersonDAO personDAO;

    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField nicknameField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField emailField;
    @FXML private DatePicker birthDatePicker;
    @FXML private Label formTitle;
    @FXML private Label formSubtitle;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button clearButton;

    @FXML
    public void initialize() {
        // Allow only digits in phoneField
        phoneField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                phoneField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });

        // Allow only letters, spaces, hyphens, and apostrophes in name fields
        javafx.beans.value.ChangeListener<String> nameListener = (obs, oldText, newText) -> {
            if (!newText.matches("[a-zA-Z-' ]*")) {
                ((TextField)((javafx.beans.property.StringProperty)obs).getBean()).setText(newText.replaceAll("[^a-zA-Z-' ]", ""));
            }
        };
        lastNameField.textProperty().addListener(nameListener);
        firstNameField.textProperty().addListener(nameListener);
        nicknameField.textProperty().addListener(nameListener);
    }

    private ObservableList<Person> personList;
    private Integer editingPersonId = null;
    private Stage formStage;

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
                    boolean deleted = personDAO.deletePerson(person.getIdperson());
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

    private void showFormWindow(Stage ownerStage, String title, Person existingPerson) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PersonForm.fxml"));
            loader.setController(this);
            VBox root = loader.load();

            formStage = new Stage();
            formStage.setTitle(title);
            formStage.initModality(Modality.APPLICATION_MODAL);
            if (ownerStage != null) {
                formStage.initOwner(ownerStage);
            }

            formTitle.setText(title);
            if (existingPerson == null) {
                formSubtitle.setText("Fill in the details below to add a new contact");
                saveButton.setText("  Add Contact  ");
            } else {
                formSubtitle.setText("Modify the contact information");
                saveButton.setText("  Save Changes  ");
                populateFields(existingPerson);
            }

            saveButton.setOnAction(e -> {
                if (existingPerson == null) {
                    handleAdd();
                } else {
                    handleUpdate();
                }
            });
            cancelButton.setOnAction(e -> formStage.close());
            clearButton.setOnAction(e -> clearForm());

            Scene scene = new Scene(root);
            formStage.setScene(scene);
            formStage.setResizable(false);
            formStage.showAndWait();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load form: " + e.getMessage());
        }
    }

    private void handleAdd() {
        String validationError = validateForm();
        if (validationError != null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", validationError);
            return;
        }

        Person person = buildPersonFromFields();
        try {
            personDAO.createPerson(person);
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    person.getFullName() + " has been added.");
            clearForm();
            refreshPersonList();
            formStage.close();
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Entry",
                        "This phone number or email already exists in the database.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error",
                        "Could not add person: " + e.getMessage());
            }
        }
    }

    private void handleUpdate() {
        String validationError = validateForm();
        if (validationError != null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", validationError);
            return;
        }

        Person person = buildPersonFromFields();
        person.setIdperson(editingPersonId);
        try {
            boolean updated = personDAO.updatePerson(person);
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
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Entry",
                        "This phone number or email already exists in the database.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error",
                        "Could not add person: " + e.getMessage());
            }
        }
    }

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

        if (lastNameField.getText().trim().length() > 45)
            return "Last name must be 45 characters or less.";
        if (firstNameField.getText().trim().length() > 45)
            return "First name must be 45 characters or less.";
        if (nicknameField.getText().trim().length() > 45)
            return "Nickname must be 45 characters or less.";
        if (!isBlank(phoneField.getText()) && phoneField.getText().trim().length() > 15)
            return "Phone number must be 15 characters or less.";
        if (!isBlank(phoneField.getText())) {
            String phone = phoneField.getText().trim();
            if (!phone.matches("^\\d+$")) {
                phoneField.requestFocus();
                return "Phone number must contain only digits.";
            }
        }
        if (!isBlank(addressField.getText()) && addressField.getText().trim().length() > 200)
            return "Address must be 200 characters or less.";

        String email = emailField.getText();
        if (!isBlank(email)) {
            if (email.trim().length() > 150)
                return "Email must be 150 characters or less.";
            if (!email.trim().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
                return "Email format is invalid (e.g. user@example.com).";
        }

        if (birthDatePicker.getValue() != null
                && birthDatePicker.getValue().isAfter(LocalDate.now()))
            return "Birth date cannot be in the future.";

        return null;
    }

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
