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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui.Controller;
import ui.util.Util;
import world.app.App;
import world.app.world.Article;
import world.app.world.articles.Character;
import world.app.world.articles.*;
import world.app.world.articles.events.Accord;
import world.app.world.articles.events.AccordType;
import world.app.world.articles.events.War;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateArticleController extends Controller {
   private Scene scene;
   private App app;


   @FXML private TextFlow TFWelcomeMessage;
   private final Text welcomeText;
   private final Text warningText = new Text();

   @FXML private ComboBox<TypeOfArticle> CBArticleType;

   @FXML private GridPane GPArticle;
   @FXML private TextField TNameArticle;
   @FXML private TextField TDescriptionArticle;
   private final ArrayList<TextField> additionalTextFields = new ArrayList<>();
   private final ArrayList<ComboBox<Article>> additionalComboBoxes = new ArrayList<>();
   private final ArrayList<Node> additionalNodes = new ArrayList<>();


   private TypeOfArticle typeOfArticle;

   public CreateArticleController() {
      welcomeText = new Text("Please choose the type of article first, and then fill the fields that will appear.");
      welcomeText.setFont(Font.font(16));
      warningText.setFont(Font.font(14));
   }

   public void setApp(App app) {
      this.app = app;
   }

   @Override
   public void load(Stage primaryStage) {
      TFWelcomeMessage.getChildren().add(welcomeText);
      TFWelcomeMessage.getChildren().add(warningText);
      TFWelcomeMessage.setTextAlignment(TextAlignment.CENTER);

      CBArticleType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
         removeContentFromArticleType();
         this.typeOfArticle = newValue;
         setContentToArticleType(newValue);
      });
      CBArticleType.setItems(FXCollections.observableList(Arrays.asList(TypeOfArticle.values())));
      primaryStage.setScene(scene);
   }

   @Override
   public void setScene(Scene scene) {
      this.scene = scene;
   }

   @FXML
   public void clickedCancel(ActionEvent event) {
      ((Stage) GPArticle.getScene().getWindow()).close();
   }

   @FXML
   public void clickedCreateArticle(ActionEvent event) {
      Article article = null;
      try {
         if (TNameArticle.getText().isBlank() || TDescriptionArticle.getText().isBlank()) {
            throw new IllegalArgumentException("Fill all fields, and don't fill them with blank spaces alone.");
         }
         switch (typeOfArticle) {
            case ACCORD:
               if (additionalComboBoxes.get(0).getSelectionModel().getSelectedItem() == null ||
                   additionalTextFields.get(0).getText().isBlank() || additionalTextFields.get(1).getText().isBlank()) {
                  throw new IllegalArgumentException("Fill all fields, and don't fill them with blank spaces alone. " +
                                                     "If you have then check you have correctly chosen an accord type" +
                                                     " from the drop down list. (If there are none, try creating an " +
                                                     "accord type first and then create an accord)");
               }
               article =
                       Accord.createAccord(app.getCurrentWorld(), TNameArticle.getText(), TDescriptionArticle.getText(),
                                           additionalComboBoxes.get(0).getSelectionModel().getSelectedItem().getId(),
                                           additionalTextFields.get(0).getText(),
                                           additionalTextFields.get(1).getText());
               break;
            case ACCORD_TYPE:
               article = AccordType.createAccordType(app.getCurrentWorld(), TNameArticle.getText(),
                                                     TDescriptionArticle.getText());
               break;
            case ALPHABET:
               if (additionalTextFields.get(0).getText().isBlank()) {
                  throw new IllegalArgumentException("Fill all fields, and don't fill them with blank spaces alone.");
               }
               article = Alphabet.createAlphabet(app.getCurrentWorld(), TNameArticle.getText(),
                                                 TDescriptionArticle.getText(), additionalTextFields.get(0).getText());
               break;
            case CHARACTER:
               if (additionalComboBoxes.get(0).getSelectionModel().getSelectedItem() == null) {
                  throw new IllegalArgumentException("Check you have correctly chosen a race from the drop down list." +
                                                     " (If there are none, try creating a race first and then create " +
                                                     "a character)");
               }
               article = Character.createCharacter(app.getCurrentWorld(), TNameArticle.getText(),
                                                   TDescriptionArticle.getText(),
                                                   additionalComboBoxes.get(0).getSelectionModel().getSelectedItem()
                                                                       .getId());
               break;
            case CONTINENT:
               article = Continent.createContinent(app.getCurrentWorld(), TNameArticle.getText(),
                                                   TDescriptionArticle.getText());
               break;
            case COUNTRY:
               if (additionalComboBoxes.get(0).getSelectionModel().getSelectedItem() == null) {
                  throw new IllegalArgumentException(
                          "Check you have correctly chosen a continent from the drop down list. (If there are none " +
                          "available, try creating a continent first and then create a country)");
               }
               article = Country.createCountry(app.getCurrentWorld(), TNameArticle.getText(),
                                               TDescriptionArticle.getText(),
                                               additionalComboBoxes.get(0).getSelectionModel().getSelectedItem()
                                                                   .getId());
               break;
            case LANGUAGE:
               if (additionalComboBoxes.get(0).getSelectionModel().getSelectedItem() == null) {
                  throw new IllegalArgumentException(
                          "Check you have correctly chosen an alphabet from the drop down list. (If there are none " +
                          "available, try creating a alphabet first and then create a language)");
               }
               article = Language.createLanguage(app.getCurrentWorld(), TNameArticle.getText(),
                                                 TDescriptionArticle.getText(),
                                                 additionalComboBoxes.get(0).getSelectionModel().getSelectedItem()
                                                                     .getId());
               break;
            case LEGEND:
               article = Legend.createLegend(app.getCurrentWorld(), TNameArticle.getText(),
                                             TDescriptionArticle.getText());
               break;
            case RACE:
               try {
                  if (additionalComboBoxes.get(0).getSelectionModel().getSelectedItem() == null ||
                      additionalTextFields.get(0).getText().isBlank() ||
                      additionalTextFields.get(1).getText().isBlank()) {
                     throw new IllegalArgumentException(
                             "Fill all fields, and don't fill them with blank spaces alone. If you have then check " +
                             "you have correctly chosen a language from the drop down list. (If there are none " +
                             "available, try creating a language first and then create a race)");
                  }
                  if (Integer.parseInt(additionalTextFields.get(0).getText()) >=
                      Integer.parseInt(additionalTextFields.get(1).getText())) {
                     throw new IllegalArgumentException(
                             "The minimum height can't be bigger or equal to the maximum height.");
                  }
               } catch (NumberFormatException exception) {
                  throw new IllegalArgumentException(
                          "The values given for the minimum and maximum heights must both be numbers.");
               }
               article = Race.createRace(app.getCurrentWorld(), TNameArticle.getText(), TDescriptionArticle.getText(),
                                         additionalComboBoxes.get(0).getSelectionModel().getSelectedItem().getId(),
                                         Integer.parseInt(additionalTextFields.get(0).getText()),
                                         Integer.parseInt(additionalTextFields.get(1).getText()));
               break;
            case RANK:
               try {
                  if (additionalComboBoxes.get(0).getSelectionModel().getSelectedItem() == null ||
                      additionalTextFields.get(0).getText().isBlank() ||
                      additionalTextFields.get(1).getText().isBlank()) {
                     throw new IllegalArgumentException(
                             "Fill all fields, and don't fill them with blank spaces alone. If you have then check " +
                             "you have correctly chosen a country from the drop down list. (If there are none " +
                             "available, try creating a country first and then create a rank)");
                  }
                  if (Double.parseDouble(additionalTextFields.get(0).getText()) < 0) {
                     throw new IllegalArgumentException("The value given for the salary cannot be smaller than zero.");
                  }
               } catch (NumberFormatException exception) {
                  throw new IllegalArgumentException("The value given for the salary must be a number.");
               }
               article = Rank.createRank(app.getCurrentWorld(), TNameArticle.getText(), TDescriptionArticle.getText(),
                                         additionalComboBoxes.get(0).getSelectionModel().getSelectedItem().getId(),
                                         Double.parseDouble(additionalTextFields.get(0).getText()),
                                         additionalTextFields.get(1).getText());
               break;
            case SIDE:
               article = Side.createSide(app.getCurrentWorld(), TNameArticle.getText(), TDescriptionArticle.getText());
               break;
            case WAR:
               try {
                  if (additionalComboBoxes.get(0).getSelectionModel().getSelectedItem() == null ||
                      additionalComboBoxes.get(1).getSelectionModel().getSelectedItem() == null ||
                      additionalTextFields.get(0).getText().isBlank() ||
                      additionalTextFields.get(1).getText().isBlank() ||
                      additionalTextFields.get(2).getText().isBlank()) {
                     throw new IllegalArgumentException(
                             "Fill all fields, and don't fill them with blank spaces alone. If you have then check " +
                             "you have correctly chosen Sides from the drop down lists. (If there are none " +
                             "available, try creating two Sides first and then create a war)");
                  }
                  if (Integer.parseInt(additionalTextFields.get(2).getText()) < 0) {
                     throw new IllegalArgumentException("The death count can't be a negative number.");
                  }
                  if (additionalComboBoxes.get(0).getSelectionModel().getSelectedItem()
                                          .equals(additionalComboBoxes.get(1).getSelectionModel().getSelectedItem())) {
                     throw new IllegalArgumentException("The two sides of the war must be different.");
                  }
               } catch (NumberFormatException exception) {
                  throw new IllegalArgumentException("The value given for the death count needs to be a number.");
               }
               article = War.createWar(app.getCurrentWorld(), TNameArticle.getText(), TDescriptionArticle.getText(),
                                       additionalComboBoxes.get(0).getSelectionModel().getSelectedItem().getId(),
                                       additionalComboBoxes.get(1).getSelectionModel().getSelectedItem().getId(),
                                       additionalTextFields.get(0).getText(), additionalTextFields.get(1).getText(),
                                       Integer.parseInt(additionalTextFields.get(2).getText()));
               break;
            default:
               break;
         }
         app.addArticle(article);
         //((Stage) TNameArticle.getScene().getWindow()).close();
      } catch (SQLException exception) {
         Util.createAlertFrame(Alert.AlertType.ERROR, "Error in article creation", "Error in article creation",
                               "There was an error while creating your Article, please check you are correctly " +
                               "connected to the Internet and try again. If the problem persists, please contact " +
                               "support.");
      } catch (IllegalArgumentException exception) {
         warningText.setText("\nThere were some issues with the values given.\n" + exception.getMessage());
      }
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
      comboBox.setConverter(new StringConverter<>() {
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
   }
}
