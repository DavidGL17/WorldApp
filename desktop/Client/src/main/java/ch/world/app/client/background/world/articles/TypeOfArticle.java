/*
 * @File TypeOfArticle.java
 * @Authors : David González León,
 * @Date 25 déc. 2020
 */
package ch.world.app.client.background.world.articles;

public enum TypeOfArticle {
   ACCORD("Accord"), ACCORD_TYPE("Accord type"), ALPHABET("Alphabet"), CHARACTER("Character"), CONTINENT("Continent"),
   COUNTRY("Country"), LANGUAGE("Language"), LEGEND("Legend"), RACE("Race"), RANK("Rank"), SIDE("Side"), WAR("War");


   private final String name;

   TypeOfArticle(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }
}
