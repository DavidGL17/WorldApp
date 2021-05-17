/*
 * @File Rank.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package ch.world.app.client.background.world.articles;


import ch.world.app.client.background.util.HashMapChaining;
import ch.world.app.client.background.world.Article;
import ch.world.app.client.background.world.World;
import ch.world.app.util.json.TypeOfArticle;

import java.sql.*;

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

   public static void loadRankIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.rank r ON a.id = r.idArticle WHERE " +
              "a.id = ? AND a.idWorld = ?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         articles.add(new Rank(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                               resultSet.getString("content"), resultSet.getDate("last_update"),
                               resultSet.getDouble("salary"), resultSet.getString("additionalRights"),
                               (Country) world.getArticleWithId(resultSet.getInt("idCountry"))));
      }
   }

   public static int createRank(World world, String name, String content, int idCountry, double salary,
                                 String additionalRights) throws SQLException {
      int id;
      try {
          id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO worldproject.rank(idarticle, idcountry, salary, additionalrights) VALUES (?,?,?,?)");
         statement.setInt(1, id);
         statement.setInt(2, idCountry);
         statement.setDouble(3, salary);
         statement.setString(4, additionalRights);
         statement.execute();
      } catch (SQLException throwables) {
         throwables.printStackTrace();
         System.err.println("Error while adding article to database");
         throw throwables;
      }
      return id;
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
