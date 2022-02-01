/*
 * @File Util.java
 * @Authors : David González León
 * @Date 24 janv. 2021
 */
package ch.world.app.client.visual.util;

import ch.world.app.client.visual.Controller;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * This class contains only static functions that are used in this module.
 */
public final class Util {
   /**
    * Creates an alert with the specified alertType, title, header and content. The new window will just wait until it
    * is closed.
    *
    * @param alertType the type of alert
    * @param title     the title
    * @param header    the header
    * @param content   the content
    */
   public static void createAlertFrame(Alert.AlertType alertType, String title, String header, String content) {
      Alert alert = new Alert(alertType);
      alert.setTitle(title);
      alert.setHeaderText(header);
      alert.setContentText(content);
      ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("images/mainImage.jpg"));

      alert.showAndWait();
   }

   /**
    * Creates a new Windows over the given one, with the indicated modality. It shows a given scene using the load()
    * functions of the given Controller. This function does not call the show function on the stage it creates.
    *
    * @param controller     the controller of the scene that will be displayed
    * @param scene          the scene that will be displayed
    * @param originalWindow the window that will be the owner of the new stage.
    * @param modality       The modality of the new stage.
    *
    * @return the stage created
    */
   public static Stage createNewWindowOver(Controller controller, Scene scene, Window originalWindow,
                                           Modality modality) {
      controller.setScene(scene);
      Stage stage = new Stage();
      stage.setAlwaysOnTop(true);
      controller.load(stage);
      stage.initOwner(originalWindow);
      stage.initModality(modality);
      return stage;
   }
}
