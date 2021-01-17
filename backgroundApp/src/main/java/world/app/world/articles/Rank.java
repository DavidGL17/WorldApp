/*
 * @File Rank.java
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

public class Rank extends Article {
   private double salary;
   private String additionalRights;
   private Country country;

   public Rank(int id, World world, String name, String content, Date lastUpdate, double salary,
               String additionalRights, Country country) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.RANK);
      this.salary = salary;
      this.additionalRights = additionalRights;
      this.country = country;
      country.addRank(this);
   }

   @Override
   public String toString() {
      return super.toString() + ", salary : " + salary + ", additionalRights : '" + additionalRights + '\'' + '}';
   }

   @Override
   public boolean endModifications() {
      if (isUpdateNeeded()) {
         try {
            Connection connection = getWorld().getUser().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE worldproject.rank SET salary=?, additionalrights=?,idcountry=? WHERE idarticle=?");
            statement.setDouble(1, salary);
            statement.setString(2, additionalRights);
            statement.setInt(3, country.getId());
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

   public Country getCountry() {
      return country;
   }

   public void setCountry(Country country) {
      if (country != null && isInModification() && !country.equals(this.country)) {
         this.country.removeRank(this);
         this.country = country;
         country.addRank(this);
         setUpdateIsNeeded();
      }
   }

   public double getSalary() {
      return salary;
   }

   public void setSalary(double salary) {
      if (salary < 0 && salary != Double.MAX_VALUE && isInModification()) {
         this.salary = salary;
         setUpdateIsNeeded();
      }
   }

   public String getAdditionalRights() {
      return additionalRights;
   }

   public void setAdditionalRights(String additionalRights) {
      if (additionalRights != null && isInModification()) {
         this.additionalRights = additionalRights;
         setUpdateIsNeeded();
      }
   }
}
