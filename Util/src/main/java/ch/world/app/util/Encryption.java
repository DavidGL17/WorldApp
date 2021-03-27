/*
 * @File Encryption.java
 * @Authors : David González León
 * @Date 27 mars 2021
 */
package ch.world.app.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.*;

public class Encryption {
   /**
    * Name of the algorithm used for encryption.
    */
   public static final String ALGORITHM = "RSA";

   /**
    * Location of the private key file.
    */
   public static final String PRIVATE_KEY_FILE;
   /**
    * Location of the public key file.
    */
   public static final String PUBLIC_KEY_FILE;

   static {
      String PRIVATE_KEY_FILE_TMP;
      String PUBLIC_KEY_FILE_TMP;
      try {
         PRIVATE_KEY_FILE_TMP = new File(".").getCanonicalPath() + "/keys/private.key";
         PUBLIC_KEY_FILE_TMP = new File(".").getCanonicalPath() + "/keys/public.key";
      } catch (IOException e) {
         e.printStackTrace();
         PRIVATE_KEY_FILE_TMP = null;
         PUBLIC_KEY_FILE_TMP = null;
      }
      PRIVATE_KEY_FILE = PRIVATE_KEY_FILE_TMP;
      PUBLIC_KEY_FILE = PUBLIC_KEY_FILE_TMP;
   }

   /**
    * Generate key which contains a pair of private and public key using 1024
    * bytes. Store the set of keys in Prvate.key and Public.key files.
    *
    * @throws NoSuchAlgorithmException
    * @throws IOException
    */
   public static void generateKey() throws IOException, NoSuchAlgorithmException {
      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
      keyGen.initialize(1024);
      final KeyPair key = keyGen.generateKeyPair();

      File privateKeyFile = new File(PRIVATE_KEY_FILE);
      File publicKeyFile = new File(PUBLIC_KEY_FILE);

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
   }


   /**
    * The method checks if the pair of public and private key has been generated.
    *
    * @return flag indicating if the pair of keys were generated.
    */
   public static boolean areKeysPresent() {
      File privateKey = new File(PRIVATE_KEY_FILE);
      File publicKey = new File(PUBLIC_KEY_FILE);

      if (privateKey.exists() && publicKey.exists()) {
         return true;
      }
      return false;
   }

   /**
    * Encrypt the plain text using public key.
    *
    * @param text : original plain text
    * @param key  :The public key
    *
    * @return Encrypted text
    *
    */
   public static byte[] encrypt(String text, PublicKey key)
           throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException,
                  InvalidKeyException {
      byte[] cipherText = null;
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // encrypt the plain text using the public key
      cipher.init(Cipher.ENCRYPT_MODE, key);
      cipherText = cipher.doFinal(text.getBytes());
      return cipherText;
   }

   /**
    * Decrypt text using private key.
    *
    * @param text :encrypted text
    * @param key  :The private key
    *
    * @return plain text
    *
    */
   public static String decrypt(byte[] text, PrivateKey key)
           throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException,
                  InvalidKeyException {
      byte[] dectyptedText = null;
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);

      // decrypt the text using the private key
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(text);


      return new String(dectyptedText);
   }

}
