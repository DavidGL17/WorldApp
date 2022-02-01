package ch.world.app.util;/*
 * @File LoggingTest.java
 * @Authors : David González León
 * @Date 26 mars 2021
 */

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoggingTest {

   private static File logDirectory;
   private final ByteArrayOutputStream output = new ByteArrayOutputStream();
   private Logger logger;

   @BeforeAll
   static void beforeAll() {
      logDirectory = new File(Logging.getCurrentPath() + "/logs");
      if (!logDirectory.exists()) {
         logDirectory.mkdirs();
      }
   }

   @AfterAll
   static void afterAll() {
      try {
         FileUtils.deleteDirectory(logDirectory);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @BeforeEach
   void setUp() {
   }

   @Test
   void getSetuppedLoggerWorksWithNullParam() {
      assertThrows(IllegalArgumentException.class, () -> {Logging.getSetuppedLogger(null, "");});
      assertThrows(IllegalArgumentException.class, () -> {Logging.getSetuppedLogger("null", null);});
   }

   @Test
   void getCurrentPathReturnsTheCorrectPath() throws IOException {
      String path = new File(".").getCanonicalPath();
      assertEquals(path, Logging.getCurrentPath());
   }

   @AfterEach
   void tearDown() {
      if (logger != null) {
         Handler[] handlers = logger.getHandlers();
         for (Handler h : handlers) {
            h.close();
            logger.removeHandler(h);
         }
      }
   }
}