/*
 * @File Character.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world.articles;

import util.HashMapChaining;
import world.app.world.Article;
import world.app.world.World;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Character extends Article {
   private Race race;
   private HashMapChaining<Language> languages;
   private ArrayList<Language> languagesDeleted = new ArrayList<>();
   private ArrayList<Article> languagesAdded = new ArrayList<Article>();

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
      return languages.copy();
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
