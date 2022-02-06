package com.emrecan.havadurumu

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emrecan.havadurumu.service.WeatherRepository
import com.emrecan.havadurumu.service.dto.CurrentWeather
import com.emrecan.havadurumu.service.dto.FullWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class MainViewModel @Inject constructor(
    repository: WeatherRepository
) : ViewModel() {

    val current: Flow<CurrentWeather> = repository.getCurrentWeather()
    val daily: Flow<List<FullWeather.Daily>> = repository.getFiveDayForecast()
    val hourly: Flow<List<FullWeather.Hourly>> = repository.getHourlyForecast()




}