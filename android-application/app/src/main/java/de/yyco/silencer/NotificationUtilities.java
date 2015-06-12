package de.yyco.silencer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;


@SuppressWarnings("deprecation")
public class NotificationUtilities {
    public static boolean process(Context c, Notification n, String packageName, int id) {

        if (packageName.equals(c.getPackageName()))
            return true;


        ArrayList<String> notificationData = null;
        // Magically extract text from notification
        try {
            notificationData = NotificationUtilities.getNotificationText(n);
        }catch(Exception e){
            return false;
        }

        SecureStorage ss = SecureStorage.getInstance(c);

        String secret = ss.get(MainActivity.KEYNAME,"");

        // Use PackageManager to get application name and icon
        final PackageManager pm = c.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final NameNotFoundException e) {
            return false;
        }

        // Create header and body of notification
        String notificationBody = "";
        String notificationHeader = "";

        if (notificationData != null && notificationData.size() > 0) {
            notificationHeader = notificationData.get(0);
            if (notificationData.size() > 1) {
                notificationBody = notificationData.get(1);
            }
        } else {
            return false;
        }

        for (int i = 2; i < notificationData.size(); i++) {
            notificationBody += "\n" + notificationData.get(i);
        }


        // Create JSON object with all necessary information
        JSONObject obj = new JSONObject();
        try {
            obj.put("p", packageName);
            obj.put("l", ai.loadLabel(pm).toString());
            obj.put("t", notificationHeader);
            obj.put("b", notificationBody);
            obj.put("n", Integer.toString(id));
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        // Show notification momentarily so it is picked up by PushBullet
        Notification.Builder mBuilder = null;
        try {
            EncryptedObject eo;
            String body = obj.toString();
            String meta = "PLAIN";

            if (secret != null &&  !secret.isEmpty()) {

                eo = new EncryptedObject(body.getBytes("UTF-8"));
                eo.encrypt(secret);
                body = eo.getDataString();
                meta = eo.getMeta();
            }

            mBuilder = new Notification.Builder(c)
                    .setContentTitle(meta)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setPriority(Notification.PRIORITY_LOW)
                    .setLargeIcon(n.largeIcon != null ? n.largeIcon : drawableToBitmap(pm.getApplicationIcon(ai)));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        NotificationManager mNotificationManager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
        mNotificationManager.cancel(0);

        return true;
    }

    @SuppressLint("DefaultLocale")
    public static ArrayList<String> getNotificationText(Notification notification) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Bundle extras = notification.extras;

            ArrayList<String> notificationData = new ArrayList<String>();

            if (extras.getString("android.title") != null)
                notificationData.add(extras.getString("android.title"));
            if (extras.getCharSequence("android.text") != null)
                notificationData.add(extras.getCharSequence("android.text").toString());
            if (extras.getString("android.subText") != null)
                notificationData.add(extras.getString("android.subText"));

            return notificationData;
        } else {
            RemoteViews views = notification.contentView;
            Class<?> secretClass = views.getClass();

            try {
                ArrayList<String> notificationData = new ArrayList<String>();

                Field outerFields[] = secretClass.getDeclaredFields();
                for (int i = 0; i < outerFields.length; i++) {
                    if (!outerFields[i].getName().equals("mActions"))
                        continue;

                    outerFields[i].setAccessible(true);

                    @SuppressWarnings("unchecked")
                    ArrayList<Object> actions = (ArrayList<Object>) outerFields[i]
                            .get(views);
                    for (Object action : actions) {
                        Field innerFields[] = action.getClass().getDeclaredFields();

                        Object value = null;
                        for (Field field : innerFields) {
                            field.setAccessible(true);
                            // Value field could possibly contain text
                            if (field.getName().equals("value")) {
                                value = field.get(action);
                            }
                        }

                        // Check if value is a String
                        if (value != null
                                && value.getClass().getName().toUpperCase()
                                .contains("STRING")) {

                            notificationData.add(value.toString());
                        }
                    }

                    return notificationData;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
