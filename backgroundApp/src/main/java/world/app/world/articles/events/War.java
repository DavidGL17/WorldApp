/*
 * @File War.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles.events;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;
import world.app.world.articles.Event;
import world.app.world.articles.Side;
import world.app.world.articles.TypeOfArticle;

import java.sql.*;

public class War extends Event {
   private int deathCount;
   private Side side1;
   private Side side2;

   public War(int id, World world, String name, String content, Date lastUpdate, String startDate, String endDate,
              int deathCount, Side side1, Side side2) {
      super(id, world, name, content, lastUpdate, startDate, endDate, TypeOfArticle.WAR);
      this.deathCount = deathCount;
      this.side1 = side1;
      this.side2 = side2;
   }

   public static void loadWarIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.war w ON a.id = w.idArticle WHERE " +
              "a.id = ? AND a.idWorld = ?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         articles.add(new War(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                              resultSet.getString("content"), resultSet.getDate("last_update"),
                              resultSet.getString("dateBeginning"), resultSet.getString("dateEnd"),
                              resultSet.getInt("deathCount"),
                              (Side) world.getArticleWithId(resultSet.getInt("idSide1")),
                              (Side) world.getArticleWithId(resultSet.getInt("idSide2"))));
      }
   }

   public static int createWar(World world, String name, String content, int idSide1, int idSide2, String dateBeginning,
                               String dateEnd, int deathCount) throws SQLException {
      int id;
      try {
         id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO worldproject.war(idarticle, idside1, idside2, datebeginning, dateend, deathcount) " +
                 "VALUES (?,?,?,?,?,?)");
         statement.setInt(1, id);
         statement.setInt(2, idSide1);
         statement.setInt(3, idSide2);
         statement.setString(4, dateBeginning);
         statement.setString(5, dateEnd);
         statement.setInt(6, deathCount);
         statement.execute();
      } catch (SQLException throwables) {
         throwables.printStackTrace();
         System.err.println("Error while adding article to database");
         throw throwables;
      }
      return id;
   }

   @Override
   public boolean endModifications() {
      if (isUpdateNeeded()) {
         try {
            Connection connection = getWorld().getUser().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE worldproject.war SET deathcount = ?, idside1 = ?, idside2 = ?, datebeginning = ?, dateend" +
                    " = ? WHERE idarticle = ?");
            statement.setInt(1, deathCount);
            statement.setInt(2, side1.getId());
            statement.setInt(3, side2.getId());
            statement.setString(4, getStartDate());
            statement.setString(5, getEndDate());
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

   @Override
   public String toString() {
      return super.toString() + ", deathCount : " + deathCount + '}';
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
