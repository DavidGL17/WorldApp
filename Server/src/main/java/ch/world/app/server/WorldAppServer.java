/*
 * @File WorldAppServer.java
 * @Authors : David González León
 * @Date 25 mars 2021
 */
package ch.world.app.server;

import ch.world.app.util.Logging;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldAppServer {
   private final Logger logger;
   private final List<Worker> activeWorkers;

   public WorldAppServer() throws IOException {
      //Setup Logger
      try {
         File logFile = new File(new File(".").getCanonicalPath() + "/logs/log.log");
         logFile.getParentFile().mkdirs();
         logFile.createNewFile();
         logger = Logging.getSetuppedLogger(Main.class.getName(), Logging.getCurrentPath() + "/logs/log.log");
      } catch (IOException e) {
         e.printStackTrace();
         System.out.println("Error while launching the logger, closing");
         throw new IOException();
      }

      this.activeWorkers = Collections.synchronizedList(new LinkedList<>());
   }

   public void start() {
      logger.info("Starting the server...");

   }

   private void registerWorker(Worker worker) {
      logger.log(Level.INFO, ">> Waiting for lock before registring worker {0}", worker.userId);
      activeWorkers.add(worker);
      logger.log(Level.INFO, "<< Worker {0} registered.", worker.userId);
   }

   private void unregisterWorker(Worker worker) {
      logger.log(Level.INFO, ">> Waiting for lock before unregistring worker {0}", worker.userId);
      activeWorkers.remove(worker);
      logger.log(Level.INFO, "<< Worker {0} unregistered.", worker.userId);
   }

   class Worker implements Runnable {

      private int userId;

      @Override
      public void run() {

      }
   }
}
