package com.learning.firhan.todo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.learning.firhan.todo.Constant.SettingNames;
import com.learning.firhan.todo.Helpers.SettingDatabaseHelper;
import com.learning.firhan.todo.Models.Setting;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        getFragmentManager().
                beginTransaction().
                replace(android.R.id.content, new GeneralPreferenceFragment()).
                commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        SettingDatabaseHelper settingDatabaseHelper;
        Setting pinObj;
        SwitchPreference enablePin;
        Preference changePin;
        Context context;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            //set context
            context = getActivity().getApplicationContext();

            //init db
            settingDatabaseHelper = new SettingDatabaseHelper(getActivity());
            enablePin = (SwitchPreference)findPreference("enablePin");
            changePin = (Preference) findPreference("changePin");

            pinObj = settingDatabaseHelper.getSettingByTitle(SettingNames.PIN_CODE, false);
            //check if pin already set/not
            Setting enablePinObj = settingDatabaseHelper.getSettingByTitle(SettingNames.ENABLED_PIN, false);
            boolean isChecked = false;
            if(enablePinObj!=null){
                if(enablePinObj.getValue().equals("True")){
                    isChecked = true;
                }
            }
            enablePin.setChecked(isChecked);

            enablePin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(pinObj==null){
                        //goto setting pin
                        Intent pinIntent = new Intent(context, PinActivity.class);
                        pinIntent.putExtra("setPin", true);
                        pinIntent.putExtra("setEnablePin", true);
                        startActivity(pinIntent);
                    }else {
                        String value = "";
                        if ((Boolean) newValue) {
                            value = "True";
                        } else {
                            value = "False";
                        }
                        //update db
                        long rowId = settingDatabaseHelper.updateData(SettingNames.ENABLED_PIN, value);
                        if (rowId != 0) {
                            //success
                            Toast.makeText(context, "Success, Enable Pin: " + value, Toast.LENGTH_SHORT).show();
                        } else {
                            //failed
                            Toast.makeText(context, "Enable Pin Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    return true;
                }
            });

            changePin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //goto setting pin
                    Intent pinIntent = new Intent(context, PinActivity.class);
                    pinIntent.putExtra("setPin", true);
                    startActivity(pinIntent);

                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch (id){
                case 16908332:
                    getActivity().finish();
                    break;
            }
            return true;
        }
    }
}
