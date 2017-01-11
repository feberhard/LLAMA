package org.llama.llama.settings;


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
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.common.base.Joiner;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.llama.llama.R;
import org.llama.llama.model.User;
import org.llama.llama.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof MultiSelectListPreference) {
                MultiSelectListPreference listPreference = (MultiSelectListPreference) preference;
                CharSequence[] entries = listPreference.getEntries();
                Set<String> selectedValues = (Set<String>) value;
                SortedSet<String> selectedEntries = new TreeSet<>();

                for (String v : selectedValues) {
                    selectedEntries.add((String) entries[listPreference.findIndexOfValue(v)]);
                }

                preference.setSummary(Joiner.on(", ").join(selectedEntries));
            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static void bindPreferenceSummaryToValue(MultiSelectListPreference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getStringSet(preference.getKey(), null));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
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
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                /*|| DataSyncPreferenceFragment.class.getName().equals(fragmentName)*/
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            UserService userService = new UserService();
            Promise p = userService.getUserInfo(userService.getCurrentUserId());

            final EditTextPreference namePref = (EditTextPreference) findPreference("pref_display_name");
            final Preference usernamePref = findPreference("pref_user_name");
            final EditTextPreference moodPref = (EditTextPreference) findPreference("pref_mood");
            final EditTextPreference emailPref = (EditTextPreference) findPreference("pref_email");
            final MultiSelectListPreference langsPref = (MultiSelectListPreference) findPreference("pref_languages");
            final ListPreference langPref = (ListPreference) findPreference("pref_default_language");

            p.done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    User user = (User)result;

                    namePref.setText(user.getName());
                    namePref.setSummary(user.getName());
                    bindPreferenceSummaryToValue(namePref);

                    usernamePref.setSummary(user.getUsername());
                    bindPreferenceSummaryToValue(usernamePref);

                    moodPref.setText(user.getMood());
                    moodPref.setSummary(user.getMood());
                    bindPreferenceSummaryToValue(moodPref);

                    emailPref.setText(user.getEmail());
                    emailPref.setSummary(user.getEmail());
                    bindPreferenceSummaryToValue(emailPref);

                    // TODO: country

                    setListPreferenceData(langsPref);
                    langsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            setListPreferenceData(langsPref);
                            return false;
                        }
                    });
                    bindPreferenceSummaryToValue(langsPref);

                    // TODO: update entries/values for pref_default_language when pref_languages is changed
                    langPref.setValue(user.getDefaultLanguage());
                    setListPreferenceData(langPref, langsPref, PreferenceManager
                            .getDefaultSharedPreferences(langsPref.getContext())
                            .getStringSet(langsPref.getKey(), null));
                    langPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            setListPreferenceData(langPref, langsPref, PreferenceManager
                                    .getDefaultSharedPreferences(langsPref.getContext())
                                    .getStringSet(langsPref.getKey(), null));
                            return false;
                        }
                    });
                    bindPreferenceSummaryToValue(langPref);
                }
            });
        }

        protected static void setListPreferenceData(ListPreference lp, MultiSelectListPreference langsp, Set<String> values) {
            List<String> entries = new ArrayList<>();
            CharSequence[] entryValues = values.toArray(new CharSequence[entries.size()]);

            CharSequence[] allEntries = langsp.getEntries();

            for (String v : values) {
                entries.add((String) allEntries[langsp.findIndexOfValue(v)]);
            }

            lp.setEntries(entries.toArray(new CharSequence[entries.size()]));
            lp.setDefaultValue(entryValues[0]);
            lp.setEntryValues(entryValues);
        }

        protected static void setListPreferenceData(MultiSelectListPreference lp) {
            CharSequence[] entries = {"Llama", "English", "French", "German", "Italiano", "Español", "العربية", "Русский", "Dutch", "Portuguese", "中文(简体)", "中文（繁體）"};
            CharSequence[] entryValues = {"ll", "en", "fr", "de", "it", "es", "ar", "ru", "nl", "pt", "zh-CN", "zh-TW"};
            lp.setEntries(entries);
            lp.setDefaultValue("en");
            lp.setEntryValues(entryValues);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
