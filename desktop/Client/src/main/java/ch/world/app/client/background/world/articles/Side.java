/*
 * @File Side.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package ch.world.app.client.background.world.articles;


import ch.world.app.client.background.util.HashMapChaining;
import ch.world.app.client.background.world.Article;
import ch.world.app.client.background.world.World;

import java.sql.*;
import java.util.ArrayList;

public class Side extends Article {
   private final HashMapChaining<Country> countries;
   private final ArrayList<Country> countriesDeleted = new ArrayList<>();
   private final ArrayList<Article> coutriesAdded = new ArrayList<>();

   public Side(int id, World world, String name, String content, Date lastUpdate, HashMapChaining<Country> countries) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.SIDE);
      this.countries = countries;
      for (Country c : countries) {
         c.addSide(this);
      }
   }

   public static void loadSideIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.side s ON a.id = s.idArticle WHERE " +
              "a.id = ? AND a.idWorld = ?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         statement = world.getUser().getConnection().prepareStatement(
                 "SELECT cs.idCountry FROM worldproject.side s INNER JOIN worldproject.country_side cs ON s" +
                 ".idArticle = cs.idSide WHERE s.idArticle=?");
         statement.setInt(1, resultSet.getInt("idArticle"));
         ResultSet countryResults = statement.executeQuery();
         HashMapChaining<Country> countries = new HashMapChaining<>();
         while (countryResults.next()) {
            countries.add((Country) world.getArticleWithId(countryResults.getInt("idCountry")));
         }
         articles.add(new Side(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                               resultSet.getString("content"), resultSet.getDate("last_update"), countries));
      }
   }

   public static int createSide(World world, String name, String content) throws SQLException {
      int id;
      try {
          id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement =
                 connection.prepareStatement("INSERT INTO worldproject.side(idarticle) VALUES (?)");
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

   @Override
   public boolean endModifications() {
      if (isUpdateNeeded()) {
         try {
            Connection connection = getWorld().getUser().getConnection();
            if (!coutriesAdded.isEmpty()) {
               String query = "INSERT INTO worldproject.country_side(idside, idcountry) VALUES ";
               addTuples(connection, query, getId(), coutriesAdded);
               coutriesAdded.clear();
            }
            if (!countriesDeleted.isEmpty()) {
               for (Country country : countriesDeleted) {
                  deleteTuple(connection, "DELETE FROM worldproject.accord_country WHERE idside=? AND idcountry = ?",
                              getId(), country.getId());
               }
               countriesDeleted.clear();
            }
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Error while saving changes of article");
            return false;
         }
      }
      return super.endModifications();
   }

   public HashMapChaining<Country> getCountries() {
      return countries;
   }

   public void addCountry(Country country) {
      if (country != null && isInModification() && !countries.contains(country)) {
         countries.add(country);
         country.addSide(this);
         coutriesAdded.add(country);
         countriesDeleted.remove(country);
         setUpdateIsNeeded();
      }
   }

   public void removeCountry(Country country) {
      if (country != null && isInModification() && countries.contains(country)) {
         country.removeSide(this);
         if (countries.erase(country)) {
            countriesDeleted.add(country);
            coutriesAdded.remove(country);
            setUpdateIsNeeded();
         }
      }
   }
}
