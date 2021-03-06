/*
 * @File App.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package ch.world.app.client.background;


import ch.world.app.client.background.user.User;
import ch.world.app.client.background.util.HashMapChaining;
import ch.world.app.client.background.util.Util;
import ch.world.app.client.background.world.Article;
import ch.world.app.client.background.world.World;
import ch.world.app.client.background.world.articles.TypeOfArticle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

//Le launcher fournira toutes les méthodes nécessaire a l'interface graphique

public class App {
   private User user;
   private World currentWorld;

   public App(int userId, String password)
           throws User.WrongPasswordException, User.UserIdNotFoundException, SQLException {
      Connection connection = DriverManager.getConnection(Util.string);
      user = new User(userId, password, connection);
   }

   public App(String firstName, String lastName, String email, String password)
           throws User.WrongPasswordException, User.UserIdNotFoundException, SQLException {
      Connection connection = DriverManager.getConnection(Util.string);
      int userId = User.createUser(connection, firstName, lastName, email, password);
      user = new User(userId, password, connection);
   }

   public User getUser() {
      return user;
   }

   /*public static void main(String[] args)
           throws User.UserIdNotFoundException, User.WrongPasswordException, SQLException {
      App app = new App(1, "1234");
      HashMapChaining<World> worlds = app.getWorlds();
      for (World w : worlds) {
         System.out.println(w);
         w.loadArticles();
         for (Article a : w.getArticles()) {
            System.out.println(a);
         }
      }
   }*/

   public HashMapChaining<World> getWorlds() {
      return user.getWorlds();
   }

   public void addWorld(String name, String description) throws SQLException {
      World.loadWorldIntoUser(user,World.createWorld(user, name, description));
   }

   public ArrayList<Article> getArticlesByArticleType(TypeOfArticle articleType) {
      ArrayList<Article> articles = new ArrayList<>();
      for (Article article : currentWorld.getArticles()) {
         if (article.getTypeOfArticle() == articleType) {
            articles.add(article);
         }
      }
      return articles;
   }

   public World getCurrentWorld() {
      return currentWorld;
   }

   public void setCurrentWorld(World currentWorld) {
      this.currentWorld = currentWorld;
      currentWorld.loadArticles();
   }

   public void addArticle(Article article) {
      currentWorld.getArticles().add(article);
   }
}
