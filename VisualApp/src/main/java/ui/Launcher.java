/*
 * @File ui.App.java
 * @Authors : David González León,
 * @Date 13 janv. 2021
 */
package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import world.app.App;
import world.app.world.Article;
import world.app.world.World;

import java.util.ArrayList;

public class Launcher extends Application {
   public static void main(String[] args) {
      launch();
   }

   @Override
   public void start(Stage primaryStage) {
      primaryStage.setTitle("Hello World!");
      Button btn = new Button();
      btn.setText("Say 'Hello World'");
      btn.setOnAction(event -> System.out.println("Hello World!"));

      StackPane root = new StackPane();
      root.getChildren().add(btn);
      primaryStage.setScene(new Scene(root, 300, 250));
      primaryStage.show();
   }
}
