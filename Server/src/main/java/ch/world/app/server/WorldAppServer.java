/*
 * @File WorldAppServer.java
 * @Authors : David González León
 * @Date 25 mars 2021
 */
package ch.world.app.server;

import ch.world.app.server.util.Logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class WorldAppServer {
   private final Logger logger;

   public WorldAppServer() throws IOException {
      try {
         File logFile = new File(new File(".").getCanonicalPath()+"/logs/log.log");
         logFile.getParentFile().mkdirs();
         logFile.createNewFile();
         logger = Logging.getSetuppedLogger(Main.class.getName(), Logging.getCurrentPath() + "/logs/log.log");
      } catch (IOException e) {
         e.printStackTrace();
         System.out.println("Error while launching the logger, closing");
         throw new IOException();
      }
   }

   public void start() {
      logger.info("Starting the server...");

   }
}
