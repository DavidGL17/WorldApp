/*
 * @File ui.App.java
 * @Authors : David González León,
 * @Date 13 janv. 2021
 */
package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.controllers.LoginViewController;

import java.io.IOException;
import java.net.URL;

//TODO le laucncher est construit, puis lancé avec une méthode start qui lui donne son utilisateur. Si l'id de
// l'utilisateur passé en paramètre est 0, il demande la création d'un user
public class Launcher extends Application {
   public static void main(String[] args) {
      launch();
   }

   @Override
   public void start(Stage primaryStage) throws IOException {
      FXMLLoader loginLoader = new FXMLLoader();
      loginLoader.setLocation(getClass().getClassLoader().getResource("views/loginView.fxml"));
      Scene loginScene = new Scene(loginLoader.load());
      LoginViewController loginViewController = loginLoader.getController();
      loginViewController.setScene(loginScene);


      loginViewController.load(primaryStage);
      primaryStage.setScene(loginScene);
      primaryStage.show();
      /**
       primaryStage.setTitle("Hello World!");
       Button btn = new Button();
       btn.setText("Say 'Hello World'");
       btn.setOnAction(event -> System.out.println("Hello World!"));

       //Insert a popup asking for the usersId

       App app = new App(1);

       StackPane root = new StackPane();
       root.getChildren().add(btn);
       TextArea textField = new TextArea();
       HashMapChaining<World> worlds = app.getWorlds();
       StringBuilder text = new StringBuilder();
       for (World w : worlds) {
       text.append(w).append("\n");
       w.loadArticles();
       for (Article a : w.getArticles()) {
       text.append(a).append("\n");
       }
       }
       textField.setText(text.toString());
       root.getChildren().add(textField);
       primaryStage.setScene(new Scene(root, 300, 250));
       primaryStage.show();**/
   }
}
