package de.yyco.silencer;

import android.util.Base64;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedObject{
    private byte[] data;
    private byte[] encryptedData;

    public static final String KEY_ALGORITHM            = "PBKDF2WithHmacSHA1";
    public static final int KEY_ITERATIONS              = 1000;
    public static final int KEY_LENGTH                  = 128;
    public static final String CIPHER_TRANSFORMATION    = "AES/GCM/NoPadding";

    private byte[] salt = new byte[KEY_LENGTH/8];
    private byte[] iv = new byte[KEY_LENGTH/8];
    private byte[] key = new byte[KEY_LENGTH/8];

    private static final SecureRandom mSecureRandom = new SecureRandom();

    public byte[] encrypt(String secret) {
        try {
            mSecureRandom.nextBytes(salt);
            mSecureRandom.nextBytes(iv);

            PBEKeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, KEY_ITERATIONS, 128);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey secretKey = skf.generateSecret(spec);
            SecretKeySpec secretKeySpec =new SecretKeySpec(secretKey.getEncoded(), "AES");

            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION, "BC");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);

            this.encryptedData = cipher.doFinal(data);

        }
        catch (Exception e) {
            e.printStackTrace();
            this.encryptedData = null;
        }

        return this.encryptedData;
    }

    EncryptedObject(byte[] data){
        this.data = data;
    }

    public String getMeta(){
        return KEY_LENGTH+":"+KEY_ITERATIONS+":"+Base64.encodeToString(this.salt, Base64.NO_WRAP)+":"+Base64.encodeToString(this.iv, Base64.NO_WRAP);
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
