/*
 * @File MainViewController.java
 * @Authors : David González León
 * @Date 17 janv. 2021
 */
package ui.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.Controller;
import world.app.App;
import world.app.world.World;

import java.io.IOException;

public class MainViewController extends Controller {
   private Scene scene;
   private App app;
   private boolean firstLoad = true;

   @FXML private TabPane TabPaneMainView;
   @FXML private Tab TWorldSelection;

   @FXML private TableView<World> TWorld;
   @FXML private TableColumn<World, String> TCWorldName;
   @FXML private TableColumn<World, Integer> TCNumberOfArticles;
   @FXML private TableColumn<World, String> TCDescription;

   public void setApp(App app) {
      this.app = app;
   }

   @Override
   public void load(Stage primaryStage) {
      if (firstLoad) {
         firstLoad = false;
         TCWorldName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
         TCNumberOfArticles
                 .setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getArticles().size()));
         TCDescription.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDescription()));
      }
      TWorld.getItems().clear();
      TWorld.setItems(FXCollections.observableList(app.getWorlds().toList()));
      primaryStage.setScene(scene);
      primaryStage.setTitle("Main menu");
   }

   @Override
   public void setScene(Scene scene) {
      this.scene = scene;
   }

   @FXML
   void addWorld(ActionEvent event) {
      try {
         FXMLLoader createWorldLoader = new FXMLLoader();
         createWorldLoader.setLocation(getClass().getClassLoader().getResource("views/createWorld.fxml"));
         Scene loginScene = new Scene(createWorldLoader.load());
         CreateWorldController createWorldController = createWorldLoader.getController();
         createWorldController.setScene(loginScene);
         createWorldController.setApp(app);

         Stage stage = new Stage();
         stage.setAlwaysOnTop(true);
         createWorldController.load(stage);
         stage.setScene(loginScene);
         stage.initOwner(TWorld.getScene().getWindow());
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.show();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @FXML
   void worldTableClicked(MouseEvent event) {
      if (event.getButton().equals(MouseButton.PRIMARY)){
         if (event.getClickCount() == 2){
         }
      }
   }
}
