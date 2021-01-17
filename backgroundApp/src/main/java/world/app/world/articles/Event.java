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
   private Date startDate;
   private Date endDate;

   public Event(int id, World world, String name, String content, Date lastUpdate, Date startDate, Date endDate,
                TypeOfArticle type) {
      super(id, world, name, content, lastUpdate, type);
      this.startDate = startDate;
      this.endDate = endDate;
   }

   @Override
   public String toString() {
      return super.toString() + ", startDate : " + startDate + ", endDate : " + endDate;
   }

   public Date getEndDate() {
      return endDate;
   }

   public void setEndDate(Date endDate) {
      if (endDate != null && isInModification()) {
         this.endDate = endDate;
         setUpdateIsNeeded();
      }
   }

   public Date getStartDate() {
      return startDate;
   }

   public void setStartDate(Date startDate) {
      if (startDate != null && isInModification()) {
         this.startDate = startDate;
         setUpdateIsNeeded();
      }
   }
}
