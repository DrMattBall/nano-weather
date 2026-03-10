package com.nanoweather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.nanoweather.ui.MainViewModel
import com.nanoweather.ui.components.WeatherScreen
import com.nanoweather.ui.theme.NanoWeatherTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as NanoWeatherApp
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(
                app.geocodingRepository,
                app.weatherRepository,
                app.cityRepository,
                app.settingsRepository,
                app.radarRepository,
                if (hasLocationPermission) app.locationProvider else null
            )
        )[MainViewModel::class.java]

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                viewModel.onLocationPermissionGranted(app.locationProvider)
            }
        }

        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        setContent {
            NanoWeatherTheme {
                WeatherScreen(viewModel)
            }
        }
    }
}
