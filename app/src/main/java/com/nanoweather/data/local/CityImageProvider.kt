package com.nanoweather.data.local

import android.content.res.Resources

interface CityImageProvider {
    fun getImageResId(cityId: Int): Int?
}

class ResourceCityImageProvider(
    private val resources: Resources,
    private val packageName: String
) : CityImageProvider {

    override fun getImageResId(cityId: Int): Int? {
        val resId = resources.getIdentifier("city_bg_$cityId", "drawable", packageName)
        return if (resId != 0) resId else null
    }
}
