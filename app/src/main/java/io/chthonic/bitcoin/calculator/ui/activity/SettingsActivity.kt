package io.chthonic.bitcoin.calculator.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.chthonic.bitcoin.calculator.BuildConfig
import io.chthonic.bitcoin.calculator.R
import io.chthonic.bitcoin.calculator.utils.UiUtils
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Created by jhavatar on 9/29/2018.
 */
class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction()
                .replace(R.id.content, SettingsFragment())
                .commit()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    class SettingsFragment: PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings)
            val versionPref = findPreference(getString(R.string.pref_app_version)) as Preference
            versionPref.setSummary("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")

            val privacyPref = findPreference(getString(R.string.pref_privacy_policy)) as Preference
            privacyPref.setOnPreferenceClickListener {
                this@SettingsFragment.context?.let {
                    UiUtils.showUrl(it, getString(R.string.privacy_policy_url))
                }
                true
            }

            val githubPref = findPreference(getString(R.string.pref_github)) as Preference
            githubPref.setOnPreferenceClickListener {
                this@SettingsFragment.context?.let {
                    UiUtils.showUrl(it, getString(R.string.github_url))
                }
                true
            }
        }
    }


}