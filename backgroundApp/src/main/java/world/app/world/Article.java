/*
 * @File Article.java
 * @Authors : David González León,
 * @Date 16 déc. 2020
 */
package world.app.world;

import util.HashMapChaining;
import world.app.world.articles.Character;
import world.app.world.articles.*;
import world.app.world.articles.events.Accord;
import world.app.world.articles.events.AccordType;
import world.app.world.articles.events.War;

import java.sql.*;
import java.util.ArrayList;

//TODO reimplémenter equals dans chaque sous-classe et start et stopModification

/**
 * The type Article.
 */
public abstract class Article implements Modifiable {
   private final TypeOfArticle typeOfArticle;
   private final int id;
   private final World world;
   private String name;
   private String content;
   private Date lastUpdate;
   private boolean inModification = false;
   private boolean updateNeeded = false;

   /**
    * Instantiates a new Article.
    *
    * @param id            the id of the article
    * @param world         the world of the article
    * @param name          the name of the article
    * @param content       the content of the article
    * @param lastUpdate    the last update of the article
    * @param typeOfArticle the type of article
    */
   public Article(int id, World world, String name, String content, Date lastUpdate, TypeOfArticle typeOfArticle) {
      this.id = id;
      this.world = world;
      this.name = name;
      this.content = content;
      this.lastUpdate = lastUpdate;
      this.typeOfArticle = typeOfArticle;
   }

   /**
    * Loads all the alphabets articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the alphabets articles linked to the given world
    */
   public static ArrayList<Article> loadAlphabets(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.alphabet a2 ON a.id = a2.idArticle " +
                 "WHERE idWorld = ?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            results.add(new Alphabet(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                     resultSet.getString("content"), resultSet.getDate("last_update"),
                                     resultSet.getString("writingSystem")));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the languages articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the languages articles linked to the given world
    */
   public static ArrayList<Article> loadLanguages(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.language l ON a.id = l.idArticle WHERE" +
                 " a.idWorld = ?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            results.add(new Language(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                     resultSet.getString("content"), resultSet.getDate("last_update"),
                                     (Alphabet) world.getArticleWithId(resultSet.getInt("idAlphabet")),
                                     new HashMapChaining<>()));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the races articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the races articles linked to the given world
    */
   public static ArrayList<Article> loadRaces(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.race r ON a.id = r.idArticle WHERE " +
                 "idWorld=?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            results.add(new Race(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                 resultSet.getString("content"), resultSet.getDate("last_update"),
                                 resultSet.getInt("heightMin"), resultSet.getInt("heightMax"),
                                 (Language) world.getArticleWithId(resultSet.getInt("idLanguage"))));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the legends articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the legends articles linked to the given world
    */
   public static ArrayList<Article> loadLegends(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.legend l ON a.id = l.idArticle WHERE " +
                 "idWorld=?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            statement = connection.prepareStatement(
                    "SELECT lr.idRace FROM worldproject.legend l INNER JOIN worldproject.legend_race lr ON l" +
                    ".idArticle = lr.idLegend WHERE l.idArticle=?");
            statement.setInt(1, resultSet.getInt("id"));
            ResultSet racesResult = statement.executeQuery();
            HashMapChaining<Race> races = new HashMapChaining<>();
            while (racesResult.next()) {
               races.add((Race) world.getArticleWithId(racesResult.getInt(1)));
            }
            results.add(new Legend(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                   resultSet.getString("content"), resultSet.getDate("last_update"), races));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the characters articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the characters articles linked to the given world
    */
   public static ArrayList<Article> loadCharacters(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article INNER JOIN worldproject.character c ON article.id = c.idArticle " +
                 "WHERE idWorld=?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            statement = connection.prepareStatement(
                    "SELECT cl.idLanguage FROM worldproject.character c INNER JOIN worldproject.character_language cl" +
                    " ON c.idArticle = cl.idCharacter WHERE c.idArticle=?");
            statement.setInt(1, resultSet.getInt("id"));
            ResultSet languagesResult = statement.executeQuery();
            HashMapChaining<Language> languages = new HashMapChaining<>();
            while (languagesResult.next()) {
               languages.add((Language) world.getArticleWithId(languagesResult.getInt(1)));
            }
            results.add(new Character(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                      resultSet.getString("content"), resultSet.getDate("last_update"),
                                      (Race) world.getArticleWithId(resultSet.getInt("idRace")), languages));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the continent articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the continent articles linked to the given world
    */
   public static ArrayList<Article> loadContinent(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.continent c ON a.id = c.idArticle " +
                 "WHERE idWorld = ?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            results.add(new Continent(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                      resultSet.getString("content"), resultSet.getDate("last_update"),
                                      new HashMapChaining<>()));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the country articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the country articles linked to the given world
    */
   public static ArrayList<Article> loadCountry(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.country c ON a.id = c.idArticle WHERE " +
                 "idWorld=?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            statement = connection.prepareStatement(
                    "SELECT cr.idRace FROM worldproject.country c INNER JOIN worldproject.country_race cr ON c" +
                    ".idArticle = cr.idCountry WHERE " + "c.idArticle=?");
            statement.setInt(1, resultSet.getInt("idArticle"));
            ResultSet racesResult = statement.executeQuery();
            HashMapChaining<Race> races = new HashMapChaining<>();
            while (racesResult.next()) {
               races.add((Race) world.getArticleWithId(racesResult.getInt("idRace")));
            }
            results.add(new Country(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                    resultSet.getString("content"), resultSet.getDate("last_update"),
                                    (Continent) world.getArticleWithId(resultSet.getInt("idContinent")),
                                    new HashMapChaining<>(), races, new HashMapChaining<>(), new HashMapChaining<>()));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the rank articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the rank articles linked to the given world
    */
   public static ArrayList<Article> loadRank(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.rank r ON a.id = r.idArticle WHERE " +
                 "idWorld=?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            results.add(new Rank(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                 resultSet.getString("content"), resultSet.getDate("last_update"),
                                 resultSet.getDouble("salary"), resultSet.getString("additionalRights"),
                                 (Country) world.getArticleWithId(resultSet.getInt("idCountry"))));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the side articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the side articles linked to the given world
    */
   public static ArrayList<Article> loadSide(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.side s ON a.id = s.idArticle WHERE " +
                 "idWorld=?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            statement = connection.prepareStatement(
                    "SELECT cs.idCountry FROM worldproject.side s INNER JOIN worldproject.country_side cs ON s" +
                    ".idArticle = cs.idSide WHERE s.idArticle=?");
            statement.setInt(1, resultSet.getInt("idArticle"));
            ResultSet countryResults = statement.executeQuery();
            HashMapChaining<Country> countries = new HashMapChaining<>();
            while (countryResults.next()) {
               countries.add((Country) world.getArticleWithId(countryResults.getInt("idCountry")));
            }
            results.add(new Side(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                 resultSet.getString("content"), resultSet.getDate("last_update"), countries));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the war articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the war articles linked to the given world
    */
   public static ArrayList<Article> loadWar(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.war w ON a.id = w.idArticle WHERE " +
                 "idWorld=?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            results.add(new War(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                resultSet.getString("content"), resultSet.getDate("last_update"),
                                resultSet.getString("dateBeginning"), resultSet.getString("dateEnd"),
                                resultSet.getInt("deathCount"),
                                (Side) world.getArticleWithId(resultSet.getInt("idSide1")),
                                (Side) world.getArticleWithId(resultSet.getInt("idSide2"))));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the accordType articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the accordType articles linked to the given world
    */
   public static ArrayList<Article> loadAccordType(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.accordtype a2 ON a.id = a2.idArticle " +
                 "WHERE idWorld=?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            results.add(new AccordType(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                       resultSet.getString("content"), resultSet.getDate("last_update")));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Loads all the accord articles linked to the given world
    *
    * @param world the world given
    *
    * @return all the accord articles linked to the given world
    */
   public static ArrayList<Article> loadAccord(World world) {
      Connection connection = world.getUser().getConnection();
      ArrayList<Article> results = new ArrayList<>();
      try {
         PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM worldproject.article a INNER JOIN worldproject.accord a2 ON a.id = a2.idArticle WHERE" +
                 " idWorld=?");
         statement.setInt(1, world.getId());
         ResultSet resultSet = statement.executeQuery();
         while (resultSet.next()) {
            statement = connection.prepareStatement(
                    "SELECT ac.idCountry FROM worldproject.accord a INNER JOIN worldproject.accord_country ac ON a" +
                    ".idArticle = ac.idAccord WHERE ac.idAccord=?");
            statement.setInt(1, resultSet.getInt("idArticle"));
            ResultSet countryResults = statement.executeQuery();
            HashMapChaining<Country> countries = new HashMapChaining<>();
            while (countryResults.next()) {
               countries.add((Country) world.getArticleWithId(countryResults.getInt("idCountry")));
            }
            results.add(new Accord(resultSet.getInt("idArticle"), world, resultSet.getString("name"),
                                   resultSet.getString("content"), resultSet.getDate("last_update"),
                                   resultSet.getString("dateBeginning"), resultSet.getString("dateEnd"),
                                   (AccordType) world.getArticleWithId(resultSet.getInt("idAccordType")), countries));
         }
      } catch (SQLException throwables) {
         throwables.printStackTrace();
      }
      return results;
   }

   /**
    * Indicates if the article is in modification
    *
    * @return true if it is, false otherwise
    */
   public boolean isInModification() {
      return inModification;
   }

   /**
    * Indicates if an update is needed
    *
    * @return true if it is, false otherwise
    */
   public boolean isUpdateNeeded() {
      return updateNeeded;
   }

   /**
    * Gets the id of the article
    *
    * @return the id of the article
    */
   public int getId() {
      return id;
   }

   /**
    * Gets world.
    *
    * @return the world of the article
    */
   public World getWorld() {
      return world;
   }

   /**
    * Gets name.
    *
    * @return the name of the article
    */
   public String getName() {
      return name;
   }

   public void setName(String name) {
      if (name != null && inModification) {
         updateNeeded = true;
         this.name = name;
      }
   }

   /**
    * Gets content.
    *
    * @return the content of the article
    */
   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      if (content != null && inModification) {
         this.content = content;
         updateNeeded = true;
      }
   }

   /**
    * Gets last update.
    *
    * @return the last update of the article
    */
   public Date getLastUpdate() {
      return lastUpdate;
   }

   /**
    * Gets type of article.
    *
    * @return the type of article
    */
   public TypeOfArticle getTypeOfArticle() {
      return typeOfArticle;
   }

   @Override
   public int hashCode() {
      return getId();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof Article)) { return false; }
      Article article = (Article) o;
      return getId() == article.getId();
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + "{" + "name : '" + name + '\'' + ", content : '" + content + '\'';
   }

   @Override
   public void startModifications() {
      inModification = true;
   }

   /**
    * Needs to be called at the end of the function by all the sub-classes
    *
    * @return true if the modifications were ended and saved successfully, otherwise returns false
    */
   @Override
   public boolean endModifications() {
      if (updateNeeded) {
         Connection connection = world.getUser().getConnection();
         try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE worldproject.article SET name = ?, content = ? WHERE id = ? RETURNING last_update");
            statement.setString(1, name);
            statement.setString(2, content);
            statement.setInt(3, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
               lastUpdate = resultSet.getDate("last_update");
            }
         } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Error while saving changes of article");
            return false;
         }
         updateNeeded = false;
      }
      inModification = false;
      return true;
   }

   protected static int createArticle(World world, String name, String content) throws SQLException {
      int id = 0;
      Connection connection = world.getUser().getConnection();
      PreparedStatement statement = connection.prepareStatement(
              "INSERT INTO worldproject.article(idworld, name, content) VALUES (?,?,?) RETURNING id");
      statement.setInt(1, world.getId());
      statement.setString(2, name);
      statement.setString(3, content);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
         id = resultSet.getInt("id");
      }
      return id;
   }

   void test() {

   }

   protected void setUpdateIsNeeded() {
      this.updateNeeded = true;
   }

}
