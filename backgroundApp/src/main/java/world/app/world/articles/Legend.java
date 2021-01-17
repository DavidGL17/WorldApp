/*
 * @File Legend.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class Legend extends Article {
   private HashMapChaining<Race> races;
   private ArrayList<Article> racesAdded = new ArrayList<>();
   private ArrayList<Article> racesDeleted = new ArrayList<>();

   public Legend(int id, World world, String name, String content, Date lastUpdate, HashMapChaining<Race> races) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.LEGEND);
      this.races = races;
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
