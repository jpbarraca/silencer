package de.yyco.silencer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.security.SecureRandom;

public class MainActivity extends ActionBarActivity {

    public static final String KEYNAME = "cipherKey";

    private SecureStorage ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.ss = SecureStorage.getInstance(this.getApplicationContext());

        findViewById(R.id.buttonViewInstructions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/hauckwill/silencer/blob/master/README.md#chrome-extension"));
                startActivity(i);
            }
        });

        findViewById(R.id.buttonPushBullet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = MainActivity.this.getPackageManager();
                Intent appStartIntent = pm.getLaunchIntentForPackage("com.pushbullet.android");
                if (null != appStartIntent)
                {
                    MainActivity.this.startActivity(appStartIntent);
                }
            }
        });

        findViewById(R.id.buttonNotificationListener).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS));
                }
                else {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            }
        });

        findViewById(R.id.buttonRandomizeKey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = MainActivity.this.generateRandomKey();
                EditText etKey = (EditText) findViewById(R.id.editText);
                etKey.setText(key);
                ss.set(MainActivity.KEYNAME, key);
            }
        });


        findViewById(R.id.buttonShowHideKey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etKey = (EditText) findViewById(R.id.editText);
                Button btn = (Button) v;
                if(btn.getText().equals("Show")){
                    btn.setText("Hide");
                    etKey.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else {
                    btn.setText("Show");
                    etKey.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        findViewById(R.id.buttonTestMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(v.getContext())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("Silencer")
                                .setContentText("Test Message for Pushbullet Silencer");

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationUtilities.process(v.getContext(),mBuilder.build(), "com.pushbullet.android",1);

            }
        });


        EditText etKey = (EditText) findViewById(R.id.editText);
        String  key = ss.get(MainActivity.KEYNAME, null);

        if(key == null){
            key = generateRandomKey();
            ss.set(MainActivity.KEYNAME, key);
        }
        etKey.setText(key);

        etKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ss.set(MainActivity.KEYNAME, s.toString());
            }
        });
    }

    public static String generateRandomKey() {
        byte rkey[] = new byte[20];
        SecureRandom random = new SecureRandom();
        random.nextBytes(rkey);
        return Base64.encodeToString(rkey, Base64.NO_WRAP | Base64.NO_PADDING);
    }
}
