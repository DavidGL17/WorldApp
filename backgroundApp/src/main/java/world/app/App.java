/*
 * @File App.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app;

import util.HashMapChaining;
import world.app.user.User;
import world.app.world.Article;
import world.app.world.World;
import world.app.world.articles.TypeOfArticle;

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
      Connection connection = DriverManager.getConnection(Util.connectionString);
      user = new User(userId, password, connection);
   }

   public App(String firstName, String lastName, String email, String password)
           throws User.WrongPasswordException, User.UserIdNotFoundException, SQLException {
      Connection connection = DriverManager.getConnection(Util.connectionString);
      int userId = User.createUser(connection, firstName, lastName, email, password);
      user = new User(userId, password, connection);
   }

   public static void main(String[] args)
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
   }

   public HashMapChaining<World> getWorlds() {
      return user.getWorlds();
   }

   public void addWorld(String name) {
      user.createWorld(name);
   }

   public void setCurrentWorld(World currentWorld) {
      this.currentWorld = currentWorld;
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

   public World getCurrentWorld(){
      return currentWorld;
   }

   public void addArticle(Article article){
      currentWorld.getArticles().add(article);
   }
}
