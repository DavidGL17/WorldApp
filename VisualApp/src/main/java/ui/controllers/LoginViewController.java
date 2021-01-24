/*
 * @File LoginViewController.java
 * @Authors : David González León
 * @Date 17 janv. 2021
 */
package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import ui.Controller;
import world.app.App;
import world.app.user.User;

import java.io.IOException;
import java.sql.SQLException;

public class LoginViewController extends Controller {
   private Scene scene;
   private Text welcomeText;
   private Text indicationText;
   private Text copyrightText;


   @FXML private TextFlow WelcomeMessageTextField;
   @FXML private TextFlow StandardCopyrightTextFlow;

   @FXML private PasswordField PasswordTextField;
   @FXML private TextField UserIdTextField;

   public LoginViewController() {
      welcomeText = new Text("Welcome to the world app application ! \n\n");
      welcomeText.setFont(Font.font(22));

      indicationText = new Text(
              "Please insert your login information in the fields bellow, or create a new user if you are new to the " +
              "app!");
      indicationText.setFont(Font.font(16));

      copyrightText = new Text("This app is being developped by David González León. All uses of this app must " +
                               "be for non-profit objectives.");
   }

   @Override
   public void load(Stage primaryStage) {
      //Welcome messages
      WelcomeMessageTextField.getChildren().add(welcomeText);
      WelcomeMessageTextField.getChildren().add(indicationText);
      WelcomeMessageTextField.setTextAlignment(TextAlignment.CENTER);

      StandardCopyrightTextFlow.getChildren().add(copyrightText);
      StandardCopyrightTextFlow.setTextAlignment(TextAlignment.CENTER);

      UserIdTextField.requestFocus();

      primaryStage.setScene(scene);
      primaryStage.setTitle("Login");
      primaryStage.getIcons().add(new Image("images/mainImage.jpg"));
   }

   @Override
   public void setScene(Scene scene) {
      this.scene = scene;
   }

   @FXML
   private void initialize() {

   }

   @FXML
   void createUser(ActionEvent event) {
      //TODO load createUser controller
   }

   @FXML
   void login(ActionEvent event) {
      try {
         App app = new App(Integer.parseInt(UserIdTextField.getText()), PasswordTextField.getText());

         FXMLLoader menuLoader = new FXMLLoader();
         menuLoader.setLocation(getClass().getClassLoader().getResource("views/mainView.fxml"));
         Scene menuScene = new Scene(menuLoader.load());
         MainViewController mainViewController = menuLoader.getController();
         mainViewController.setScene(menuScene);
         mainViewController.setApp(app);

         mainViewController.load((Stage) WelcomeMessageTextField.getScene().getWindow());
      } catch (User.WrongPasswordException e) {
         e.printStackTrace();
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Wrong password");
         alert.setHeaderText("Wrong password");
         alert.setContentText("The password you entered is false, please try again");
         ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/mainImage.jpg"));

         alert.showAndWait();
      } catch (User.UserIdNotFoundException e) {
         e.printStackTrace();
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Wrong id");
         alert.setHeaderText("Wrong id");
         alert.setContentText("The id you entered is not registered");
         ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/mainImage.jpg"));

         alert.showAndWait();
      } catch (SQLException throwables) {
         throwables.printStackTrace();
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Connection Error");
         alert.setHeaderText("Connection Error");
         alert.setContentText("Could not open the connection to the Database, please try again");
         ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/mainImage.jpg"));

         alert.showAndWait();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}

