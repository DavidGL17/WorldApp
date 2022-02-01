package ch.world.app.util.protocol;/*
 * @File ErrorTypeTest.java
 * @Authors : David González León
 * @Date 27 mars 2021
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ErrorTypeTest {

   @Test
   void testErrorTypesHaveTheCorrectValue() {
      assertEquals(500, ErrorType.SYSTEM_ERROR.getValue());
      assertEquals(501, ErrorType.WRONG_MESSAGE.getValue());
      assertEquals(400, ErrorType.SYNTAX_ERROR.getValue());
      assertEquals(404, ErrorType.REF_ERROR.getValue());
   }

   @Test
   void testGetErrorTypeWithValueReturnsCorrectErrorType() {
      assertEquals(ErrorType.SYSTEM_ERROR, ErrorType.getErrorTypeWithValue(500));
      assertEquals(ErrorType.WRONG_MESSAGE, ErrorType.getErrorTypeWithValue(501));
      assertEquals(ErrorType.SYNTAX_ERROR, ErrorType.getErrorTypeWithValue(400));
      assertEquals(ErrorType.REF_ERROR, ErrorType.getErrorTypeWithValue(404));
      assertNull(ErrorType.getErrorTypeWithValue(1));
   }

}