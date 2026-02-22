package com.contact;

import java.sql.SQLException;

import com.contact.dao.PersonDAOImpl;
import com.contact.model.Person;
import com.contact.service.PersonService;
import com.contact.service.PersonServiceImpl;
import com.contact.ui.PersonFormController;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private ObservableList<Person> personList = FXCollections.observableArrayList();
    private FilteredList<Person> filteredList;
    private TableView<Person> table = new TableView<>();
    private PersonFormController formController;
    private Label statsLabel;
    private PersonService personService;

    @SuppressWarnings({ "unchecked", "deprecation" })
	@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Contact Manager");
        personService = new PersonServiceImpl(new PersonDAOImpl());

        loadPersons();
        filteredList = new FilteredList<>(personList, p -> true);
        formController = new PersonFormController(personList);

        // Header
        Label title = new Label("Contact Manager");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Manage your contacts with ease");
        subtitle.getStyleClass().add("app-subtitle");
        VBox headerText = new VBox(2, title, subtitle);

        HBox header = new HBox(headerText);
        header.getStyleClass().add("header");

        // Menu bar - application navigation
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setOnAction(e -> loadPersons());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());
        fileMenu.getItems().addAll(refreshItem, new SeparatorMenuItem(), exitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAbout());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

        // Toolbar
        TextField searchField = new TextField();
        searchField.setPromptText("\uD83D\uDD0D  Search contacts...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(240);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredList.setPredicate(p -> {
                if (lower.isEmpty()) return true;
                return (p.getFullName() != null && p.getFullName().toLowerCase().contains(lower))
                    || (p.getNickname() != null && p.getNickname().toLowerCase().contains(lower))
                    || (p.getPhoneNumber() != null && p.getPhoneNumber().toLowerCase().contains(lower))
                    || (p.getEmailAddress() != null && p.getEmailAddress().toLowerCase().contains(lower));
            });
            updateStats();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add");
        addBtn.getStyleClass().add("btn-add");
        addBtn.setOnAction(e -> formController.showAddForm(primaryStage));

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("btn-edit");
        editBtn.setOnAction(e ->
                formController.showEditForm(primaryStage, table.getSelectionModel().getSelectedItem()));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-delete");
        deleteBtn.setOnAction(e ->
                formController.showDeleteConfirmation(table.getSelectionModel().getSelectedItem()));

        Button refreshBtn = new Button("\u21BB Refresh");
        refreshBtn.getStyleClass().add("btn-refresh");
        refreshBtn.setOnAction(e -> loadPersons());

        HBox toolbar = new HBox(12, searchField, spacer, refreshBtn, addBtn, editBtn, deleteBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(16, 24, 12, 24));

        table.setItems(filteredList);
        table.setPlaceholder(new Label("No contacts found"));

        TableColumn<Person, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getIdperson())));
        idCol.setPrefWidth(50);
        idCol.setMaxWidth(60);

        TableColumn<Person, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFullName()));
        nameCol.setPrefWidth(170);

        TableColumn<Person, String> nickCol = new TableColumn<>("Nickname");
        nickCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNickname()));
        nickCol.setPrefWidth(100);

        TableColumn<Person, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPhoneNumber() != null ? c.getValue().getPhoneNumber() : "—"));
        phoneCol.setPrefWidth(110);

        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEmailAddress() != null ? c.getValue().getEmailAddress() : "—"));
        emailCol.setPrefWidth(190);

        table.getColumns().addAll(idCol, nameCol, nickCol, phoneCol, emailCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Handle table selection events: enable/disable Edit and Delete when selection changes
        editBtn.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        deleteBtn.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        VBox tableCard = new VBox(table);
        tableCard.getStyleClass().add("card");
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        statsLabel = new Label();
        statsLabel.getStyleClass().add("stats-label");
        updateStats();
        HBox statsBar = new HBox(statsLabel);
        statsBar.setPadding(new Insets(8, 24, 12, 24));

        VBox content = new VBox(8, toolbar, tableCard, statsBar);
        content.setPadding(new Insets(0, 20, 10, 20));
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        VBox root = new VBox(menuBar, header, content);
        VBox.setVgrow(content, Priority.ALWAYS);

        Scene scene = new Scene(root, 720, 680);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(550);
        primaryStage.setMinHeight(500);
        primaryStage.show();

        personList.addListener((javafx.collections.ListChangeListener<Person>) c -> updateStats());
    }

    private void updateStats() {
        int total = personList.size();
        int shown = filteredList.size();
        if (total == shown) {
            statsLabel.setText(total + " contact" + (total != 1 ? "s" : ""));
        } else {
            statsLabel.setText(shown + " of " + total + " contacts");
        }
    }

    private void loadPersons() {
        try {
            personList.setAll(personService.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAbout() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Contact Manager");
        alert.setContentText("A JavaFX Contact Management Application.\n"
                + "Manage your contacts with ease.");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
