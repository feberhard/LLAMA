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
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.base.Joiner;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.llama.llama.MyApp;
import org.llama.llama.R;
import org.llama.llama.model.Country;
import org.llama.llama.model.Language;
import org.llama.llama.model.User;
import org.llama.llama.services.CountryService;
import org.llama.llama.services.ICountryService;
import org.llama.llama.services.ILanguageService;
import org.llama.llama.services.IUserService;
import org.llama.llama.services.LanguageService;
import org.llama.llama.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        private ListPreference langPref;

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            final String stringValue = value.toString();

            IUserService userService = new UserService();

            switch (preference.getKey()) {
                case "pref_user_name":
                    final EditTextPreference pref = (EditTextPreference) preference;
                    final String oldUsername = pref.getText();

                    if (!stringValue.equals(pref.getText())) {
                        userService.updateCurrentUserName(stringValue, new Runnable() {
                            @Override
                            public void run() {
                                pref.setText(oldUsername);
                                pref.setSummary(oldUsername);

                                Context context = MyApp.getAppContext();
                                CharSequence text = "Username '" + stringValue + "' is already taken.";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        });
                    }
                    break;
                case "pref_display_name":
                    if (!stringValue.equals(((EditTextPreference) preference).getText())) {
                        userService.updateCurrentUserDisplayName(stringValue);
                    }
                    break;
                case "pref_mood":
                    if (!stringValue.equals(((EditTextPreference) preference).getText())) {
                        userService.updateCurrentUserMood(stringValue);
                    }
                    break;
                case "pref_email":
                    if (!stringValue.equals(((EditTextPreference) preference).getText())) {
                        userService.updateCurrentUserEmail(stringValue);
                    }
                    break;
                case "pref_country":
                    if (!stringValue.equals(((ListPreference) preference).getValue())) {
                        userService.updateCurrentUserCountry(stringValue);
                    }
                    break;
                case "pref_default_language":
                    langPref = (ListPreference) preference;
                    if (!stringValue.equals(langPref.getValue())) {
                        userService.updateCurrentUserDefaultLanguage(stringValue);
                    }
                    break;
                case "pref_languages":
                    Set<String> values = (Set<String>) value;
                    MultiSelectListPreference msPref = (MultiSelectListPreference) preference;

                    // there needs to be at least one language
                    if (values.isEmpty()) {
                        values.add("ll");
                    }

                    if (!values.equals(msPref.getValues())) {
                        userService.updateCurrentUserLanguages(values);

                        // update pref_default_language
                        if (langPref != null) {
                            List<String> entries = new ArrayList<>();
                            CharSequence[] entryValues = values.toArray(new CharSequence[entries.size()]);

                            CharSequence[] allEntries = msPref.getEntries();

                            for (String v : values) {
                                entries.add((String) allEntries[msPref.findIndexOfValue(v)]);
                            }

                            langPref.setEntries(entries.toArray(new CharSequence[entries.size()]));
                            langPref.setEntryValues(entryValues);
                            langPref.setDefaultValue(entryValues[0]);

                            // update default language if it's not in available languages any more
                            if (!values.contains(langPref.getValue())) {
                                langPref.setValue(values.iterator().next());
                            }

                            bindPreferenceSummaryToValue(langPref);
                        }
                    }
                    break;
                case "notifications_new_message":
                    Boolean boolValue = (Boolean) value;
                    if (boolValue == !((SwitchPreference) preference).isChecked()) {
                        userService.updateCurrentUserNotifications(boolValue);
                    }
                    break;
            }

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
            } else if (preference instanceof SwitchPreference) {
                // nothing to do
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

            IUserService userService = new UserService();
            Promise pu = userService.getUserInfo(userService.getCurrentUserId());
            ILanguageService langService = new LanguageService();
            final Promise pl = langService.getLanguages();
            ICountryService countryService = new CountryService();
            final Promise pc = countryService.getCountries();

            final EditTextPreference namePref = (EditTextPreference) findPreference("pref_display_name");
            final EditTextPreference usernamePref = (EditTextPreference) findPreference("pref_user_name");
            final EditTextPreference moodPref = (EditTextPreference) findPreference("pref_mood");
            final EditTextPreference emailPref = (EditTextPreference) findPreference("pref_email");
            final ListPreference countryPref = (ListPreference) findPreference("pref_country");
            final MultiSelectListPreference langsPref = (MultiSelectListPreference) findPreference("pref_languages");
            final ListPreference langPref = (ListPreference) findPreference("pref_default_language");

            pu.done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    final User user = (User) result;

                    namePref.setText(user.getName());
                    namePref.setSummary(user.getName());
                    bindPreferenceSummaryToValue(namePref);

                    usernamePref.setText(user.getUsername());
                    usernamePref.setSummary(user.getUsername());
                    bindPreferenceSummaryToValue(usernamePref);

                    moodPref.setText(user.getMood());
                    moodPref.setSummary(user.getMood());
                    bindPreferenceSummaryToValue(moodPref);

                    emailPref.setText(user.getEmail());
                    emailPref.setSummary(user.getEmail());
                    bindPreferenceSummaryToValue(emailPref);

                    countryPref.setValue(user.getCountry());
                    setListPreferenceData(countryPref, pc);
                    countryPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            setListPreferenceData(countryPref, pc);
                            return false;
                        }
                    });

                    langsPref.setValues(user.getLanguages().keySet());
                    setListPreferenceData(langPref, langsPref, pl);
                    langsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            setListPreferenceData(langPref, langsPref, pl);
                            return false;
                        }
                    });

                    // TODO: update entries/values for pref_default_language when pref_languages is changed
                    langPref.setValue(user.getDefaultLanguage());
                    langPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            setListPreferenceData(langPref, langsPref, PreferenceManager
                                    .getDefaultSharedPreferences(langsPref.getContext())
                                    .getStringSet(langsPref.getKey(), null));
                            return false;
                        }
                    });
                }
            });
        }

        protected static void setListPreferenceData(final ListPreference lp, Promise p) {
            p.done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    Map<String, Country> countries = (Map<String, Country>) result;

                    List<String> entries = new ArrayList<>();
                    for (Country c : countries.values()) {
                        if (c.getNativeName().equals(c.getName())) {
                            entries.add(c.getNativeName());
                        } else {
                            entries.add(c.getNativeName() + " (" + c.getName() + ")");
                        }
                    }

                    lp.setEntries(entries.toArray(new CharSequence[entries.size()]));
                    lp.setEntryValues(countries.keySet().toArray(new CharSequence[entries.size()]));
                    lp.setDefaultValue("AT");
                    bindPreferenceSummaryToValue(lp);
                }
            });
        }

        public static void setListPreferenceData(ListPreference lp, MultiSelectListPreference langsp, Set<String> values) {
            List<String> entries = new ArrayList<>();
            CharSequence[] entryValues = values.toArray(new CharSequence[entries.size()]);

            CharSequence[] allEntries = langsp.getEntries();

            for (String v : values) {
                entries.add((String) allEntries[langsp.findIndexOfValue(v)]);
            }

            lp.setEntries(entries.toArray(new CharSequence[entries.size()]));
            lp.setEntryValues(entryValues);
            lp.setDefaultValue(entryValues[0]);
            bindPreferenceSummaryToValue(lp);
        }

        protected static void setListPreferenceData(final ListPreference lp, final MultiSelectListPreference langsp, Promise p) {
            p.done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    Map<String, Language> langs = (Map<String, Language>) result;

                    List<String> entries = new ArrayList<>();
                    for (Language l : langs.values()) {
                        if (l.getNativeName().equals(l.getName())) {
                            entries.add(l.getNativeName());
                        } else {
                            entries.add(l.getNativeName() + " (" + l.getName() + ")");
                        }
                    }

                    langsp.setEntries(entries.toArray(new CharSequence[entries.size()]));
                    langsp.setEntryValues(langs.keySet().toArray(new CharSequence[entries.size()]));
                    langsp.setDefaultValue("en");
                    bindPreferenceSummaryToValue(langsp);

                    setListPreferenceData(lp, langsp, langsp.getValues());
                }
            });
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

            IUserService userService = new UserService();
            Promise pu = userService.getUserInfo(userService.getCurrentUserId());

            final SwitchPreference notificationsPref = (SwitchPreference) findPreference("notifications_new_message");

            pu.done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    final User user = (User) result;

                    notificationsPref.setChecked(user.getNotifications());
                    notificationsPref.setDefaultValue(user.getNotifications());
                    bindPreferenceSummaryToValue(notificationsPref);
                }
            });

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));

            PreferenceScreen screen = getPreferenceScreen();
            screen.removePreference(findPreference("notifications_new_message_ringtone"));
            screen.removePreference(findPreference("notifications_new_message_vibrate"));
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
