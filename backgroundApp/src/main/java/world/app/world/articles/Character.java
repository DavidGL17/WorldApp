/*
 * @File Character.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;

import java.sql.*;
import java.util.ArrayList;

public class Character extends Article {
   private final HashMapChaining<Language> languages;
   private final ArrayList<Language> languagesDeleted = new ArrayList<>();
   private final ArrayList<Article> languagesAdded = new ArrayList<>();
   private Race race;

   public Character(int id, World world, String name, String content, Date lastUpdate, Race race,
                    HashMapChaining<Language> languages) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.CHARACTER);
      this.race = race;
      this.languages = languages;
   }

   public Race getRace() {
      return race;
   }

   public void setRace(Race race) {
      if (race != null && isInModification()) {
         this.race = race;
         setUpdateIsNeeded();
      }
   }

   public HashMapChaining<Language> getLanguages() {
      return languages;
   }

   public void addLanguage(Language language) {
      if (language != null && isInModification()) {
         languages.add(language);
         languagesAdded.add(language);
         languagesDeleted.remove(language);
         setUpdateIsNeeded();
      }
   }

   public void removeLanguage(Language language) {
      if (language != null && isInModification()) {
         if (languages.erase(language)) {
            languagesDeleted.add(language);
            languagesAdded.remove(language);
            setUpdateIsNeeded();
         }
      }
   }

   public static void loadCharacterIntoWorld(World world, int id) throws SQLException {
      HashMapChaining<Article> articles = world.getArticles();
      if (articles.find(id) != null) {
         return;
      }
      PreparedStatement statement = world.getUser().getConnection().prepareStatement(
              "SELECT * FROM worldproject.article INNER JOIN worldproject.character c ON article.id = c.idArticle " +
              "WHERE article.id = ? AND article.idWorld=?");
      statement.setInt(1, id);
      statement.setInt(2, world.getId());
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         statement = world.getUser().getConnection().prepareStatement(
                 "SELECT cl.idLanguage FROM worldproject.character c INNER JOIN worldproject.character_language cl" +
                 " ON c.idArticle = cl.idCharacter WHERE c.idArticle=?");
         statement.setInt(1, resultSet.getInt("id"));
         ResultSet languagesResult = statement.executeQuery();
         HashMapChaining<Language> languages = new HashMapChaining<>();
         while (languagesResult.next()) {
            languages.add((Language) world.getArticleWithId(languagesResult.getInt(1)));
         }
         articles.add(new Character(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                    resultSet.getString("content"), resultSet.getDate("last_update"),
                                    (Race) world.getArticleWithId(resultSet.getInt("idRace")), languages));
      }
   }

   public static int createCharacter(World world, String name, String content, int idRace) throws SQLException {
      int id;
      try {
         id = Article.createArticle(world, name, content);
         Connection connection = world.getUser().getConnection();
         PreparedStatement statement =
                 connection.prepareStatement("INSERT INTO worldproject.character(idarticle, idrace) VALUES (?,?)");
         statement.setInt(1, id);
         statement.setInt(2, idRace);
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
      return super.toString() + ", race : " + race + '}';
   }

   @Override
   public boolean endModifications() {
      if (isUpdateNeeded()) {
         try {
            Connection connection = getWorld().getUser().getConnection();
            PreparedStatement statement =
                    connection.prepareStatement("UPDATE worldproject.character SET idrace = ? WHERE idarticle = ?");
            statement.setInt(1, race.getId());
            statement.setInt(2, getId());
            statement.execute();
            if (!languagesAdded.isEmpty()) {
               String query = "Insert Into worldproject.character_language(idcharacter, idlanguage) VALUES ";
               addTuples(connection, query, getId(), languagesAdded);
               languagesAdded.clear();
            }
            if (!languagesDeleted.isEmpty()) {
               for (Language language : languagesDeleted) {
                  deleteTuple(connection,
                              "DELETE FROM worldproject.character_language WHERE idcharacter=? AND " + "idlanguage = ?",
                              getId(), language.getId());
               }
               languagesDeleted.clear();
            }
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Error while saving changes of article");
            return false;
         }
      }
      return super.endModifications();
   }
}
