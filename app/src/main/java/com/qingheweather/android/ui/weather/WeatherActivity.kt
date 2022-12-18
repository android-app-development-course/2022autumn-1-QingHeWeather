package com.qingheweather.android.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.matteobattilana.weather.PrecipType
import com.qingheweather.android.R
import com.qingheweather.android.logic.model.Sky
import com.qingheweather.android.logic.model.Weather
import com.qingheweather.android.logic.model.getsky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.miuiweatherview.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*


class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProviders.of(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.activity_weather)

        weatherLayout.setEnableTopRebound(false)
        weatherLayout.viewTreeObserver.addOnScrollChangedListener {
            swipeRefresh.isEnabled = weatherLayout.scrollY == 0
        }

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            weatherLayout.scrollTo(0, -10)
            refreshWeather()

        }
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        weatherView.setWeatherData(PrecipType.CLEAR)
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        val hourly = weather.hourly


        // 填充miuiweatherview.xml布局中数据
//        val simplehourFormat = SimpleDateFormat("hh-mm", Locale.getDefault())
        //        Toast.makeText(this, hourly.temperature[0].value.toString(), Toast.LENGTH_SHORT).show() //正常
//        Toast.makeText(this, hourly.skycon[0].value, Toast.LENGTH_SHORT).show() // 正常
//        Toast.makeText(this, simplehourFormat.format(hourly.skycon[0].datetime), Toast.LENGTH_SHORT).show()

        val hours = hourly.skycon.size
        val Weatherdata = ArrayList<WeatherBean>()
        for (i in 0 until hours) {
            val hourlyskycon = hourly.skycon[i]
            val temperature = hourly.temperature[i].value.toInt()
//            val simplehourFormat = SimpleDateFormat("hh-mm", Locale.getDefault())
//            val hour = simplehourFormat.format(hourlyskycon.datetime)
            val hour = hourly.skycon[i].datetime.substring(11, 16)
            Weatherdata.add(WeatherBean(skykonConvert(hourlyskycon.value), temperature, hour))
        }
        weatherChart.setData(Weatherdata)

        // 填充now.xml布局中数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getsky(realtime.skycon).info
        if (realtime.skycon == "LIGHT_RAIN" || realtime.skycon == "MODERATE_RAIN" || realtime.skycon == "HEAVY_RAIN" || realtime.skycon == "STORM_RAIN"){
            weatherView.setWeatherData(PrecipType.RAIN)
        }else if (realtime.skycon == "LIGHT_SNOW" || realtime.skycon == "MODERATE_SNOW" || realtime.skycon == "HEAVY_SNOW"){
            weatherView.setWeatherData(PrecipType.SNOW)
        }
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getsky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getsky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }

    private fun skykonConvert(value: String): String =
        when (value){
            "CLEAR_DAY" -> WeatherBean.SUN
            "CLEAR_NIGHT" -> WeatherBean.SUN_NIGHT
            "PARTLY_CLOUDY_DAY"  -> WeatherBean.SUN_CLOUD
            "PARTLY_CLOUDY_NIGHT"  -> WeatherBean.SUN_NIGHT
            "CLOUDY"  -> WeatherBean.CLOUDY
            "WIND" -> WeatherBean.CLOUDY
            "LIGHT_RAIN"  -> WeatherBean.LIGHT_SNOW
            "MODERATE_RAIN" -> WeatherBean.MODERATE_SNOW
            "HEAVY_RAIN" -> WeatherBean.HEAVY_SNOW
            "STORM_RAIN" -> WeatherBean.STORM_RAIN
            "THUNDER_SHOWER" -> WeatherBean.THUNDER_RAIN
            "SLEET" -> WeatherBean.SLEET
            "LIGHT_SNOW" -> WeatherBean.LIGHT_SNOW
            "MODERATE_SNOW" -> WeatherBean.MODERATE_SNOW
            "HEAVY_SNOW" -> WeatherBean.HEAVY_SNOW
            "STORM_SNOW" -> WeatherBean.STORM_SNOW
            "HAIL" -> WeatherBean.HAIL
            "LIGHT_HAZE" -> WeatherBean.LIGHT_HAZE
            "MODERATE_HAZE" -> WeatherBean.MODERATE_HAZE
            "HEAVY_HAZE" -> WeatherBean.HEAVY_HAZE
            "FOG" -> WeatherBean.FOG
            "DUST" -> WeatherBean.FOG
            else -> WeatherBean.SUN
        }
}
