/*
 * @File LoginViewController.java
 * @Authors : David González León
 * @Date 17 janv. 2021
 */
package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import ui.Controller;

public class LoginViewController extends Controller {

   @FXML private TextFlow WelcomeMessageTextField;
   @FXML private TextFlow StandardCopyrightTextFlow;

   @FXML private PasswordField PasswordTextField;
   @FXML private TextField UserIdTextField;
   @FXML private Button LoginButton;
   @FXML private Button LoginButton1;

   @Override
   public void load(Stage primaryStage) {

   }

   @FXML
   void createUser(ActionEvent event) {

   }

   @FXML
   void login(ActionEvent event) {

   }

   @FXML
   private void initialize() {

   }

}

