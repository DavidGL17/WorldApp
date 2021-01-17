/*
 * @File App.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app;

import world.app.user.User;
import world.app.world.Article;
import world.app.world.World;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

//Le launcher fournira toutes les méthodes nécessaire a l'interface graphique
//TODO le laucncher est construit, puis lancé avec une méthode start qui lui donne son utilisateur. Si l'id de
// l'utilisateur
// apssé en paramètre est 0, il demande la création d'un user
public class App {
   private User user;

   public App(int userId) {

      try {
         Connection connection = DriverManager.getConnection(Util.connectionString);
         user = new User(userId, connection);
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
   }

   public App(String firstName, String lastName, String email) {
      try {
         Connection connection = DriverManager.getConnection(Util.connectionString);
         int userId = addUser(connection, firstName, lastName, email);
         user = new User(userId, connection);
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
   }

   public static void main(String[] args) {
      App app = new App(1);
      ArrayList<World> worlds = app.getWorlds();
      for (World w : worlds) {
         System.out.println(w);
         w.loadArticles();
         for (Article a : w.getArticles()) {
            System.out.println(a);
         }
      }
   }

   public ArrayList<World> getWorlds() {
      return user.getWorlds();
   }

   public void addWorld(String name) {
      user.createWorld(name);
   }

   private int addUser(Connection connection, String firstName, String lastName, String email) {
      return User.createUser(connection, firstName, lastName, email);
   }

}
