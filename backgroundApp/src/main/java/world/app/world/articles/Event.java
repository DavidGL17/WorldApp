/*
 * @File Event.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import world.app.world.Article;
import world.app.world.World;

import java.sql.Date;

public abstract class Event extends Article {
   private String startDate;
   private String endDate;

   public Event(int id, World world, String name, String content, Date lastUpdate, String startDate, String endDate,
                TypeOfArticle type) {
      super(id, world, name, content, lastUpdate, type);
      this.startDate = startDate;
      this.endDate = endDate;
   }

   @Override
   public String toString() {
      return super.toString() + ", startDate : " + startDate + ", endDate : " + endDate;
   }

   public String getEndDate() {
      return endDate;
   }

   public void setEndDate(String endDate) {
      if (endDate != null && isInModification()) {
         this.endDate = endDate;
         setUpdateIsNeeded();
      }
   }

   public String getStartDate() {
      return startDate;
   }

   public void setStartDate(String startDate) {
      if (startDate != null && isInModification()) {
         this.startDate = startDate;
         setUpdateIsNeeded();
      }
   }
}
