/*
 * @File MainViewController.java
 * @Authors : David González León
 * @Date 17 janv. 2021
 */
package ui.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
import ui.util.Util;
import world.app.App;
import world.app.world.Article;
import world.app.world.World;

import java.io.IOException;
import java.sql.Date;

public class MainViewController extends Controller {
   private Scene scene;
   private App app;
   private boolean firstLoad = true;

   @FXML private TabPane TabPaneMainView;

   @FXML private Tab TWorldSelection;
   @FXML private TableView<World> TWSWorld;
   @FXML private TableColumn<World, String> TCWSWorldName;
   @FXML private TableColumn<World, Integer> TCWSNumberOfArticles;
   @FXML private TableColumn<World, String> TCWSDescription;

   private World selectedWorld;
   @FXML private Tab TOpenedWorld;
   @FXML private TableView<Article> TOWArticles;
   @FXML private TableColumn<Article, String> TOWArticleName;
   @FXML private TableColumn<Article, String> TOWArticleType;
   @FXML private TableColumn<Article, String> TOWArticleDescription;
   @FXML private TableColumn<Article, Date> TOWArticleDate;

   public void setApp(App app) {
      this.app = app;
   }

   @Override
   public void load(Stage primaryStage) {
      if (firstLoad) {
         firstLoad = false;
         TCWSWorldName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
         TCWSNumberOfArticles
                 .setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getArticles().size()));
         TCWSDescription.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDescription()));

         TOWArticleName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
         TOWArticleType.setCellValueFactory(
                 param -> new SimpleStringProperty(param.getValue().getTypeOfArticle().toString()));
         TOWArticleDescription.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getContent()));
         TOWArticleDate.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getLastUpdate()));
      }
      refreshWorldTable();

      TOpenedWorld.setDisable(true);

      primaryStage.setScene(scene);
      primaryStage.setTitle("Main menu");
      primaryStage.setMaximized(true);
   }

   @Override
   public void setScene(Scene scene) {
      this.scene = scene;
   }

   @FXML
   public void addWorld(ActionEvent event) {
      try {
         FXMLLoader createWorldLoader = new FXMLLoader();
         createWorldLoader.setLocation(getClass().getClassLoader().getResource("views/createWorld.fxml"));
         Scene loginScene = new Scene(createWorldLoader.load());
         CreateWorldController createWorldController = createWorldLoader.getController();
         createWorldController.setApp(app);
         createWorldController.setScene(loginScene);
         Stage newStage = Util.createNewWindowOver(createWorldController, loginScene, TWSWorld.getScene().getWindow(),
                                                   Modality.APPLICATION_MODAL);
         newStage.showAndWait();
         refreshWorldTable();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @FXML
   public void worldTableClicked(MouseEvent event) {
      if (event.getButton().equals(MouseButton.PRIMARY)) {
         if (event.getClickCount() == 2) {
            selectedWorld = TWSWorld.getSelectionModel().getSelectedItem();
            if (selectedWorld != null) {
               refreshArticleTable();
               TOpenedWorld.setDisable(false);
               TOpenedWorld.setText(selectedWorld.getName());
               TabPaneMainView.getSelectionModel().select(TOpenedWorld);
               app.setCurrentWorld(selectedWorld);
            }
         }
      }
   }

   @FXML
   public void addArticle(ActionEvent event) {
      try {
         FXMLLoader createArticleLoader = new FXMLLoader();
         createArticleLoader.setLocation(getClass().getClassLoader().getResource("views/createArticle.fxml"));
         Scene createArticleScene = new Scene(createArticleLoader.load());
         CreateArticleController createArticleController = createArticleLoader.getController();
         createArticleController.setApp(app);
         Stage newStage =
                 Util.createNewWindowOver(createArticleController, createArticleScene, TWSWorld.getScene().getWindow(),
                                          Modality.APPLICATION_MODAL);
         newStage.showAndWait();
         refreshArticleTable();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void refreshWorldTable() {
      TWSWorld.getItems().clear();
      TWSWorld.setItems(FXCollections.observableList(app.getWorlds().toList()));
   }

   public void refreshArticleTable() {
      TOWArticles.getItems().clear();
      TOWArticles.setItems(FXCollections.observableList(selectedWorld.getArticles().toList()));
   }
}
