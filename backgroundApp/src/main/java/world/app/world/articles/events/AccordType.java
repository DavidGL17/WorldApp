/*
 * @File AccordType.java
 * @Authors : David González León,
 * @Date 10 janv. 2021
 */
package world.app.world.articles.events;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;
import world.app.world.articles.TypeOfArticle;

import java.sql.*;

public class AccordType extends Article {
   private final HashMapChaining<Accord> accords;

   public AccordType(int id, World world, String name, String content, Date lastUpdate) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.ACCORD_TYPE);
      accords = new HashMapChaining<>();
   }

   public static void loadArticleTypeIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.accordtype a2 ON a.id = a2.idArticle " +
              "WHERE a.id = ? AND a.idWorld=?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         articles.add(new AccordType(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                     resultSet.getString("content"), resultSet.getDate("last_update")));
      }
   }

   public static int createAccordType(World world, String name, String content) throws SQLException {
      int id;
      try {
         id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement =
                 connection.prepareStatement("INSERT INTO worldproject.accordtype(idarticle) VALUES (?)");
         statement.setInt(1, id);
         statement.execute();
      } catch (SQLException throwables) {
         throwables.printStackTrace();
         System.err.println("Error while adding article to database");
         throw throwables;
      }
      return id;
   }

   @Override
   public String toString() {
      return super.toString() + "}";
   }

   protected void addAccord(Accord accord) {
      if (accord == null) {
         throw new NullPointerException();
      }
      if (accord.getType() != this) {
         return;
      }
      accords.add(accord);
   }

   protected void removeAccord(Accord accord) {
      if (accord == null) {
         throw new NullPointerException();
      }
      if (accord.getType() != this) {
         return;
      }
      accords.erase(accord);
   }
}
