/*
 * @File ErrorType.java
 * @Authors : David González León
 * @Date 26 mars 2021
 */
package ch.world.app.util.protocol;

public enum ErrorType {
   SYSTEM_ERROR(500), WRONG_MESSAGE(501), SYNTAX_ERROR(400), REF_ERROR(404);

   private final int value;

   ErrorType(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }
}
