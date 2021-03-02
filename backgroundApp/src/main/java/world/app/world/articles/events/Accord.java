/*
 * @File Accord.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles.events;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;
import world.app.world.articles.Country;
import world.app.world.articles.Event;
import world.app.world.articles.TypeOfArticle;

import java.sql.*;
import java.util.ArrayList;

public class Accord extends Event {
   private final HashMapChaining<Country> countries;
   private final ArrayList<Country> countriesDeleted = new ArrayList<>();
   private final ArrayList<Article> coutriesAdded = new ArrayList<>();
   private AccordType type;

   public Accord(int id, World world, String name, String content, Date lastUpdate, String startDate, String endDate,
                 AccordType type, HashMapChaining<Country> countries) {
      super(id, world, name, content, lastUpdate, startDate, endDate, TypeOfArticle.ACCORD);
      this.type = type;
      this.countries = countries;
      for (Country c : countries) {
         c.addAccord(this);
      }
      type.addAccord(this);
   }

   public static void loadAccordIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.accord a2 ON a.id = a2.idArticle WHERE" +
              " a.id = ? AND a.idWorld=?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         statement = world.getUser().getConnection().prepareStatement(
                 "SELECT ac.idCountry FROM worldproject.accord a INNER JOIN worldproject.accord_country ac ON a" +
                 ".idArticle = ac.idAccord WHERE ac.idAccord=?");
         statement.setInt(1, resultSet.getInt("idArticle"));
         ResultSet countryResults = statement.executeQuery();
         HashMapChaining<Country> countries = new HashMapChaining<>();
         while (countryResults.next()) {
            countries.add((Country) world.getArticleWithId(countryResults.getInt("idCountry")));
         }
         articles.add(new Accord(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                 resultSet.getString("content"), resultSet.getDate("last_update"),
                                 resultSet.getString("dateBeginning"), resultSet.getString("dateEnd"),
                                 (AccordType) world.getArticleWithId(resultSet.getInt("idAccordType")), countries));
      }
   }

   public static int createAccord(World world, String name, String content, int idAccordType, String dateBeginning,
                                     String dateEnd) throws SQLException {
      int id;
      try {
          id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO worldproject.accord(idarticle, idaccordtype, datebeginning, dateend) VALUES (?,?,?,?)");
         statement.setInt(1, id);
         statement.setInt(2, idAccordType);
         statement.setString(3, dateBeginning);
         statement.setString(4, dateEnd);
         statement.execute();
      } catch (SQLException throwables) {
         throwables.printStackTrace();
         System.err.println("Error while adding article to database");
         throw throwables;
      }
      return id;
   }

   @Override
   public boolean endModifications() {
      if (isUpdateNeeded()) {
         try {
            Connection connection = getWorld().getUser().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE worldproject.accord SET idaccordtype = ?, datebeginning = ?, dateend = ? WHERE idarticle " +
                    "= ?");
            statement.setInt(1, type.getId());
            statement.setString(2, getStartDate());
            statement.setString(3, getEndDate());
            statement.setInt(4, getId());
            statement.execute();
            if (!coutriesAdded.isEmpty()) {
               String query = "INSERT INTO worldproject.accord_country(idaccord, idcountry) VALUES ";
               addTuples(connection, query, getId(), coutriesAdded);
               coutriesAdded.clear();
            }
            if (!countriesDeleted.isEmpty()) {
               for (Country country : countriesDeleted) {
                  deleteTuple(connection, "DELETE FROM worldproject.accord_country WHERE idaccord=? AND idcountry = ?",
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

   @Override
   public String toString() {
      return super.toString() + ", type : " + type + '}';
   }

   public AccordType getType() {
      return type;
   }

   public void setType(AccordType type) {
      if (type != null && isInModification() && !type.equals(this.type)) {
         type.removeAccord(this);
         this.type = type;
         type.addAccord(this);
         setUpdateIsNeeded();
      }
   }

   public HashMapChaining<Country> getCountries() {
      return countries;
   }

   public void addCountry(Country country) {
      if (country != null && isInModification() && !countries.contains(country)) {
         countries.add(country);
         country.addAccord(this);
         coutriesAdded.add(country);
         countriesDeleted.remove(country);
         setUpdateIsNeeded();
      }
   }

   public void removeCountry(Country country) {
      if (country != null && isInModification() && countries.contains(country)) {
         country.removeAccord(this);
         if (countries.erase(country)) {
            countriesDeleted.add(country);
            coutriesAdded.remove(country);
            setUpdateIsNeeded();
         }
      }
   }
}
