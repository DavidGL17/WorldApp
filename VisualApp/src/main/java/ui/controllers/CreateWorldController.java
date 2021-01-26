/*
 * @File CreateWorldController.java
 * @Authors : David González León
 * @Date 26 janv. 2021
 */
package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import ui.Controller;
import world.app.App;

public class CreateWorldController extends Controller {
   private Scene scene;
   private Text welcomeText;
   private App app;

   @FXML private TextFlow TFWelcomeMessage;

   @FXML private TextField TNameWorld;
   @FXML private TextField TDescriptionWorld;

   public CreateWorldController() {
      welcomeText = new Text("Please enter the name and description of the World\nThe name of the world must not be " +
                             "one you haev already used");
      welcomeText.setFont(Font.font(16));
   }

   @Override
   public void load(Stage primaryStage) {
      TFWelcomeMessage.getChildren().add(welcomeText);
      TFWelcomeMessage.setTextAlignment(TextAlignment.CENTER);

      TNameWorld.requestFocus();

      primaryStage.setScene(scene);
      primaryStage.setTitle("World creation");
   }

   @Override
   public void setScene(Scene scene) {
      this.scene = scene;
   }

   public void setApp(App app) {
      this.app = app;
   }

   @FXML
   void clickedCancel(ActionEvent event) {

   }

   @FXML
   void clickedCreateWorld(ActionEvent event) {

   }

}

