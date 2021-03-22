/*
 * @File Main.java
 * @Authors : David González León
 * @Date 17 mars 2021
 */
package ch.world.app.server;

import ch.world.app.server.util.Logging;

import java.io.File;
import java.util.logging.Logger;

public class Main {
   private static final Logger logger =
           Logging.getSetuppedLogger(Main.class.getName(), Logging.getCurrentPath() + "/logs/log.log");

   public static void main(String[] args) {
      File logDirectory = new File(Logging.getCurrentPath()+"/Server/logs");
      if (!logDirectory.exists()){
         logDirectory.mkdirs();
      }
      logger.info("This is a test");

   }
}
