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
import java.io.*;
import java.security.*;
import java.util.Base64;

public class Encryption {
   /**
    * Name of the algorithm used for encryption.
    */
   public static final String ALGORITHM = "RSA";

   /**
    * Name of the format of the keys
    */
   public static final String FORMAT = "X.509";

   /**
    * Location of the private key file.
    */
   public static final String PRIVATE_KEY_FILE;
   /**
    * Location of the public key file.
    */
   public static final String PUBLIC_KEY_FILE;

   /**
    * Location of the base directory for clients public keys
    */
   public static final String PUBLIC_KEY_CLIENT_DIRECTORY;

   static {
      String PRIVATE_KEY_FILE_TMP;
      String PUBLIC_KEY_FILE_TMP;
      String PUBLIC_KEY_CLIENT_DIRECTORY_TMP;
      try {
         PRIVATE_KEY_FILE_TMP = new File(".").getCanonicalPath() + "/keys/private.key";
         PUBLIC_KEY_FILE_TMP = new File(".").getCanonicalPath() + "/keys/public.key";
         PUBLIC_KEY_CLIENT_DIRECTORY_TMP = new File(".").getCanonicalPath() + "/keys/clients";
      } catch (IOException e) {
         e.printStackTrace();
         PRIVATE_KEY_FILE_TMP = null;
         PUBLIC_KEY_FILE_TMP = null;
         PUBLIC_KEY_CLIENT_DIRECTORY_TMP = null;
      }
      PRIVATE_KEY_FILE = PRIVATE_KEY_FILE_TMP;
      PUBLIC_KEY_FILE = PUBLIC_KEY_FILE_TMP;
      PUBLIC_KEY_CLIENT_DIRECTORY = PUBLIC_KEY_CLIENT_DIRECTORY_TMP;
   }

   /**
    * Generate key which contains a pair of private and public key using 1024
    * bytes. Store the set of keys in Private.key and Public.key files.
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
    * Stores the clients public key in the default location
    *
    * @param encodedPublicKey the encoded public key of the client
    * @param clientId         the id of the client
    *
    * @throws IOException if there was an issue while saving the key
    */
   public static void storeClientPublicKey(byte[] encodedPublicKey, int clientId) throws IOException {
      PublicKey publicClientKey = new PublicKey() {
         @Override
         public String getAlgorithm() {
            return ALGORITHM;
         }

         @Override
         public String getFormat() {
            return FORMAT;
         }

         @Override
         public byte[] getEncoded() {
            return encodedPublicKey;
         }
      };
      File clientPublicKeyFile = new File(PUBLIC_KEY_CLIENT_DIRECTORY + "/PC" + clientId + ".key");

      if (clientPublicKeyFile.getParentFile() != null) {
         clientPublicKeyFile.getParentFile().mkdirs();
      }
      clientPublicKeyFile.createNewFile();

      ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(clientPublicKeyFile));
      publicKeyOS.writeObject(publicClientKey);
      publicKeyOS.close();
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
    * Returns the public key created by the generateKey function, if it exists
    * @return the public key
    * @throws IOException if the key does not exist, or if there was an issue while reading the key file
    * @throws ClassNotFoundException if the key files did not contain a public key
    */
   public static PublicKey getPublicKey() throws IOException, ClassNotFoundException {
      ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
      PublicKey publicKey = (PublicKey) inputStream.readObject();
      inputStream.close();
      return publicKey;
   }

   /**
    * Returns the private key created by the generateKey function, if it exists
    * @return the private key
    * @throws IOException if the key does not exist, or if there was an issue while reading the key file
    * @throws ClassNotFoundException if the key files did not contain a private key
    */
   public static PrivateKey getPrivateKey() throws IOException, ClassNotFoundException {
      ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
      PrivateKey privateKey = (PrivateKey) inputStream.readObject();
      inputStream.close();
      return privateKey;
   }

   /**
    * Returns the public key linked to the given user id, if it exists
    * @return the public key linked to the given user
    * @throws IOException if the key does not exist, or if there was an issue while reading the key file
    * @throws ClassNotFoundException if the key files did not contain a public key
    */
   public static PublicKey getClientPublicKey(int clientId) throws IOException, ClassNotFoundException {
      ObjectInputStream inputStream =
              new ObjectInputStream(new FileInputStream(PUBLIC_KEY_CLIENT_DIRECTORY + "/PC" + clientId + ".key"));
      PublicKey publicKey = (PublicKey) inputStream.readObject();
      inputStream.close();
      return publicKey;
   }

   /**
    * Encrypt the plain text using public key.
    *
    * @param text : original plain text
    * @param key  :The public key
    *
    * @return Encrypted text
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
