module com.contact {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;
    
    opens com.contact.ui to javafx.fxml;
    exports com.contact.model;
    exports com.contact.dao;
    exports com.contact.service;
    exports com.contact.ui;
}