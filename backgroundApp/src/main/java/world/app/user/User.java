/*
 * @File User.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.user;

import util.HashMapChaining;
import world.app.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * The type User, representing a knwon user of the application
 */
//Utilisateur, connecté a un certain nombre de mondes
public class User {
   private final int id;
   private final HashMapChaining<World> worlds = new HashMapChaining<>();
   private String firstName;
   private String lastName;
   private String email;
   private Connection connection;

   /**
    * Instantiates a new User.
    *
    * @param id         the id of the user
    * @param connection the connection to the database
    */
   public User(int id, Connection connection) {
      this.id = id;
      try {
         this.connection = connection;
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT firstName,lastName,email FROM worldproject.user_account" + " u " + "WHERE u.id = ?");
         statement.setInt(1, id);
         ResultSet resultSet = statement.executeQuery();
         if (resultSet.next()) {
            this.firstName = resultSet.getString("firstName");
            this.lastName = resultSet.getString("lastName");
            this.email = resultSet.getString("email");
            loadWorlds();
         } else {
            throw new IllegalArgumentException("no user with this id");
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
         System.out.println("Error while connecting, please try again");
      }
   }

   /**
    * Creates a new user, and adds it to the database using connection
    *
    * @param connection the connection to the database
    * @param firstName  the first name of the user
    * @param lastName   the last name of the user
    * @param email      the email of the user
    *
    * @return the id of the new user
    */
   public static int createUser(Connection connection, String firstName, String lastName, String email) {
      int id = 0;
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO worldproject.user_account(email, firstName, lastName) VALUES (?,?,?) RETURNING id;");
         statement.setString(1, email);
         statement.setString(2, firstName);
         statement.setString(3, lastName);
         ResultSet resultSet = statement.executeQuery();
         resultSet.next();
         id = resultSet.getInt(1);
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return id;
   }

   @Override
   public int hashCode() {
      return Objects.hash(getId());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof User)) { return false; }
      User user = (User) o;
      return getId() == user.getId();
   }

   /**
    * Gets id.
    *
    * @return the id of the user
    */
   public int getId() {
      return id;
   }

   /**
    * Gets the worlds owned by the user
    *
    * @return the worlds owned by the user
    */
   public HashMapChaining<World> getWorlds() {
      return worlds;
   }

   /**
    * Gets a specific world
    *
    * @param id the id of this specific world
    *
    * @return the world with this specific id, or null if the user has no world with this id
    */
   public World getWorld(int id) {
      return worlds.find(id);
   }

   /**
    * Gets first name.
    *
    * @return the first name of the user
    */
   public String getFirstName() {
      return firstName;
   }

   /**
    * Gets last name.
    *
    * @return the last name of the user
    */
   public String getLastName() {
      return lastName;
   }

   /**
    * Gets email.
    *
    * @return the email of the user
    */
   public String getEmail() {
      return email;
   }

   /**
    * Gets connection.
    *
    * @return the connection to the database of the user
    */
   public Connection getConnection() {
      return connection;
   }

   /**
    * Create a new world and links it with the user. If the name given to the world is already given to another world of
    * the user, the operation is refused
    *
    * @param name the name of the world to create
    *
    * @return true if the world was created, false if it wasn't
    */
   public boolean createWorld(String name) {
      if (!checkWorldName(name)) {
         return false;
      }
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO worldproject.world(name, idUser, description) VALUES (?,?,?)");
         statement.setString(1, name);
         statement.setInt(2, id);
         statement.setString(3, "");
         statement.execute();
      } catch (SQLException throwables) {
         throwables.printStackTrace();
         System.out.println("Error while inserting the world");
      }
      return true;
   }

   /**
    * Loads a specific world's information, meaning all of it's articles
    *
    * @param worldID the world id of the world we want to load the information from
    */
   public void loadWorldInformation(int worldID) {
      for (World w : worlds) {
         if (w.getId() == worldID) {
            w.loadArticles();
            break;
         }
      }
      System.err.println("No world with this id exists");
   }

   /**
    * Checks if the worldName is not already used in another world associated to the user
    *
    * @param worldName the name of the world to check
    *
    * @return true if there is no world with this name, false otherwise
    */
   public boolean checkWorldName(String worldName) {
      for (World world : worlds) {
         if (world.getName().equals(worldName)) {
            return false;
         }
      }
      return true;
   }

   /**
    * Loads the articles of the given world
    *
    * @param worldName the name of the world
    */
   private void loadWorld(String worldName) {
      try {
         worlds.add(new World(worldName, this));
      } catch (SQLException throwables) {
         throwables.printStackTrace();
         System.out.println("Error loading a world");
      }
   }

   /**
    * Loads all the worlds associated to the user and stores them in the list worlds. This function does not load all
    * the artciles associated with each world, only it's description and name
    */
   private void loadWorlds() {
      try {
         PreparedStatement statement =
                 connection.prepareStatement("SELECT name FROM worldproject.world WHERE world.idUser=?;");
         statement.setInt(1, id);
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            loadWorld(resultSet.getString(1));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
   }
}
