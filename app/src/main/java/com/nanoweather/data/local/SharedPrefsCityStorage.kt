package com.nanoweather.data.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nanoweather.domain.model.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPrefsCityStorage(
    private val prefs: SharedPreferences,
    private val gson: Gson = Gson()
) : CityStorage {

    override suspend fun loadCities(): List<City> = withContext(Dispatchers.IO) {
        val json = prefs.getString(KEY_CITIES, null) ?: return@withContext emptyList()
        val type = object : TypeToken<List<City>>() {}.type
        gson.fromJson(json, type)
    }

    override suspend fun saveCities(cities: List<City>) = withContext(Dispatchers.IO) {
        prefs.edit()
            .putString(KEY_CITIES, gson.toJson(cities))
            .apply()
    }

    companion object {
        private const val KEY_CITIES = "saved_cities"
    }
}
