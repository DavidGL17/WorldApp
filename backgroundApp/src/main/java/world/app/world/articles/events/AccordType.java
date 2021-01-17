/*
 * @File AccordType.java
 * @Authors : David González León,
 * @Date 10 janv. 2021
 */
package world.app.world.articles.events;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;
import world.app.world.articles.TypeOfArticle;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccordType extends Article {
   private HashMapChaining<Accord> accords;

   public AccordType(int id, World world, String name, String content, Date lastUpdate) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.ACCORD_TYPE);
      accords = new HashMapChaining<>();
   }

   @Override
   public String toString() {
      return super.toString() + "}";
   }

   protected void addAccord(Accord accord) {
      if (accord == null) {
         throw new NullPointerException();
      }
      if (accord.getType() != this) {
         return;
      }
      accords.add(accord);
   }

   protected void removeAccord(Accord accord) {
      if (accord == null) {
         throw new NullPointerException();
      }
      if (accord.getType() != this) {
         return;
      }
      accords.erase(accord);
   }
}
