/*
 * @File CreateArticleController.java
 * @Authors : David González León
 * @Date 5 févr. 2021
 */
package ui.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui.Controller;
import world.app.App;
import world.app.world.Article;
import world.app.world.articles.TypeOfArticle;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateArticleController extends Controller {
   private Scene scene;
   private App app;


   @FXML private TextFlow TFWelcomeMessage;
   @FXML private ComboBox<TypeOfArticle> CBArticleType;

   @FXML private GridPane GPArticle;
   @FXML private TextField TNameArticle;
   @FXML private TextField TDescriptionArticle;
   private ArrayList<TextField> additionalTextFields = new ArrayList<>();
   private ArrayList<ComboBox<Article>> additionalComboBoxes = new ArrayList<>();
   private ArrayList<Node> additionalNodes = new ArrayList<>();

   @FXML private Button BCancelArticle;
   @FXML private Button BCreateArticle;

   private TypeOfArticle typeOfArticle;

   public void setApp(App app) {
      this.app = app;
   }

   @Override
   public void load(Stage primaryStage) {
      CBArticleType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
         removeContentFromArticleType();
         this.typeOfArticle = newValue;
         setContentToArticleType(newValue);
      });
      CBArticleType.setItems(FXCollections.observableList(Arrays.asList(TypeOfArticle.values())));
   }

   @Override
   public void setScene(Scene scene) {
      this.scene = scene;
   }

   @FXML
   public void clickedCancel(ActionEvent event) {
      ((Stage)GPArticle.getScene().getWindow()).close();
   }

   @FXML
   public void clickedCreateArticle(ActionEvent event) {

   }

   private void setContentToArticleType(TypeOfArticle articleType) {
      switch (articleType) {
         case ACCORD:
            GPArticle.add(createLabel("Accord Type"), 1, 2);
            GPArticle.add(createComboBox(app.getArticlesByArticleType(TypeOfArticle.ACCORD_TYPE)), 2, 2);
            GPArticle.add(createLabel("Beginning Date"), 1, 3);
            GPArticle.add(createTextField("Beginning Date"), 2, 3);
            GPArticle.add(createLabel("End Date"), 1, 4);
            GPArticle.add(createTextField("End Date"), 2, 4);
            setConstraintsForRowsCreated(3);
            break;
         case ALPHABET:
            GPArticle.add(createLabel("Writing system"), 1, 2);
            GPArticle.add(createTextField("Writing System"), 2, 2);
            setConstraintsForRowsCreated(1);
            break;
         case CHARACTER:
            GPArticle.add(createLabel("Race"), 1, 2);
            GPArticle.add(createComboBox(app.getArticlesByArticleType(TypeOfArticle.RACE)), 2, 2);
            setConstraintsForRowsCreated(1);
            break;
         case COUNTRY:
            GPArticle.add(createLabel("Continent"), 1, 2);
            GPArticle.add(createComboBox(app.getArticlesByArticleType(TypeOfArticle.CONTINENT)), 2, 2);
            setConstraintsForRowsCreated(1);
            break;
         case LANGUAGE:
            GPArticle.add(createLabel("Language"), 1, 2);
            GPArticle.add(createComboBox(app.getArticlesByArticleType(TypeOfArticle.ALPHABET)), 2, 2);
            setConstraintsForRowsCreated(1);
            break;
         case RACE:
            GPArticle.add(createLabel("Language"), 1, 2);
            GPArticle.add(createComboBox(app.getArticlesByArticleType(TypeOfArticle.ALPHABET)), 2, 2);
            GPArticle.add(createLabel("Minimum Height"), 1, 3);
            GPArticle.add(createTextField("Minimum Height"), 2, 3);
            GPArticle.add(createLabel("Maximum Height"), 1, 4);
            GPArticle.add(createTextField("Maximum Height"), 2, 4);
            setConstraintsForRowsCreated(3);
            break;
         case RANK:
            GPArticle.add(createLabel("Country"), 1, 2);
            GPArticle.add(createComboBox(app.getArticlesByArticleType(TypeOfArticle.COUNTRY)), 2, 2);
            GPArticle.add(createLabel("Salary"), 1, 3);
            GPArticle.add(createTextField("Salary"), 2, 3);
            GPArticle.add(createLabel("Additional Rights"), 1, 4);
            GPArticle.add(createTextField("Additional Rights"), 2, 4);
            setConstraintsForRowsCreated(3);
            break;
         case SIDE:
            GPArticle.add(createLabel("Name"), 1, 2);
            GPArticle.add(createTextField("Name"), 2, 2);
            setConstraintsForRowsCreated(1);
            break;
         case WAR:
            GPArticle.add(createLabel("Side 1"), 1, 2);
            GPArticle.add(createComboBox(app.getArticlesByArticleType(TypeOfArticle.SIDE)), 2, 2);
            GPArticle.add(createLabel("Side 2"), 1, 3);
            GPArticle.add(createComboBox(app.getArticlesByArticleType(TypeOfArticle.SIDE)), 2, 3);
            GPArticle.add(createLabel("Beginning Date"), 1, 4);
            GPArticle.add(createTextField("Beginning Date"), 2, 4);
            GPArticle.add(createLabel("End Date"), 1, 5);
            GPArticle.add(createTextField("End Date"), 2, 5);
            setConstraintsForRowsCreated(4);
            break;
         default:
            break;
      }
   }

   private TextField createTextField(String label) {
      TextField textField = new TextField();
      textField.setPromptText(label);
      additionalTextFields.add(textField);
      return textField;
   }

   private Label createLabel(String name) {
      Label label = new Label(name);
      additionalNodes.add(label);
      return label;
   }

   private ComboBox<Article> createComboBox(ArrayList<Article> articles) {
      ComboBox<Article> comboBox = new ComboBox<>();
      comboBox.setConverter(new StringConverter<Article>() {
         @Override
         public String toString(Article object) {
            if (object != null) {
               return object.getName();
            }
            return null;
         }

         @Override
         public Article fromString(String string) {
            return null;
         }
      });
      comboBox.setItems(FXCollections.observableList(articles));
      additionalComboBoxes.add(comboBox);
      return comboBox;
   }

   private void setConstraintsForRowsCreated(int numberOfRowsCreated) {
      for (int i = 0; i < numberOfRowsCreated; ++i) {
         GPArticle.getRowConstraints().add(new RowConstraints(GPArticle.getRowConstraints().get(0).getMinHeight(),
                                                              GPArticle.getRowConstraints().get(0).getPrefHeight(),
                                                              GPArticle.getRowConstraints().get(0).getMaxHeight(),
                                                              Priority.ALWAYS, VPos.CENTER, false));
      }
   }

   private void removeContentFromArticleType() {
      for (TextField textField : additionalTextFields) {
         GPArticle.getChildren().remove(textField);
      }
      additionalTextFields.clear();
      for (Node node : additionalNodes) {
         GPArticle.getChildren().remove(node);
      }
      additionalNodes.clear();
      for (ComboBox<Article> comboBox : additionalComboBoxes) {
         GPArticle.getChildren().remove(comboBox);
      }
      additionalComboBoxes.clear();
      while (GPArticle.getRowConstraints().size() > 2) {
         GPArticle.getRowConstraints().remove(2);
      }
      System.out.println("Size : " + GPArticle.getRowConstraints().size());
   }
}
