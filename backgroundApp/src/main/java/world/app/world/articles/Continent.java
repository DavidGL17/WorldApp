/*
 * @File Continent.java
 * @Authors : David González León
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;

import java.sql.*;

public class Continent extends Article {
   private final HashMapChaining<Country> countries;

   public Continent(int id, World world, String name, String content, Date lastUpdate,
                    HashMapChaining<Country> countries) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.CONTINENT);
      this.countries = countries;
   }

   public static void loadContinentIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.continent c ON a.id = c.idArticle " +
              "WHERE a.id = ? AND a.idWorld = ?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         articles.add(new Continent(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                    resultSet.getString("content"), resultSet.getDate("last_update"),
                                    new HashMapChaining<>()));
      }
   }

   public static int createContinent(World world, String name, String content) throws SQLException {
      int id;
      try {
         id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement =
                 connection.prepareStatement("INSERT INTO worldproject.continent(idarticle) VALUES (?)");
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

   protected void addCountry(Country country) {
      if (country == null) {
         throw new NullPointerException();
      }
      if (country.getContinent() != this) {
         return;
      }
      countries.add(country);
   }

   protected void removeCountry(Country country) {
      if (country == null) {
         throw new NullPointerException();
      }
      if (country.getContinent() != this) {
         return;
      }
      countries.erase(country);
   }

}
