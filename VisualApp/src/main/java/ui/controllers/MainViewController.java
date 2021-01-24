/*
 * @File MainViewController.java
 * @Authors : David González León
 * @Date 17 janv. 2021
 */
package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import ui.Controller;
import world.app.App;

public class MainViewController extends Controller {
   private Scene scene;
   private App app;

   @FXML private TextFlow mainTextField;

   @Override
   public void setScene(Scene scene) {
      this.scene = scene;
   }

   public void setApp(App app){
      this.app = app;
   }

   @Override
   public void load(Stage primaryStage) {
      primaryStage.setScene(scene);
      primaryStage.setTitle("Main menu");
   }

   @FXML
   private void initialize() {

   }
}
