package de.yyco.silencer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.security.SecureRandom;

public class MainActivity extends ActionBarActivity {

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();

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

        EditText key = (EditText) findViewById(R.id.editText);

        //If there is no key, lets generate one
        if(settings.getString(NotificationUtilities.PREF_KEY,"").length() == 0){
            SecureRandom random = new SecureRandom();
            byte[] keyData = new byte[16];
            random.nextBytes(keyData);

            editor.putString(NotificationUtilities.PREF_KEY,Base64.encodeToString(keyData, Base64.NO_WRAP));
        }

        key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString(NotificationUtilities.PREF_KEY, s.toString()).commit();
            }
        });
    }
}
