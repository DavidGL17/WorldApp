/*
 * @File CreateUserController.java
 * @Authors : David González León
 * @Date 24 janv. 2021
 */
package ch.world.app.client.visual.controllers;

import ch.world.app.client.background.App;
import ch.world.app.client.background.user.User;
import ch.world.app.client.visual.Controller;
import ch.world.app.client.visual.util.Util;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class CreateUserController extends Controller {
   private Scene scene;
   private Text welcomeText;
   private LoginViewController loginViewController;

   @FXML private TextFlow TFWelcomeMessage;

   @FXML private Button BCancelUser;
   @FXML private Button BCreateUser;

   @FXML private TextField TFirstNameUser;
   @FXML private TextField TLastNameUser;
   @FXML private TextField TEmailUser;
   @FXML private PasswordField PFPasswordUser;

   public CreateUserController() {
      welcomeText = new Text("Please enter your account information in the fields bellow");
      welcomeText.setFont(Font.font(16));
   }

   @Override
   public void load(Stage primaryStage) {
      TFWelcomeMessage.getChildren().add(welcomeText);
      TFWelcomeMessage.setTextAlignment(TextAlignment.CENTER);

      TFirstNameUser.requestFocus();

      primaryStage.setScene(scene);
      primaryStage.setTitle("User creation");
   }

   @Override
   public void setScene(Scene scene) {
      this.scene = scene;
   }

   public void setLoginViewController(LoginViewController loginViewController) {
      this.loginViewController = loginViewController;
   }

   @FXML
   void onClickedCancel(MouseEvent event) {
      loginViewController.load((Stage) TFWelcomeMessage.getScene().getWindow());
   }

   @FXML
   void onClickedCreateUser(MouseEvent event) {
      if (!TFirstNameUser.getText().isBlank() && !TLastNameUser.getText().isBlank() &&
          !TEmailUser.getText().isBlank() && !PFPasswordUser.getText().isBlank()) {
         try {
            App app = new App(TFirstNameUser.getText(), TLastNameUser.getText(), TEmailUser.getText(),
                              PFPasswordUser.getText());
            FXMLLoader menuLoader = new FXMLLoader();
            menuLoader.setLocation(getClass().getClassLoader().getResource("views/mainView.fxml"));
            Scene menuScene = new Scene(menuLoader.load());
            MainViewController mainViewController = menuLoader.getController();
            mainViewController.setScene(menuScene);
            mainViewController.setApp(app);

            mainViewController.load((Stage) TFWelcomeMessage.getScene().getWindow());
         } catch (User.UserIdNotFoundException | User.WrongPasswordException e) {
            e.printStackTrace();
            Util.createAlertFrame(Alert.AlertType.ERROR, "Error while creating the user",
                                  "Error while creating the user",
                                  "There has been an error while creating you user account. Please check you are" +
                                  " correctly connected to internet before trying again. If the problem persists, " +
                                  "please contact the developper of the app");
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            Util.createAlertFrame(Alert.AlertType.ERROR, "Connection error", "Connection Error",
                                  "Could not open the connection to the Database, please try again");
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         Util.createAlertFrame(Alert.AlertType.INFORMATION, "Login information incomplete",
                               "You have not filled all the required fields",
                               "You have not filled all the required fields. Please do so before trying again.");
      }
   }
}
