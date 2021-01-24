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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import ui.Controller;
import ui.util.Util;
import world.app.App;
import world.app.user.User;

import java.io.IOException;
import java.sql.SQLException;

public class LoginViewController extends Controller {
   private final Text welcomeText;
   private final Text indicationText;
   private final Text copyrightText;
   private Scene scene;
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

   @FXML
   public void initialize(){
      WelcomeMessageTextField.getChildren().add(welcomeText);
      WelcomeMessageTextField.getChildren().add(indicationText);
      WelcomeMessageTextField.setTextAlignment(TextAlignment.CENTER);

      StandardCopyrightTextFlow.getChildren().add(copyrightText);
      StandardCopyrightTextFlow.setTextAlignment(TextAlignment.CENTER);
   }

   @Override
   public void load(Stage primaryStage) {
      UserIdTextField.clear();
      PasswordTextField.clear();
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
   void createUser(ActionEvent event) {
      try {
         FXMLLoader createUserLoader = new FXMLLoader();
         createUserLoader.setLocation(getClass().getClassLoader().getResource("views/createUser.fxml"));
         Scene createUserScene = new Scene(createUserLoader.load());
         CreateUserController createUserController = createUserLoader.getController();
         createUserController.setScene(createUserScene);
         createUserController.setLoginViewController(this);

         createUserController.load((Stage) WelcomeMessageTextField.getScene().getWindow());
      } catch (IOException e) {
         e.printStackTrace();
      }
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
         Util.createAlertFrame(Alert.AlertType.ERROR, "Wrong password", "Wrong password",
                               "The password you entered is false, please try again");
      } catch (User.UserIdNotFoundException e) {
         e.printStackTrace();
         Util.createAlertFrame(Alert.AlertType.ERROR, "Wrong id", "Wrong id", "The id you entered is not registered");
      } catch (SQLException throwable) {
         throwable.printStackTrace();
         Util.createAlertFrame(Alert.AlertType.ERROR, "Connection Error", "Connection Error",
                               "Could not open the connection to the Database, please try again");
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}

