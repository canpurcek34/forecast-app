package com.emrecan.havadurumu

import android.Manifest
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emrecan.havadurumu.ui.theme.DarkBlue
import com.emrecan.havadurumu.ui.theme.HavaDurumuTheme
import com.emrecan.havadurumu.ui.theme.LightGray
import com.emrecan.havadurumu.service.dto.CurrentWeather
import com.emrecan.havadurumu.service.dto.FullWeather
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.ExperimentalTime


@AndroidEntryPoint
@ExperimentalPermissionsApi
class MainActivity : ComponentActivity() {


    val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HavaDurumuTheme {

                val permission =
                    rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

                PermissionRequired(
                    permissionState = permission,
                    permissionNotGrantedContent = { LocationPermissionDetails(onContinueClick = permission::launchPermissionRequest) },
                    permissionNotAvailableContent = { LocationPermissionNotAvailable() }

                ) {

                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WeatherApp(viewModel)
                    Navigation()

                }


            }
        }

    }
}

    @Composable
    fun WeatherApp(viewModel: MainViewModel) {


        val scrollState = rememberScrollState()

            val current by viewModel.current.collectAsState(null)
            val daily by viewModel.daily.collectAsState(emptyList())
            val hourly by viewModel.hourly.collectAsState(emptyList())
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 48.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                    .verticalScroll(state = scrollState),
                horizontalAlignment = Alignment.CenterHorizontally) {
                current?.let {
                    HeaderImage()
                    MainInfo(weather = it)
                    InfoTable(it)
                    Divider(color = Color.White)
                }

                Text(text = "Saatlik hava durumu tahmini",
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                fontSize = 16.sp
                )

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {

                    hourly?.let {
                        Hourly(hourly = it)
                    }

                }

                Text(text = "5 günlük hava durumu tahmini",
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp)

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    daily.let {
                        Daily(weather = it)
                    }

                }
            }
        }


    @Composable
    fun HeaderImage() {

        Image(
            painter = painterResource(id = R.drawable.ic_couple),
            contentDescription = null,
            modifier = Modifier
                .width(100.dp)
                .height(150.dp)
                .padding(top = 10.dp)
        )
    }


    @Composable
    fun MainInfo(weather: CurrentWeather) {

            Column(
                modifier = Modifier.padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = weather.main.temp.toString()+"°C",
                    color = DarkBlue,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = weather.name,
                    color = DarkBlue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = weather.weather.first().description.toUpperCase(),
                    color = Color.Gray,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontWeight = FontWeight.Bold
                )
        }
        }
    @Composable
    fun InfoTable(weather: CurrentWeather) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    LightGray
                )
        ) {
            Row(Modifier.padding(16.dp)) {
                InfoItem(
                    iconRes = R.drawable.ic_humidity,
                    title = "Nem",
                    subtitle = "%"+weather.main.humidity.toString(),
                    modifier = Modifier.weight(
                        1f
                    )
                )
                InfoItem(
                    iconRes = R.drawable.wind,
                    title = "Rüzgar",
                    subtitle = weather.wind.speed.toString()+"km/h",
                    modifier = Modifier.weight(
                        1f
                    )
                )
            }
            Divider(color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))

            val sunrise = weather.sys.sunrise.toLong()
            val sunset = weather.sys.sunset.toLong()
            Row(Modifier.padding(16.dp)) {
                InfoItem(
                    iconRes = R.drawable.ic_sun_half,
                    title = "Gün Doğumu",
                    subtitle = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000)),
                    modifier = Modifier.weight(
                        1f
                    )
                )
                InfoItem(
                    iconRes = R.drawable.ic_sun_half,
                    title = "Gün Batımı",
                    subtitle = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000)),
                    modifier = Modifier.weight(
                        1f
                    )
                )
            }
            Divider(color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
            Row(Modifier.padding(16.dp)) {
                InfoItem(
                    iconRes = R.drawable.termocold,
                    title = "Min. Sıcaklık",
                    subtitle = weather.main.tempMin.toString()+"°C",
                    modifier = Modifier.weight(
                        1f
                    )
                )
                InfoItem(
                    iconRes = R.drawable.termohot,
                    title = "Maks. Sıcaklık",
                    subtitle = weather.main.tempMax.toString()+"°C",
                    modifier = Modifier.weight(
                        1f
                    )
                )
            }
        }
    }

    @Composable
    fun InfoItem(@DrawableRes iconRes: Int, title: String, subtitle: String, modifier: Modifier) {
        Row(modifier = modifier) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(40.dp)
            )
            Column {
                Text(title, color = Color.Gray)
                Text(subtitle, color = DarkBlue, fontWeight = FontWeight.Bold)
            }
        }
    }

@OptIn(ExperimentalTime::class)
@Composable
fun Daily(weather: (List<FullWeather.Daily>)) {

    val day0 = weather.getOrNull(0)?.dt?.toLong()
    val temp0 = weather.getOrNull(0)?.temp?.day.toString()
    val desc0 = weather.getOrNull(0)?.weather?.firstOrNull()?.description
    val day1 = weather.getOrNull(1)?.dt?.toLong()
    val temp1 = weather.getOrNull(1)?.temp?.day.toString()
    val desc1 = weather.getOrNull(1)?.weather?.firstOrNull()?.description
    val day2 = weather.getOrNull(2)?.dt?.toLong()
    val temp2 = weather.getOrNull(2)?.temp?.day.toString()
    val desc2 = weather.getOrNull(2)?.weather?.firstOrNull()?.description
    val day3 = weather.getOrNull(3)?.dt?.toLong()
    val temp3 = weather.getOrNull(3)?.temp?.day.toString()
    val desc3 = weather.getOrNull(3)?.weather?.firstOrNull()?.description
    val day4 = weather.getOrNull(4)?.dt?.toLong()
    val temp4 = weather.getOrNull(4)?.temp?.day.toString()
    val desc4 = weather.getOrNull(4)?.weather?.firstOrNull()?.description

    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(LightGray)
            .horizontalScroll(state = scrollState)
    ) {
        Column(Modifier.padding(7.dp)) {
            if (day0 != null) {
                DailyInfoItem(
                    iconRes = R.drawable.thermometer,
                    title =SimpleDateFormat("EEEE").format(Date(day0*1000)) ,
                    subtitle = temp0+"°C",
                    modifier = Modifier
                        .weight(1f, false)

                )
            }
        }

        Column(Modifier.padding(7.dp)) {
            if (day1 != null) {
                DailyInfoItem(
                    iconRes = R.drawable.thermometer,
                    title = SimpleDateFormat("EEEE").format(Date(day1*1000)),
                    subtitle = temp1+"°C",
                    modifier = Modifier.weight(1f,false)
                )

            }

        }



        Column(Modifier.padding(7.dp)) {
            if (day2 != null) {
                DailyInfoItem(
                    iconRes = R.drawable.thermometer,
                    title = SimpleDateFormat("EEEE").format(Date(day2*1000)),
                    subtitle = temp2+"°C",
                    modifier = Modifier.weight(1f,false))

            }

        }


        Column(Modifier.padding(7.dp)) {
            if (day3 != null) {
                DailyInfoItem(
                    iconRes = R.drawable.thermometer,
                    title = SimpleDateFormat("EEEE").format(Date(day3*1000)),
                    subtitle = temp3+"°C",
                    modifier = Modifier.weight(1f,false)
                )

            }

        }


        Column(Modifier.padding(7.dp)) {
            if (day4 != null) {
                DailyInfoItem(
                    iconRes = R.drawable.thermometer,
                    title = SimpleDateFormat("EEEE").format(Date(day4*1000)),
                    subtitle = temp4+"°C",
                    modifier = Modifier.weight(1f,false)
                )

            }

        }

    }
}

@Composable
fun DailyInfoItem(@DrawableRes iconRes: Int,title: String,subtitle: String, modifier: Modifier){
    Column(modifier = modifier) {

        Text(text = title,
            color = DarkBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally))
        Image(painter = painterResource(id = iconRes),
            contentDescription = null,
        modifier = Modifier
            .width(30.dp)
            .height(40.dp)
            .align(Alignment.CenterHorizontally))
        Text(text = subtitle,
            color = DarkBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        modifier = Modifier.align(Alignment.CenterHorizontally))

    }

}

@OptIn(ExperimentalTime::class)
@Composable
fun Hourly(hourly: (List<FullWeather.Hourly>)) {

    val time0 = hourly.getOrNull(0)?.dt?.toLong()
    val time1 = hourly.getOrNull(1)?.dt?.toLong()
    val time2 = hourly.getOrNull(2)?.dt?.toLong()
    val time3 = hourly.getOrNull(3)?.dt?.toLong()
    val time4 = hourly.getOrNull(4)?.dt?.toLong()
    val temp0 = hourly.getOrNull(0)?.temp.toString()
    val temp1 = hourly.getOrNull(1)?.temp.toString()
    val temp2 = hourly.getOrNull(2)?.temp.toString()
    val temp3 = hourly.getOrNull(3)?.temp.toString()
    val temp4 = hourly.getOrNull(4)?.temp.toString()


    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(LightGray)
            .horizontalScroll(state = scrollState)
    ) {
        Column(Modifier.padding(7.dp)) {
            if (time0 != null) {
                HourlyInfoItem(
                    iconRes = R.drawable.thermometer,
                    title = SimpleDateFormat("hh:mm a").format(Date(time0 * 1000)),
                    subtitle = temp0 + "°C",
                    modifier = Modifier
                        .weight(1f, false)

                )
            }
        }


        Column(Modifier.padding(7.dp)) {

            if (time1 != null) {
                HourlyInfoItem(
                    iconRes = R.drawable.thermometer,
                    title = SimpleDateFormat("hh:mm a").format(Date(time1*1000)),
                    subtitle = temp1+"°C",
                    modifier = Modifier.weight(1f,false)
                )
            }

        }



        Column(Modifier.padding(7.dp)) {
            if (time2 != null) {
                HourlyInfoItem(
                    iconRes = R.drawable.thermometer,
                    title = SimpleDateFormat("hh:mm a").format(Date(time2*1000)),
                    subtitle = temp2+"°C",
                    modifier = Modifier.weight(1f,false))
            }



        }


        Column(Modifier.padding(7.dp)) {
            if (time3 != null) {
                HourlyInfoItem(
                    iconRes = R.drawable.thermometer,
                    title = SimpleDateFormat("hh:mm a").format(Date(time3*1000)),
                    subtitle = temp3+"°C",
                    modifier = Modifier.weight(1f,false)
                )
            }

            }




        Column(Modifier.padding(7.dp)) {
            if (time4 != null) {
                HourlyInfoItem(
                    iconRes = R.drawable.thermometer,
                    title = SimpleDateFormat("hh:mm a").format(Date(time4*1000)),
                    subtitle = temp4+"°C",
                    modifier = Modifier.weight(1f,false)
                )
            }

        }

    }
}

@Composable
fun HourlyInfoItem(@DrawableRes iconRes: Int,title: String,subtitle: String, modifier: Modifier){
    Column(modifier = modifier) {

        Text(text = title,
            color = DarkBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally))
        Image(painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .width(30.dp)
                .height(40.dp)
                .align(Alignment.CenterHorizontally))
        Text(text = subtitle,
            color = DarkBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally))

    }

}


@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController=navController,
    startDestination = "splash_screen"){
        composable("splash_screen"){
            SplashScreen(navController=navController)
        }

        composable("WeatherApp"){
            }
        }
    }

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 300,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(100L)
        navController.navigate("WeatherApp")
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.forecast),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}



