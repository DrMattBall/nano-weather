package com.nanoweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.nanoweather.ui.MainViewModel
import com.nanoweather.ui.components.WeatherScreen
import com.nanoweather.ui.theme.NanoWeatherTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as NanoWeatherApp
        val viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(
                app.geocodingRepository,
                app.weatherRepository,
                app.cityRepository
            )
        )[MainViewModel::class.java]

        setContent {
            NanoWeatherTheme {
                WeatherScreen(viewModel)
            }
        }
    }
}
