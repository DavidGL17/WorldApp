/*
 * @File HashMapChaining.java
 * @Authors : David González León
 * @Date 10 janv. 2021
 */
package util;

import java.util.*;
import java.util.function.Consumer;

/**
 * The type Hash map chaining.
 *
 * @param <T> the type parameter
 */
public class HashMapChaining<T> implements Iterable<T> {
   private final int defaultMaxSize = 2;
   private int maxSizeOfTab = defaultMaxSize;
   private int currentSize;
   private ArrayList<LinkedList<T>> map = createMap(maxSizeOfTab);

   /**
    * Instantiates a new Hash map chaining.
    */
   public HashMapChaining() {
   }

   /**
    * Add an element to the map if it doesnt already exist
    *
    * @param element the element
    */
   public void add(T element) {
      if (element != null) {
         if (!contains(element)) {
            int hash = getHash(element);
            map.get(hash).push(element);
            ++currentSize;
            resize();
         }
      }
   }

   /**
    * Add all.
    *
    * @param list the list
    */
   public void addAll(ArrayList<T> list) {
      for (T t : list) {
         add(t);
      }
   }

   /**
    * Contains boolean.
    *
    * @param element the element
    *
    * @return the boolean
    */
   public boolean contains(T element) {
      if (element != null) {
         int hash = getHash(element);
         LinkedList<T> list = map.get(hash);
         for (T t : list) {
            if (element.equals(t)) {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Find an element in the hashMap with the same hash as the one given
    *
    * @param hash the hash of the element
    *
    * @return the element if found or null if there is no element with this hash in the list
    */
   public T find(int hash) {
      LinkedList<T> list = map.get(hash % maxSizeOfTab);
      for (T t : list) {
         if (t.hashCode() == hash) {
            return t;
         }
      }
      return null;
   }

   /**
    * Erase.
    *
    * @param element the element
    */
   public boolean erase(T element) {
      if (element != null) {
         int hash = getHash(element);
         LinkedList<T> list = map.get(hash);
         if (list.remove(element)) {
            --currentSize;
            resize();
            return true;
         }
      }
      return false;
   }

   /**
    * Erases all elements of the HashMap
    */
   public void clear() {
      map = createMap(defaultMaxSize);
      currentSize = 0;
      maxSizeOfTab = defaultMaxSize;
   }

   /**
    * Size int.
    *
    * @return the int
    */
   public int size() {
      return currentSize;
   }

   /**
    * Is empty boolean.
    *
    * @return the boolean
    */
   public boolean isEmpty() {
      return currentSize == 0;
   }

   /**
    * Copies the content of the map into a new map. Does not make a profound copy of the elements, so any modification
    * of the elements in the copied map will impact this instance.
    *
    * @return a new map containing the same elements as this one.
    */
   public HashMapChaining<T> copy() {
      HashMapChaining<T> newMap = new HashMapChaining<>();
      for (T t : this) {
         newMap.add(t);
      }
      return newMap;
   }

   @Override
   public int hashCode() {
      return Objects.hash(defaultMaxSize, maxSizeOfTab, currentSize, map);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) { return true; }
      if (!(o instanceof HashMapChaining)) { return false; }
      HashMapChaining<?> that = (HashMapChaining<?>) o;
      return maxSizeOfTab == that.maxSizeOfTab && currentSize == that.currentSize && Objects.equals(map, that.map);
   }

   @Override
   public Iterator<T> iterator() {
      return new HashMapChainingIterator();
   }

   @Override
   public void forEach(Consumer<? super T> action) {
      for (LinkedList<T> l : map) {
         for (T t : l) {
            action.accept(t);
         }
      }
   }

   /**
    * Gets hash.
    *
    * @param element the element
    *
    * @return the hash
    */
   private int getHash(T element) {
      return element.hashCode() % maxSizeOfTab;
   }

   /**
    * Resize.
    */
   private void resize() {
      if (currentSize / maxSizeOfTab <= 2) {
         if (maxSizeOfTab == 1) {
            return;
         }
         maxSizeOfTab /= 2;
      } else if (currentSize / maxSizeOfTab >= 8) {
         maxSizeOfTab *= 2;
      } else {
         return;
      }

      ArrayList<LinkedList<T>> newMap = createMap(maxSizeOfTab);
      for (LinkedList<T> l : map) {
         for (T t : l) {
            newMap.get(getHash(t)).push(t);
         }
      }
      map = newMap;
   }

   /**
    * Returns a container with the targeted size
    *
    * @param size the size of the array list
    *
    * @return the created container
    */
   private ArrayList<LinkedList<T>> createMap(int size) {
      ArrayList<LinkedList<T>> map = new ArrayList<>();
      for (int i = 0; i < size; ++i) {
         map.add(new LinkedList<>());
      }
      return map;
   }

   /**
    * The type Hash map chaining iterator.
    */
   class HashMapChainingIterator implements Iterator<T> {
      /**
       * The Current list iterator.
       */
      final Iterator<LinkedList<T>> currentListIterator = map.iterator();
      /**
       * The Current list.
       */
      LinkedList<T> currentList = map.get(0);
      /**
       * The Current element iterator.
       */
      Iterator<T> currentElementIterator = null;


      @Override
      public boolean hasNext() {
         return currentElementIterator != null && currentElementIterator.hasNext() || currentListIterator.hasNext();
      }

      @Override
      public T next() {
         if (currentElementIterator != null && currentElementIterator.hasNext()) {
            return currentElementIterator.next();
         } else if (currentListIterator.hasNext()) {
            do {
               currentList = currentListIterator.next();
               currentElementIterator = currentList.iterator();
            } while (!currentElementIterator.hasNext());
            return currentElementIterator.next();
         }
         throw new NoSuchElementException("The iteration is over");
      }
   }
}
