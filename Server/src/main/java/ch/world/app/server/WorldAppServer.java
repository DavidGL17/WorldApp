/*
 * @File WorldAppServer.java
 * @Authors : David González León
 * @Date 25 mars 2021
 */
package ch.world.app.server;

import ch.world.app.util.Logging;
import ch.world.app.util.protocol.WorldAppProtocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldAppServer {
   private final Logger logger;
   private final List<Worker> activeWorkers;
   private ServerSocket serverSocket;
   private boolean shouldRun = true;

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
      try {
         logger.log(Level.INFO, "Starting the server on port {0}...", WorldAppProtocol.DEFAULT_PORT);
         serverSocket = new ServerSocket(WorldAppProtocol.DEFAULT_PORT);
         while (shouldRun) {
            Socket clientSocket = serverSocket.accept();
            Worker worker = new Worker(clientSocket);
            registerWorker(worker);
            new Thread(worker).start();
         }
         serverSocket.close();
         logger.info("shouldRun is false... server going down");

      } catch (IOException e) {
         logger.log(Level.SEVERE, e.getMessage(), e);
         e.printStackTrace();
         System.exit(-1);
      }
   }

   private void shutdown() {
      logger.info("Shutting down server...");
      shouldRun = false;
      try {
         serverSocket.close();
      } catch (IOException ex) {
         logger.log(Level.SEVERE, ex.getMessage(), ex);
      }
      disconnectConnectedWorkers();
   }

   private void registerWorker(Worker worker) {
      logger.log(Level.INFO, ">> Waiting for lock before registring new worker...");
      activeWorkers.add(worker);
      logger.log(Level.INFO, "<< New Worker registered.");
   }

   private void unregisterWorker(Worker worker) {
      logger.log(Level.INFO, ">> Waiting for lock before unregistring worker {0}", worker.userId);
      activeWorkers.remove(worker);
      logger.log(Level.INFO, "<< Worker {0} unregistered.", worker.userId);
   }

   private void disconnectConnectedWorkers() {
      logger.info(">> Waiting for lock before disconnecting workers");
      synchronized (activeWorkers) {
         logger.info("Disconnecting workers");
         for (Worker worker : activeWorkers) {
            worker.disconnect();
         }
      }
      logger.info("<< Workers disconnected");
   }

   class Worker implements Runnable {

      private final Socket userSocket;
      private BufferedReader in;
      private PrintWriter out;
      private int userId;
      private boolean connected = true;

      Worker(Socket userSocket) {
         this.userSocket = userSocket;
         try {
            in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
            out = new PrintWriter(userSocket.getOutputStream());
            connected = true;
         } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
         }
      }

      @Override
      public void run() {

         while (connected) {

         }
      }

      private void cleanup() {
         logger.log(Level.INFO, "Cleaning up worker used by {0}", userId);

         logger.log(Level.INFO, "Closing clientSocket used by {0}", userId);
         try {
            if (userSocket != null) {
               userSocket.close();
            }
         } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
         }

         logger.log(Level.INFO, "Closing in used by {0}", userId);
         try {
            if (in != null) {
               in.close();
            }
         } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
         }

         logger.log(Level.INFO, "Closing out used by {0}", userId);
         if (out != null) {
            out.close();
         }

         logger.log(Level.INFO, "Clean up done for worker used by {0}", userId);
      }

      private void disconnect() {
         logger.log(Level.INFO, "Disconnecting worker used by {0}", userId);
         connected = false;
         cleanup();
      }
   }
}
