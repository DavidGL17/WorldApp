package ch.world.app.util;/*
 * @File EncryptionTest.java
 * @Authors : David González León
 * @Date 28 mars 2021
 */

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;

import static org.junit.jupiter.api.Assertions.*;


class EncryptionTest {

   @AfterEach
   void tearDown() throws IOException {
      File directoryKeys = new File(new File(".").getCanonicalPath() + "/keys");
      if (directoryKeys.exists()) {
         FileUtils.deleteDirectory(directoryKeys);
      }
   }

   @Test
   void testKeyGenerationCreatesFiles() {
      assertFalse(new File(Encryption.PUBLIC_KEY_FILE).exists());
      assertFalse(new File(Encryption.PRIVATE_KEY_FILE).exists());
      assertDoesNotThrow(Encryption::generateKey);
      assertTrue(new File(Encryption.PUBLIC_KEY_FILE).exists());
      assertTrue(new File(Encryption.PRIVATE_KEY_FILE).exists());
   }

   @Test
   void testKeyGenerationWorksIfFilesExist() throws IOException, NoSuchAlgorithmException {
      Encryption.generateKey();
      File publicKey = new File(Encryption.PUBLIC_KEY_FILE);
      File privateKey = new File(Encryption.PRIVATE_KEY_FILE);
      assertTrue(publicKey.exists());
      assertTrue(privateKey.exists());
      assertDoesNotThrow(Encryption::generateKey);
   }

   @Test
   void testAreKeyPresentDetectsKeys() throws IOException, NoSuchAlgorithmException {
      assertFalse(Encryption.areKeysPresent());
      Encryption.generateKey();
      assertTrue(Encryption.areKeysPresent());
   }

   @Test
   void testStoreClientKeyCorrectlyStoresTheKey() throws NoSuchAlgorithmException, IOException {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(1024);
      KeyPair key = keyGen.generateKeyPair();

      Encryption.storeClientPublicKey(key.getPublic().getEncoded(), 1);
      assertTrue(new File(Encryption.PUBLIC_KEY_CLIENT_DIRECTORY + "/PC1.key").exists());
   }

   @Test
   void testGetClientPublicKeyWorks() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(1024);
      KeyPair key = keyGen.generateKeyPair();

      Encryption.storeClientPublicKey(key.getPublic().getEncoded(), 1);
      assertDoesNotThrow(() -> {Encryption.getClientPublicKey(1);});
      PublicKey savedKey = Encryption.getClientPublicKey(1);
      assertEquals(key.getPublic(),savedKey);
   }

   @Test
   void testGetPublicAndPrivateKeyWork() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance(Encryption.ALGORITHM);
      keyGen.initialize(1024);
      final KeyPair key = keyGen.generateKeyPair();

      File privateKeyFile = new File(Encryption.PRIVATE_KEY_FILE);
      File publicKeyFile = new File(Encryption.PUBLIC_KEY_FILE);

      // Create files to store public and private key
      if (privateKeyFile.getParentFile() != null) {
         privateKeyFile.getParentFile().mkdirs();
      }
      privateKeyFile.createNewFile();

      if (publicKeyFile.getParentFile() != null) {
         publicKeyFile.getParentFile().mkdirs();
      }
      publicKeyFile.createNewFile();

      // Saving the Public key in a file
      ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
      publicKeyOS.writeObject(key.getPublic());
      publicKeyOS.close();

      // Saving the Private key in a file
      ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
      privateKeyOS.writeObject(key.getPrivate());
      privateKeyOS.close();

      assertDoesNotThrow(Encryption::getPublicKey);
      assertDoesNotThrow(Encryption::getPrivateKey);

      assertEquals(key.getPublic(),Encryption.getPublicKey());
      assertEquals(key.getPrivate(),Encryption.getPrivateKey());
   }

   @Test
   void testEncryptionAndDecryptionWork()
           throws IOException, NoSuchAlgorithmException, ClassNotFoundException, IllegalBlockSizeException,
                  InvalidKeyException, BadPaddingException, NoSuchPaddingException {
      String text = "I want to encrypt and decrypt this text";

      Encryption.generateKey();
      byte[] cypher = Encryption.encrypt(text,Encryption.getPublicKey());
      assertEquals(text,Encryption.decrypt(cypher,Encryption.getPrivateKey()));
   }
}