/*
 * @File ErrorType.java
 * @Authors : David González León
 * @Date 26 mars 2021
 */
package ch.world.app.util.protocol;

/**
 * The enum Error type, that describes the errors defined in the Protocol.md file.
 */
public enum ErrorType {

   SYSTEM_ERROR(500), WRONG_MESSAGE(501), SYNTAX_ERROR(400), REF_ERROR(404);

   private final int value;

   ErrorType(int value) {
      this.value = value;
   }

   /**
    * Gets the value of the ErrorType.
    *
    * @return the value
    */
   public int getValue() {
      return value;
   }

   /**
    * Gets the error type that coincides with the given value.
    *
    * @param value the value
    *
    * @return the error type with the value, or null if the value does not correspond to an ErrorType.
    */
   public static ErrorType getErrorTypeWithValue(int value) {
      switch (value) {
         case 400:
            return SYNTAX_ERROR;
         case 404:
            return REF_ERROR;
         case 500:
            return SYSTEM_ERROR;
         case 501:
            return WRONG_MESSAGE;
         default:
            return null;
      }
   }
}
