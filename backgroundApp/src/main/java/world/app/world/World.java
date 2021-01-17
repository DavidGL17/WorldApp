/*
 * @File World.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world;

import util.HashMapChaining;
import world.app.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

//TODO Implement the addition of articles, and insert a few, and then try to see if it loads them

/**
 * The type World.
 */
public class World implements Modifiable {
   private final int id;
   private final User user;
   private final HashMapChaining<Article> articles = new HashMapChaining<>();
   private String name;
   private String description;
   private boolean inModification = false;
   private boolean updateNeeded = false;

   /**
    * Instantiates a new World.
    *
    * @param name the name of the world
    * @param user the user that creates this world
    */
   public World(String name, User user) throws SQLException {
      try {
         Connection connection = user.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement("SELECT * FROM worldproject.world w WHERE w.name = ? AND w.idUser = ?");
         statement.setString(1, name);
         statement.setInt(2, user.getId());
         ResultSet resultSet = statement.executeQuery();
         resultSet.next();
         if (!resultSet.isLast()) {
            throw new RuntimeException("name or userId is wrong");
         }
         this.user = user;
         this.name = name;
         this.id = resultSet.getInt("id");
         this.description = resultSet.getString("description");
      } catch (SQLException throwables) {
         throwables.printStackTrace();
         System.err.println("Error while creating world");
         throw throwables;
      }
   }

   /**
    * Gets id.
    *
    * @return the id of the world
    */
   public int getId() {
      return id;
   }

   /**
    * Gets user of the world
    *
    * @return the user
    */
   public User getUser() {
      return user;
   }

   /**
    * Gets name.
    *
    * @return the name of the world
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name of the world. Only available during modification. If name is already used in a world associated with
    * user, it does not change the name.
    *
    * @param name the new name of the world
    */
   public void setName(String name) {
      if (name != null) {
         if (inModification) {
            if (!getUser().checkWorldName(name)) {
               return;
            }
            this.name = name;
            updateNeeded = true;
         }
      }
   }

   /**
    * Gets the description.
    *
    * @return the description of the world
    */
   public String getDescription() {
      return description;
   }

   /**
    * Sets the description of the world. Only available during modification.
    *
    * @param description the new description of the world
    */
   public void setDescription(String description) {
      if (description != null) {
         if (inModification) {
            this.description = description;
            updateNeeded = true;
         }
      }
   }

   /**
    * Gets article with id.
    *
    * @param id the id of the article
    *
    * @return the article with the given id, or null if there is no article with this id in the world
    */
   public Article getArticleWithId(int id) {
      return articles.find(Objects.hash(id));
   }

   /**
    * Returns a copy of the HashMapChaining articles containing the same elements
    *
    * @return a copy of articles
    */
   public HashMapChaining<Article> getArticles() {
      return articles.copy();
   }

   public void startModifications() {
      inModification = true;
   }

   public boolean endModifications() {
      inModification = false;
      if (updateNeeded) {
         Connection connection = user.getConnection();
         try {
            PreparedStatement statement =
                    connection.prepareStatement("UPDATE worldproject.world w SET description = ?, name=? WHERE id = ?");
            statement.setString(1, description);
            statement.setString(2, name);
            statement.setInt(3, id);
            statement.execute();
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
         }
         updateNeeded = false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      return Objects.hash(getId());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof World)) { return false; }
      World world = (World) o;
      return getId() == world.getId();
   }

   @Override
   public String toString() {
      return "World : " + name + ". Description : " + description;
   }

   /**
    * Loads all the articles of the world
    */
   public void loadArticles() {
      articles.addAll(Article.loadAlphabets(this));
      articles.addAll(Article.loadLanguages(this));
      articles.addAll(Article.loadRaces(this));
      articles.addAll(Article.loadLegends(this));
      articles.addAll(Article.loadCharacters(this));
      articles.addAll(Article.loadContinent(this));
      articles.addAll(Article.loadCountry(this));
      articles.addAll(Article.loadRank(this));
      articles.addAll(Article.loadSide(this));
      articles.addAll(Article.loadWar(this));
      articles.addAll(Article.loadAccordType(this));
      articles.addAll(Article.loadAccord(this));
   }
}
