package com.nanoweather.data.repository

interface SettingsRepository {
    fun getContrastBubbles(): Boolean
    fun setContrastBubbles(enabled: Boolean)
    fun getUseFahrenheit(): Boolean
    fun setUseFahrenheit(enabled: Boolean)
}
