/*
 * @File Side.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;
import world.app.world.articles.Country;
import world.app.world.articles.TypeOfArticle;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class Side extends Article {
   private final HashMapChaining<Country> countries;
   private final ArrayList<Country> countriesDeleted = new ArrayList<>();
   private final ArrayList<Article> coutriesAdded = new ArrayList<>();

   public Side(int id, World world, String name, String content, Date lastUpdate, HashMapChaining<Country> countries) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.SIDE);
      this.countries = countries;
      for (Country c : countries) {
         c.addSide(this);
      }
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
            if (!coutriesAdded.isEmpty()) {
               String query = "INSERT INTO worldproject.country_side(idside, idcountry) VALUES ";
               addTuples(connection, query, getId(), coutriesAdded);
               coutriesAdded.clear();
            }
            if (!countriesDeleted.isEmpty()) {
               for (Country country : countriesDeleted) {
                  deleteTuple(connection, "DELETE FROM worldproject.accord_country WHERE idside=? AND idcountry = ?",
                              getId(), country.getId());
               }
               countriesDeleted.clear();
            }
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Error while saving changes of article");
            return false;
         }
      }
      return super.endModifications();
   }

   public HashMapChaining<Country> getCountries() {
      return countries;
   }

   public void addCountry(Country country) {
      if (country != null && isInModification() && !countries.contains(country)) {
         countries.add(country);
         country.addSide(this);
         coutriesAdded.add(country);
         countriesDeleted.remove(country);
         setUpdateIsNeeded();
      }
   }

   public void removeCountry(Country country) {
      if (country != null && isInModification() && countries.contains(country)) {
         country.removeSide(this);
         if (countries.erase(country)) {
            countriesDeleted.add(country);
            coutriesAdded.remove(country);
            setUpdateIsNeeded();
         }
      }
   }
}
