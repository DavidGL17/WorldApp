/*
 * @File Race.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import world.app.world.Article;
import world.app.world.World;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Race extends Article {
   private int heighMin;
   private int heightMax;
   private Language language;

   public Race(int id, World world, String name, String content, Date lastUpdate, int heighMin, int heightMax,
               Language language) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.RACE);
      this.heighMin = heighMin;
      this.heightMax = heightMax;
      this.language = language;
      language.addRace(this);
   }

   @Override
   public String toString() {
      return super.toString() + ", heighMin : " + heighMin + ", heightMax : " + heightMax + '}';
   }

   @Override
   public boolean endModifications() {
      if (isUpdateNeeded()) {
         try {
            Connection connection = getWorld().getUser().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE worldproject.race SET heightmin=?, heightmax=?, idlanguage=? WHERE idarticle=?");
            statement.setInt(1, heighMin);
            statement.setInt(2, heightMax);
            statement.setInt(3, language.getId());
            statement.setInt(4, getId());
            statement.execute();
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Error while saving changes of article");
            return false;
         }
      }
      return super.endModifications();
   }

   public int getHeighMin() {
      return heighMin;
   }

   public void setHeighMin(int heighMin) {
      if (heighMin <= 0 && heighMin <= heightMax && isInModification()) {
         this.heighMin = heighMin;
         setUpdateIsNeeded();
      }
   }

   public int getHeightMax() {
      return heightMax;
   }

   public void setHeightMax(int heightMax) {
      if (heightMax <= 0 && heightMax >= heighMin && isInModification()) {
         this.heightMax = heightMax;
         setUpdateIsNeeded();
      }
   }

   public Language getLanguage() {
      return language;
   }

   public void setLanguage(Language language) {
      if (language != null && isInModification() && !language.equals(this.language)) {
         this.language.removeRace(this);
         this.language = language;
         language.addRace(this);
         setUpdateIsNeeded();
      }
   }
}
