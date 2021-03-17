package ch.world.app.client.background.util;/*
 * @File HashMapChainingTest.java
 * @Authors : David González León
 * @Date 17 mars 2021
 */

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashMapChainingTest {
   @Test
   void add() {
      HashMapChaining<String> test = new HashMapChaining<>();
      test.add("Hello");
      assertEquals(1, test.size());
      assertTrue(test.contains("Hello"));
      test.erase("Hello");
      assertEquals(0, test.size());
   }

   @Test
   void forEach() {
      HashMapChaining<Integer> test = new HashMapChaining<>();
      ArrayList<Integer> list = new ArrayList<>();
      for (int i = 0; i < 1000; ++i) {
         test.add(i);
         list.add(i);
      }
      assertEquals(1000, test.size());
      for (Integer i : test) {
         assertTrue(list.contains(i));
      }
   }

   @Test
   void addAllAndClearAndNoDuplicates() {
      HashMapChaining<Integer> test = new HashMapChaining<>();
      ArrayList<Integer> list = new ArrayList<>();
      for (int i = 0; i < 1000; ++i) {
         list.add(i);
      }
      test.addAll(list);
      assertEquals(list.size(), test.size());
      list.clear();
      test.clear();
      assertTrue(test.isEmpty());
      for (int i = 0; i < 10; ++i) {
         list.add(1);
      }
      test.addAll(list);
      assertEquals(1, test.size());
   }

   @Test
   void equalsWoks() {
      HashMapChaining<Integer> test = new HashMapChaining<>();
      HashMapChaining<Integer> test2 = new HashMapChaining<>();
      for (int i = 0; i < 1000; ++i) {
         test.add(i);
         test2.add(i);
      }
      assertEquals(test, test2);
   }
}