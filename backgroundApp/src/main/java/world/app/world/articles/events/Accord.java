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

import java.sql.Date;
import java.util.ArrayList;

public class Accord extends Event {
   private AccordType type;
   private final HashMapChaining<Country> countries;
   private final ArrayList<Country> countriesDeleted = new ArrayList<>();
   private final ArrayList<Article> coutriesAdded = new ArrayList<>();

   public Accord(int id, World world, String name, String content, Date lastUpdate, Date startDate, Date endDate,
                 AccordType type, HashMapChaining<Country> countries) {
      super(id, world, name, content, lastUpdate, startDate, endDate, TypeOfArticle.ACCORD);
      this.type = type;
      this.countries = countries;
      for (Country c : countries) {
         c.addAccord(this);
      }
      type.addAccord(this);
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
      return countries.copy();
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
