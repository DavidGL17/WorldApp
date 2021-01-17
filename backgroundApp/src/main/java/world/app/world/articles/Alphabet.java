/*
 * @File Alphabet.java
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

public class Alphabet extends Article {
   private String writingSystem;
   private final HashMapChaining<Language> languages;

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
