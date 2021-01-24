/*
 * @File CreateUserController.java
 * @Authors : David González León
 * @Date 24 janv. 2021
 */
package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import ui.Controller;

public class CreateUserController extends Controller {
   private Scene scene;
   private Text welcomeText;

   @FXML private TextFlow TFWelcomeMessage;

   @FXML private Button BCancelUser;
   @FXML private Button BCreateUser;

   @FXML private TextField TFirstNameUser;
   @FXML private TextField TLastNameUser;
   @FXML private TextField TEmailUser;
   @FXML private PasswordField PFPasswordUser;

   public CreateUserController() {
      welcomeText.setText("Please enter your account information in the fields bellow");
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


   @FXML
   void onClickedCancel(MouseEvent event) {

   }

   @FXML
   void onClickedCreateUser(MouseEvent event) {

   }
}
