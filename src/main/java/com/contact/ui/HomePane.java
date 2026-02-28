package com.contact.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Home page view matching the Contact App design:
 * - Central graphic (image from resources)
 * - "My Contacts" title
 * - "Keep all your contacts organized" subtitle
 * - View Contacts and Add Contact buttons
 */
public class HomePane extends VBox {

    public HomePane(Runnable onViewContacts, Runnable onAddContact) {
        getStyleClass().add("home-pane");
        setAlignment(Pos.CENTER);
        setSpacing(24);
        setPadding(new javafx.geometry.Insets(40, 24, 48, 24));

        // Central graphic: image from resources
        Image image = new Image(getClass().getResourceAsStream("/image/image.png"));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(280);
        imageView.getStyleClass().add("home-graphic");

        // Title and subtitle
        Label title = new Label("My Contacts");
        title.getStyleClass().add("home-title");

        Label subtitle = new Label("Keep all your contacts organized");
        subtitle.getStyleClass().add("home-subtitle");

        // Buttons
        Button viewBtn = new Button("View Contacts");
        viewBtn.getStyleClass().add("btn-view-contacts");
        viewBtn.setOnAction(e -> onViewContacts.run());

        Button addBtn = new Button("Add Contact");
        addBtn.getStyleClass().add("btn-add-contact");
        addBtn.setOnAction(e -> onAddContact.run());

        VBox buttons = new VBox(12, viewBtn, addBtn);
        buttons.setAlignment(Pos.CENTER);

        getChildren().addAll(imageView, title, subtitle, buttons);
    }
}
