/*
 * @File Alphabet.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;

import java.sql.*;

public class Alphabet extends Article {
   private final HashMapChaining<Language> languages;
   private String writingSystem;

   public Alphabet(int id, World world, String name, String content, Date lastUpdate, String writingSystem) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.ALPHABET);
      this.writingSystem = writingSystem;
      languages = new HashMapChaining<>();
   }

   public String getWritingSystem() {
      return writingSystem;
   }

   public void setWritingSystem(String writingSystem) {
      if (writingSystem != null && isInModification()) {
         this.writingSystem = writingSystem;
      }
   }

   public static void loadAlphabetIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.alphabet a2 ON a.id = a2.idArticle " +
              "WHERE a.id = ? AND a.idworld = ?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         articles.add(new Alphabet(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                   resultSet.getString("content"), resultSet.getDate("last_update"),
                                   resultSet.getString("writingSystem")));
      }
   }

   public static int createAlphabet(World world, String name, String content, String writingSystem)
           throws SQLException {
      int id;
      try {
         id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO worldproject.alphabet(idarticle, writingsystem) VALUES (?,?)");
         statement.setInt(1, id);
         statement.setString(2, writingSystem);
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
      return super.toString() + ", writingSystem : '" + writingSystem + "'}";
   }

   @Override
   public boolean endModifications() {
      if (isUpdateNeeded()) {
         try {
            Connection connection = getWorld().getUser().getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE worldproject.alphabet SET writingsystem = ? WHERE idarticle = ?");
            statement.setString(1, writingSystem);
            statement.setInt(2, getId());
            statement.execute();
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Error while saving changes of article");
            return false;
         }
      }
      return super.endModifications();
   }

   protected void addLanguage(Language language) {
      if (language == null) {
         throw new NullPointerException();
      }
      if (language.getAlphabet() != this) {
         return;
      }
      languages.add(language);
   }

   protected void removeLanguage(Language language) {
      if (language == null) {
         throw new NullPointerException();
      }
      if (language.getAlphabet() != this) {
         return;
      }
      languages.erase(language);
   }
}
