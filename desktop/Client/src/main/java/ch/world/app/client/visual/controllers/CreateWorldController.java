/*
 * @File CreateWorldController.java
 * @Authors : David González León
 * @Date 26 janv. 2021
 */
package ch.world.app.client.visual.controllers;

import ch.world.app.client.background.App;
import ch.world.app.client.visual.Controller;
import ch.world.app.client.visual.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CreateWorldController extends Controller {
   private Scene scene;
   private Text welcomeText;
   private Text warningText = new Text();
   private App app;

   @FXML private TextFlow TFWelcomeMessage;

   @FXML private TextField TNameWorld;
   @FXML private TextField TDescriptionWorld;

   public CreateWorldController() {
      welcomeText = new Text("Please enter the name and description of the World\nThe name of the world must not be " +
                             "one you haev already used");
      welcomeText.setFont(Font.font(16));
      warningText.setFont(Font.font(14));
   }

   @Override
   public void load(Stage primaryStage) {
      TFWelcomeMessage.getChildren().add(welcomeText);
      TFWelcomeMessage.getChildren().add(warningText);
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
      ((Stage) TNameWorld.getScene().getWindow()).close();
   }

   @FXML
   void clickedCreateWorld(ActionEvent event) {
      try {
         if (TNameWorld.getText().isBlank() || TDescriptionWorld.getText().isBlank()) {
            throw new IllegalArgumentException("Fill all fields, and don't fill them with blank spaces alone.");
         }
         if (!app.getUser().isWorldNameValid(TNameWorld.getText())){
            throw new IllegalArgumentException("The name of your world cannot be one you have already used..");
         }
         app.addWorld(TNameWorld.getText(),TDescriptionWorld.getText());
      } catch (SQLException exception) {
         exception.printStackTrace();
         Util.createAlertFrame(Alert.AlertType.ERROR, "Error in article creation", "Error in article creation",
                               "There was an error while creating your Article, please check you are correctly " +
                               "connected to the Internet and try again. If the problem persists, please contact " +
                               "support.");
      } catch (IllegalArgumentException exception) {
         warningText.setText("\nThere were some issues with the values given.\n" + exception.getMessage());
      }
   }

}

