/*
 * @File Main.java
 * @Authors : David González León
 * @Date 17 mars 2021
 */
package ch.world.app.server;

import java.io.IOException;

public class Main {

   public static void main(String[] args) {
      WorldAppServer server;
      try {
         server = new WorldAppServer();
      } catch (IOException e) {
         e.printStackTrace();
         return;
      }

      server.start();
   }
}
