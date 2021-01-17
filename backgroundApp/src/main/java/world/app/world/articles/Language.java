/*
 * @File Language.java
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

public class Language extends Article {
   private Alphabet alphabet;
   private HashMapChaining<Race> races;

   public Language(int id, World world, String name, String content, Date lastUpdate, Alphabet alphabet,
                   HashMapChaining<Race> races) {
      super(id, world, name, content, lastUpdate, TypeOfArticle.LANGUAGE);
      this.alphabet = alphabet;
      alphabet.addLanguage(this);
      this.races = races;
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
