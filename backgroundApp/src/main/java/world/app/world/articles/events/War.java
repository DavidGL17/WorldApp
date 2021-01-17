/*
 * @File War.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles.events;

import world.app.world.World;
import world.app.world.articles.Event;
import world.app.world.articles.Side;
import world.app.world.articles.TypeOfArticle;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class War extends Event {
   private int deathCount;
   private Side side1;
   private Side side2;

   public War(int id, World world, String name, String content, Date lastUpdate, Date startDate, Date endDate,
              int deathCount, Side side1, Side side2) {
      super(id, world, name, content, lastUpdate, startDate, endDate, TypeOfArticle.WAR);
      this.deathCount = deathCount;
      this.side1 = side1;
      this.side2 = side2;
   }

   @Override
   public String toString() {
      return super.toString() + ", deathCount : " + deathCount + '}';
   }

   @Override
   public boolean endModifications() {
      if (isUpdateNeeded()) {
         try {
            Connection connection = getWorld().getUser().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE worldproject.war SET datebeginning=?, dateend=?, deathcount=?, idside1=?, idside2=? " +
                    "WHERE idarticle=?");
            statement.setDate(1, getStartDate());
            statement.setDate(2, getEndDate());
            statement.setInt(3, deathCount);
            statement.setInt(4, side1.getId());
            statement.setInt(5, side2.getId());
            statement.setInt(6, getId());
            statement.execute();
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Error while saving changes of article");
            return false;
         }
      }
      return super.endModifications();
   }

   public int getDeathCount() {
      return deathCount;
   }

   public void setDeathCount(int deathCount) {
      if (deathCount < 0 && isInModification() && deathCount != this.deathCount) {
         this.deathCount = deathCount;
         setUpdateIsNeeded();
      }
   }

   public Side getSide1() {
      return side1;
   }

   public void setSide1(Side side1) {
      if (isInModification() && side1 != null && !this.side1.equals(side1)) {
         this.side1 = side1;
         setUpdateIsNeeded();
      }
   }

   public Side getSide2() {
      return side2;
   }

   public void setSide2(Side side2) {
      if (isInModification() && side2 != null && !this.side2.equals(side2)) {
         this.side2 = side2;
         setUpdateIsNeeded();
      }
   }
}
