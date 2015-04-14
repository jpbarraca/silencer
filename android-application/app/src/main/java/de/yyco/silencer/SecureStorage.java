package de.yyco.silencer;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import android.provider.Settings.Secure;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class SecureStorage {

    private static final String APP_KEY         = "rXc7BGatrU89sX7F"; //Oh well...

    private static char[] password;
    private static SecureStorage ss = null;
    private String keyStorePath = "";

    public static SecureStorage getInstance(Context ctx){
        if(ss != null)
            return ss;

        ss = new SecureStorage(ctx);
        return ss;
    }
    //Here be Dragons!
    //This is insanely ugly and FAR from actually secure
    //However it is the best we can get for now...
    private SecureStorage(Context ctx){
        password = (APP_KEY+Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID)).toCharArray();
        this.keyStorePath = ctx.getFilesDir().getAbsolutePath()+"key.store";
    }


    synchronized public boolean set(String name, String value) {
        if(value == null || value.length() == 0)
            return false;

        try {

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, SecureStorage.password);
            SecretKey key = new SecretKeySpec(value.getBytes(), "RAW");

            ks.setKeyEntry(name, key, null, null);
            FileOutputStream writeStream = new FileOutputStream(this.keyStorePath);

            ks.store(writeStream, SecureStorage.password);
            writeStream.close();

            return true;

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    synchronized  public String get(String name, String defaultValue){
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

            FileInputStream fis = new FileInputStream(this.keyStorePath);

            ks.load(fis, SecureStorage.password);

            SecretKey key = null;

            try {
                key = (SecretKey) ks.getKey(name, null);
            }catch(Exception e){
                e.printStackTrace();
            }

            if(key != null) {
                return new String(key.getEncoded(), "UTF-8");
            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

}
