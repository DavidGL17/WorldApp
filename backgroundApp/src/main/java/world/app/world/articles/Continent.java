/*
 * @File Continent.java
 * @Authors : David González León
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;

import java.sql.Date;

public class Continent extends Article {
   private HashMapChaining<Country> countries;

   public Continent(int id, World world, String name, String content, Date lastUpdate,
                    HashMapChaining<Country> countries) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.CONTINENT);
      this.countries = countries;
   }

   @Override
   public String toString() {
      return super.toString() + "}";
   }

   protected void addCountry(Country country) {
      if (country == null) {
         throw new NullPointerException();
      }
      if (country.getContinent() != this) {
         return;
      }
      countries.add(country);
   }

   protected void removeCountry(Country country) {
      if (country == null) {
         throw new NullPointerException();
      }
      if (country.getContinent() != this) {
         return;
      }
      countries.erase(country);
   }

}
