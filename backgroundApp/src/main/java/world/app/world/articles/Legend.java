/*
 * @File Legend.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;

import java.sql.*;
import java.util.ArrayList;

public class Legend extends Article {
   private final HashMapChaining<Race> races;
   private final ArrayList<Article> racesAdded = new ArrayList<>();
   private final ArrayList<Article> racesDeleted = new ArrayList<>();

   public Legend(int id, World world, String name, String content, Date lastUpdate, HashMapChaining<Race> races) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.LEGEND);
      this.races = races;
   }

   public static void loadLegendIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.legend l ON a.id = l.idArticle WHERE " +
              "a.id = ? AND a.idWorld=?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         statement = world.getUser().getConnection().prepareStatement(
                 "SELECT lr.idRace FROM worldproject.legend l INNER JOIN worldproject.legend_race lr ON l" +
                 ".idArticle = lr.idLegend WHERE l.idArticle=?");
         statement.setInt(1, resultSet.getInt("id"));
         ResultSet racesResult = statement.executeQuery();
         HashMapChaining<Race> races = new HashMapChaining<>();
         while (racesResult.next()) {
            races.add((Race) world.getArticleWithId(racesResult.getInt(1)));
         }
         articles.add(new Legend(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                 resultSet.getString("content"), resultSet.getDate("last_update"), races));
      }
   }

   public static int createLegend(World world, String name, String content) throws SQLException {
      int id;
      try {
         id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement =
                 connection.prepareStatement("INSERT INTO worldproject.legend(idarticle) VALUES (?)");
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
            if (!racesAdded.isEmpty()) {
               String query = "INSERT INTO worldproject.legend_race(idlegend, idrace) VALUES ";
               addTuples(connection, query, getId(), racesAdded);
               racesAdded.clear();
            }
            if (!racesDeleted.isEmpty()) {
               for (Article race : racesDeleted) {
                  deleteTuple(connection, "DELETE FROM worldproject.legend_race WHERE idlegend = ? AND idrace = ?",
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
}
