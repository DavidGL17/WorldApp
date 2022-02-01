/*
 * @File Language.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package ch.world.app.client.background.world.articles;


import ch.world.app.client.background.util.HashMapChaining;
import ch.world.app.client.background.world.Article;
import ch.world.app.client.background.world.World;

import java.sql.*;

public class Language extends Article {
   private final HashMapChaining<Race> races;
   private Alphabet alphabet;

   public Language(int id, World world, String name, String content, Date lastUpdate, Alphabet alphabet,
                   HashMapChaining<Race> races) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.LANGUAGE);
      this.alphabet = alphabet;
      alphabet.addLanguage(this);
      this.races = races;
   }

   public static void loadLanguageIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article a INNER JOIN worldproject.language l ON a.id = l.idArticle WHERE" +
              "  a.id = ? AND a.idWorld = ?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         articles.add(new Language(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                   resultSet.getString("content"), resultSet.getDate("last_update"),
                                   (Alphabet) world.getArticleWithId(resultSet.getInt("idAlphabet")),
                                   new HashMapChaining<>()));
      }
   }

   public static int createLanguage(World world, String name, String content, int idAlphabet) throws SQLException {
      int id;
      try {
         id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement =
                 connection.prepareStatement("INSERT INTO worldproject.language(idarticle, idalphabet) VALUES (?,?)");
         statement.setInt(1, id);
         statement.setInt(2, idAlphabet);
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
      return super.toString() + '}';
   }

   @Override
   public boolean endModifications() {
      if (isUpdateNeeded()) {
         try {
            Connection connection = getWorld().getUser().getConnection();
            PreparedStatement statement =
                    connection.prepareStatement("UPDATE worldproject.language SET idalphabet = ? WHERE idarticle = ?");
            statement.setInt(1, alphabet.getId());
            statement.setInt(2, getId());
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Error while saving changes of article");
            return false;
         }
      }
      return super.endModifications();
   }

   public Alphabet getAlphabet() {
      return alphabet;
   }

   public void setAlphabet(Alphabet alphabet) {
      if (alphabet != null && isInModification()) {
         this.alphabet.removeLanguage(this);
         this.alphabet = alphabet;
         alphabet.addLanguage(this);
         setUpdateIsNeeded();
      }
   }

   public HashMapChaining<Race> getRaces() {
      return races;
   }

   protected void addRace(Race race) {
      if (race == null) {
         throw new NullPointerException();
      }
      if (race.getLanguage() != this) {
         return;
      }
      races.add(race);
   }

   protected void removeRace(Race race) {
      if (race == null) {
         throw new NullPointerException();
      }
      if (race.getLanguage() != this) {
         return;
      }
      races.erase(race);
   }
}
