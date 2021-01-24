/*
 * @File Controller.java
 * @Authors : David González León
 * @Date 17 janv. 2021
 */
package ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class Controller {

   /**
    * Loads the view associated with the controller, and refreshes/loads the data if needed
    *
    * @param primaryStage the stage to change the view
    */
   public abstract void load(Stage primaryStage);

   public abstract void setScene(Scene scene);
}
