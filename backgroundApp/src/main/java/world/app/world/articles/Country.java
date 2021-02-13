/*
 * @File Country.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;
import world.app.world.articles.events.Accord;

import java.sql.*;
import java.util.ArrayList;

public class Country extends Article {
   private final HashMapChaining<Rank> ranks;
   private final HashMapChaining<Race> races;
   private final ArrayList<Article> racesAdded = new ArrayList<>();
   private final ArrayList<Race> racesDeleted = new ArrayList<>();
   private final HashMapChaining<Side> sidesInvolvedIn;
   private final HashMapChaining<Accord> accords;
   private Continent continent;

   public Country(int id, World world, String name, String content, Date lastUpdate, Continent continent,
                  HashMapChaining<Rank> ranks, HashMapChaining<Race> races, HashMapChaining<Side> sidesInvolvedIn,
                  HashMapChaining<Accord> accords) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.COUNTRY);
      this.continent = continent;
      this.ranks = ranks;
      this.races = races;
      this.sidesInvolvedIn = sidesInvolvedIn;
      this.accords = accords;
      continent.addCountry(this);
   }

   public static void loadCountryIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.country c ON a.id = c.idArticle WHERE " +
              "a.id = ? AND a.idWorld=?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         statement = world.getUser().getConnection().prepareStatement(
                 "SELECT cr.idRace FROM worldproject.country c INNER JOIN worldproject.country_race cr ON c" +
                 ".idArticle = cr.idCountry WHERE c.idArticle=?");
         statement.setInt(1, resultSet.getInt("idArticle"));
         ResultSet racesResult = statement.executeQuery();
         HashMapChaining<Race> races = new HashMapChaining<>();
         while (racesResult.next()) {
            races.add((Race) world.getArticleWithId(racesResult.getInt("idRace")));
         }
         articles.add(new Country(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                  resultSet.getString("content"), resultSet.getDate("last_update"),
                                  (Continent) world.getArticleWithId(resultSet.getInt("idContinent")),
                                  new HashMapChaining<>(), races, new HashMapChaining<>(), new HashMapChaining<>()));
      }
   }

   public static int createCountry(World world, String name, String content, int idContinent) throws SQLException {
      int id;
      try {
         id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement =
                 connection.prepareStatement("INSERT INTO worldproject.country(idarticle, idcontinent) VALUES (?,?)");
         statement.setInt(1, id);
         statement.setInt(2, idContinent);
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
            PreparedStatement statement =
                    connection.prepareStatement("UPDATE worldproject.country SET idcontinent=? WHERE idarticle=?");
            statement.setInt(1, continent.getId());
            statement.setInt(2, getId());
            statement.execute();
            if (!racesAdded.isEmpty()) {
               String query = "INSERT INTO worldproject.country_race(idcountry, idrace) VALUES ";
               addTuples(connection, query, getId(), racesAdded);
               racesAdded.clear();
            }
            if (!racesDeleted.isEmpty()) {
               for (Race race : racesDeleted) {
                  deleteTuple(connection, "DELETE FROM worldproject.country_race WHERE idcountry=? AND idrace = ?",
                              getId(), race.getId());
               }
               racesDeleted.clear();
            }
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Error while saving changes of article");
            return false;
         }
      }
      return super.endModifications();
   }

   public Continent getContinent() {
      return continent;
   }

   public void setContinent(Continent continent) {
      if (continent != null && isInModification() && !continent.equals(this.continent)) {
         this.continent.removeCountry(this);
         this.continent = continent;
         continent.addCountry(this);
         setUpdateIsNeeded();
      }
   }

   public HashMapChaining<Rank> getRanks() {
      return ranks;
   }

   public HashMapChaining<Race> getRaces() {
      return races;
   }

   public void addRace(Race race) {
      if (race != null && isInModification()) {
         races.add(race);
         racesAdded.add(race);
         racesDeleted.remove(race);
         setUpdateIsNeeded();
      }
   }

   public void removeRace(Race race) {
      if (race != null && isInModification()) {
         if (races.erase(race)) {
            racesDeleted.add(race);
            racesAdded.remove(race);
            setUpdateIsNeeded();
         }
      }
   }

   public HashMapChaining<Side> getSidesInvolvedIn() {
      return sidesInvolvedIn;
   }

   public HashMapChaining<Accord> getAccords() {
      return accords;
   }

   public void addAccord(Accord accord) {
      if (accord == null) {
         throw new NullPointerException();
      }
      if (!accord.getCountries().contains(this)) {
         return;
      }
      accords.add(accord);
   }

   public void removeAccord(Accord accord) {
      if (accord == null) {
         throw new NullPointerException();
      }
      if (!accord.getCountries().contains(this)) {
         return;
      }
      accords.erase(accord);
   }

   protected void addSide(Side side) {
      if (side == null) {
         throw new NullPointerException();
      }
      if (!side.getCountries().contains(this)) {
         return;
      }
      sidesInvolvedIn.add(side);
   }

   protected void removeSide(Side side) {
      if (side == null) {
         throw new NullPointerException();
      }
      if (!side.getCountries().contains(this)) {
         return;
      }
      sidesInvolvedIn.erase(side);
   }

   protected void addRank(Rank rank) {
      if (rank == null) {
         throw new NullPointerException();
      }
      if (rank.getCountry() != this) {
         return;
      }
      ranks.add(rank);
   }

   protected void removeRank(Rank rank) {
      if (rank == null) {
         throw new NullPointerException();
      }
      if (rank.getCountry() != this) {
         return;
      }
      ranks.erase(rank);
   }
}

