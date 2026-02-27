module com.contact {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    
    opens com.contact.ui to javafx.fxml;
    exports com.contact.model;
    exports com.contact.dao;
    exports com.contact.service;
    exports com.contact.ui;
}