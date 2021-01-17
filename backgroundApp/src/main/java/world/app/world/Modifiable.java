/*
 * @File Modifiable.java
 * @Authors : David González León,
 * @Date 14 janv. 2021
 */
package world.app.world;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The interface Modifiable. implemented by classes that will be modifiable only for a short period of time
 */
public interface Modifiable {
   /**
    * Start modifications.
    */
   void startModifications();

   /**
    * End modifications. If the modifications were ended and saved successfully, returns true, otherwise returns false;
    */
   boolean endModifications();

   default void addTuples(Connection connection, String beggining, int id, ArrayList<Article> articles)
           throws SQLException {
      StringBuilder query = new StringBuilder(beggining);
      for (int i = 0; i < articles.size(); ++i) {
         query.append("(?,?)");
         if (i != articles.size() - 1) {
            query.append(",");
         }
      }
      PreparedStatement statement = connection.prepareStatement(query.toString());
      for (int i = 0; i < articles.size(); ++i) {
         statement.setInt((2 * i) + 1, id);
         statement.setInt((2 * i) + 2, articles.get(i).getId());
      }
      statement.execute();
   }

   default void deleteTuple(Connection connection, String query, int id1, int id2) throws SQLException {
      PreparedStatement statement = connection.prepareStatement(query);
      statement.setInt(1, id1);
      statement.setInt(2, id2);
      statement.execute();
   }
}
