/*
 * @File Logging.java
 * @Authors : David González León
 * @Date 21 mars 2021
 */
package ch.world.app.server.util;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class with useful functions for logging
 */
public class Logging {

   /**
    * Gets the current path.
    *
    * @return the current path
    */
   public static String getCurrentPath() {
      try {
         return new File(".").getCanonicalPath();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Gets a setupped logger.
    *
    * @param className the class name to give to the logger
    * @param path      the path to store the output of the logger
    *
    * @return the setupped logger
    */
   public static Logger getSetuppedLogger(String className, String path) {
      if (className == null || path == null) {
         throw new IllegalArgumentException();
      }
      Logger logger = Logger.getLogger(className);
      try {
         logger.setLevel(Level.FINER);
         logger.addHandler(new FileHandler(path));
      } catch (IOException e) {
         logger.throwing(className, "getSetuppedLogger", e);
         e.printStackTrace();
      }
      return logger;
   }

   /**
    * Logs server socket address, and other information
    *
    * @param logger the logger to use
    * @param socket the socket of the server
    */
   public static void logServerSocketAddress(Logger logger, ServerSocket socket) {
      logger.log(Level.INFO, "       Local IP address: {0}", new Object[]{socket.getLocalSocketAddress()});
      logger.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(socket.getLocalPort())});
      logger.log(Level.INFO, "               is bound: {0}", new Object[]{socket.isBound()});
   }

   /**
    * Logs client scockets address and other info
    *
    * @param logger       the logger to use
    * @param clientSocket the client socket that we want to log
    */
   public static void logSocketAddress(Logger logger, Socket clientSocket) {
      logger.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
      logger.log(Level.INFO, "             Local port: {0}",
                 new Object[]{Integer.toString(clientSocket.getLocalPort())});
      logger.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
      logger.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
   }
}
