package com.nanoweather.data.repository

import android.content.SharedPreferences

class SettingsRepositoryImpl(
    private val prefs: SharedPreferences
) : SettingsRepository {

    override fun getContrastBubbles(): Boolean =
        prefs.getBoolean(KEY_CONTRAST_BUBBLES, false)

    override fun setContrastBubbles(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_CONTRAST_BUBBLES, enabled).apply()
    }

    override fun getUseFahrenheit(): Boolean =
        prefs.getBoolean(KEY_USE_FAHRENHEIT, false)

    override fun setUseFahrenheit(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_USE_FAHRENHEIT, enabled).apply()
    }

    companion object {
        private const val KEY_CONTRAST_BUBBLES = "contrast_bubbles"
        private const val KEY_USE_FAHRENHEIT = "use_fahrenheit"
    }
}
