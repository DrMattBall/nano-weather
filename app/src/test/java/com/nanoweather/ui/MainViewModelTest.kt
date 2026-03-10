package com.nanoweather.ui

import com.nanoweather.data.repository.CityRepository
import com.nanoweather.data.repository.GeocodingRepository
import com.nanoweather.data.repository.WeatherRepository
import com.nanoweather.domain.model.City
import com.nanoweather.domain.model.CityWeather
import com.nanoweather.domain.model.CurrentWeather
import com.nanoweather.domain.model.DailyForecast
import com.nanoweather.domain.model.HourlyForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val london = City(1, "London", 51.5, -0.1, "UK", "England")
    private val sampleWeather = CityWeather(
        current = CurrentWeather(temperature = 20.0, weatherCode = 0),
        daily = DailyForecast(highTemp = 25.0, lowTemp = 15.0),
        hourly = listOf(
            HourlyForecast("2024-01-15T12:00", 20.0, 0.0)
        )
    )

    private val fakeGeocodingRepo = object : GeocodingRepository {
        var result: Result<List<City>> = Result.success(listOf(london))
        override suspend fun searchCities(query: String) = result
    }

    private val fakeWeatherRepo = object : WeatherRepository {
        var result: Result<CityWeather> = Result.success(sampleWeather)
        override suspend fun getWeather(latitude: Double, longitude: Double) = result
    }

    private val fakeCityRepo = object : CityRepository {
        val cities = mutableListOf<City>()
        override suspend fun getSavedCities() = cities.toList()
        override suspend fun addCity(city: City) { cities.add(city) }
        override suspend fun removeCity(cityId: Int) { cities.removeAll { it.id == cityId } }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = MainViewModel(fakeGeocodingRepo, fakeWeatherRepo, fakeCityRepo)

    @Test
    fun `init loads saved cities and fetches weather`() = runTest {
        fakeCityRepo.cities.add(london)
        val vm = createViewModel()

        val state = vm.uiState.value
        assertEquals(1, state.cities.size)
        assertEquals("London", state.cities[0].city.name)
        assertEquals(20.0, state.cities[0].currentTemp!!, 0.001)
    }

    @Test
    fun `onCitySelected adds city and fetches weather`() = runTest {
        val vm = createViewModel()

        vm.onCitySelected(london)

        val state = vm.uiState.value
        assertEquals(1, state.cities.size)
        assertEquals(20.0, state.cities[0].currentTemp!!, 0.001)
        assertTrue(state.searchQuery.isEmpty())
        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun `onCityToggled toggles expanded state`() = runTest {
        fakeCityRepo.cities.add(london)
        val vm = createViewModel()

        vm.onCityToggled(london.id)
        assertTrue(vm.uiState.value.cities[0].isExpanded)

        vm.onCityToggled(london.id)
        assertTrue(!vm.uiState.value.cities[0].isExpanded)
    }

    @Test
    fun `long press enters selection mode and selects city`() = runTest {
        fakeCityRepo.cities.add(london)
        val vm = createViewModel()

        vm.onEnterSelectionMode()
        vm.onToggleSelection(london.id)

        assertTrue(vm.uiState.value.isSelectionMode)
        assertTrue(vm.uiState.value.cities[0].isSelected)
    }

    @Test
    fun `onRemoveSelected removes selected cities and exits selection mode`() = runTest {
        fakeCityRepo.cities.add(london)
        val vm = createViewModel()

        vm.onEnterSelectionMode()
        vm.onToggleSelection(london.id)
        vm.onRemoveSelected()

        assertTrue(vm.uiState.value.cities.isEmpty())
        assertTrue(!vm.uiState.value.isSelectionMode)
    }

    @Test
    fun `onExitSelectionMode clears selections`() = runTest {
        fakeCityRepo.cities.add(london)
        val vm = createViewModel()

        vm.onEnterSelectionMode()
        vm.onToggleSelection(london.id)
        vm.onExitSelectionMode()

        assertTrue(!vm.uiState.value.isSelectionMode)
        assertTrue(!vm.uiState.value.cities[0].isSelected)
    }
}
