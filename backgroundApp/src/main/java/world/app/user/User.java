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
    * @param password   the password
    * @param connection the connection to the database
    *
    * @throws UserIdNotFoundException the user id not found exception
    * @throws WrongPasswordException  the wrong password exception
    */
   public User(int id, String password, Connection connection) throws UserIdNotFoundException, WrongPasswordException {
      this.id = id;
      try {
         this.connection = connection;
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT firstName,lastName,email,password FROM worldproject.user_account u WHERE u.id = ?");
         statement.setInt(1, id);
         ResultSet resultSet = statement.executeQuery();
         if (resultSet.next()) {
            if (resultSet.getString("password").equals(password)) {
               this.firstName = resultSet.getString("firstName");
               this.lastName = resultSet.getString("lastName");
               this.email = resultSet.getString("email");
               loadWorlds();
            } else {
               throw new WrongPasswordException("Wrong password");
            }
         } else {
            throw new UserIdNotFoundException("no user with this id");
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
    * @param password   the password
    *
    * @return the id of the new user
    *
    * @throws SQLException the sql exception
    */
   public static int createUser(Connection connection, String firstName, String lastName, String email, String password)
           throws SQLException {
      PreparedStatement statement = connection.prepareStatement(
              "INSERT INTO worldproject.user_account(email, firstName, lastName, password) VALUES (?,?,?,?) " +
              "RETURNING " + "id;");
      statement.setString(1, email);
      statement.setString(2, firstName);
      statement.setString(3, lastName);
      statement.setString(4, password);
      ResultSet resultSet = statement.executeQuery();
      resultSet.next();
      return resultSet.getInt(1);
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
    * Loads a specific world's information, meaning all of it's articles
    *
    * @param worldID the world id of the world we want to load the information from
    */
   public void loadWorldInformation(int worldID) {
      for (World w : worlds) {
         if (w.getId() == worldID) {
            w.loadArticles();
            return;
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
   public boolean isWorldNameValid(String worldName) {
      for (World world : worlds) {
         if (world.getName().equals(worldName)) {
            return false;
         }
      }
      return true;
   }

   /**
    * Adds a world to the list of wolrds of this user, and loads all of it's articles
    *
    * @param id
    * @param name
    * @param description
    */
   private void loadWorld(int id, String name, String description) {
      worlds.add(new World(id, this, name, description));
   }

   /**
    * Loads all the worlds associated to the user and stores them in the list worlds. This function does not load all
    * the artciles associated with each world, only it's description and name
    */
   private void loadWorlds() {
      try {
         PreparedStatement statement =
                 connection.prepareStatement("SELECT * FROM worldproject.world WHERE world.idUser=?;");
         statement.setInt(1, id);
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            loadWorld(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("description"));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
   }

   /**
    * The type User id not found exception.
    */
   public static class UserIdNotFoundException extends Exception {
      /**
       * Instantiates a new User id not found exception.
       *
       * @param message the message
       */
      public UserIdNotFoundException(String message) {
         super(message);
      }
   }

   /**
    * The type Wrong password exception.
    */
   public static class WrongPasswordException extends Exception {
      /**
       * Instantiates a new Wrong password exception.
       *
       * @param message the message
       */
      public WrongPasswordException(String message) {
         super(message);
      }
   }
}
