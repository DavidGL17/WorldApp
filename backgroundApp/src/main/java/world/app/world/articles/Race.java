/*
 * @File Race.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;

import java.sql.*;

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

   public static void loadRaceIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.race r ON a.id = r.idArticle WHERE " +
              "a.id = ? AND a.idWorld=?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         articles.add(new Race(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                               resultSet.getString("content"), resultSet.getDate("last_update"),
                               resultSet.getInt("heightMin"), resultSet.getInt("heightMax"),
                               (Language) world.getArticleWithId(resultSet.getInt("idLanguage"))));
      }
   }

   public static int createRace(World world, String name, String content, int idLanguage, int heightMin, int heightMax)
           throws SQLException {
      int id;
      try {
         id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO worldproject.race(idarticle, idlanguage, heightmin, heightmax) VALUES (?,?,?,?)");
         statement.setInt(1, id);
         statement.setInt(2, idLanguage);
         statement.setInt(3, heightMin);
         statement.setInt(4, heightMax);
         statement.execute();
      } catch (SQLException throwables) {
         throwables.printStackTrace();
         System.err.println("Error while adding article to database");
         throw throwables;
      }
      return id;
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
