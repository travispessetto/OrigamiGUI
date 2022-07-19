/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

module OrigamiGUI {
    requires javafx.controls;
    requires javafx.web;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.graphics;
    requires javafx.media;
    requires jdk.jsobject;
    requires java.desktop;
    requires org.json;
    requires com.pessetto.origamismtp;


    opens com.pessetto.origamigui to javafx.fxml, java.desktop;
    opens com.pessetto.origamigui.web to javafx.web;
    exports com.pessetto.origamigui.console;
}
