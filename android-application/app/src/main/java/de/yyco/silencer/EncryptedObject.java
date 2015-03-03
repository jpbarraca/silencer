package de.yyco.silencer;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jpbarraca on 3/3/15.
 */

public class EncryptedObject{
    private byte[] data;
    private byte[] encryptedData;
    private byte[] salt;

    public static final String KEY_ALGORITHM            = "PBKDF2WithHmacSHA1";
    public static final int KEY_ITERATIONS              = 25;
    public static final int KEY_LENGTH                  = 256;

    public static final String CIPHER_TRANSFORMATION    = "AES/GCM/PKCS7Padding";
    public static final byte[] IV                       = new byte[32];


    public void encrypt(String secret) {
        try {
            SecureRandom random = new SecureRandom();
            salt = new byte[EncryptedObject.KEY_LENGTH];
            random.nextBytes(salt);

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            KeySpec keySpec = new PBEKeySpec(secret.toCharArray(), salt, KEY_ITERATIONS, KEY_LENGTH);

            SecretKey temporarySecretKey = secretKeyFactory.generateSecret(keySpec);
            SecretKey secretKey = new SecretKeySpec(temporarySecretKey.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));
            this.encryptedData = cipher.doFinal(data);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    public void decrypt(String secret, byte[] iv, String algorithm, byte[] salt){

    }
*/
    EncryptedObject(byte[] data){
        this.data = data;
    }

    public String getMeta(){
        return Base64.encodeToString(this.salt, Base64.NO_WRAP);
    }

    public String getDataString(){
        return Base64.encodeToString(this.encryptedData, Base64.NO_WRAP);
    }


    public byte[] getSalt() {
        return salt;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    public void setEncryptedData(byte[] data){
        this.encryptedData = data;
    }

    public byte[] getEncryptedData(){
        return this.encryptedData;
    }
}
