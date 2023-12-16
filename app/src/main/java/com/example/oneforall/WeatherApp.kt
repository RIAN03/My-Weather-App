package com.example.oneforall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.oneforall.databinding.ActivityMainBinding
import com.example.oneforall.databinding.ActivityWeatherAppBinding
import org.json.JSONObject
import java.time.Instant

class WeatherApp : AppCompatActivity() {
    lateinit var binding: ActivityWeatherAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherAppBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val lat = intent.getStringExtra("lat")
        val long = intent.getStringExtra("long")

        getJsonData(lat,long)
    }

    private fun getJsonData(lat: String?, long: String?) {

        val queue = Volley.newRequestQueue(this)
        val API_KEY = "767616bee1b0927999b1100098cf5b15"
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${long}&appid=${API_KEY}"


        // Request a string response from the provided URL.
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { response ->
                setValues(response)
            },
            Response.ErrorListener { Toast.makeText(this,"ERROR",Toast.LENGTH_LONG).show() })

        // Add the request to the RequestQueue.
        queue.add(jsonRequest)

    }


    private fun setValues(response: JSONObject) {
        binding.city.text = response.getString("name")
        val condition = response.getJSONArray("weather").getJSONObject(0).getString("main")
        when(condition){
            "Haze" -> {
                val haze = R.drawable.nightsnow2
                binding.relative.setBackgroundResource(haze)
                binding.weatherResource.setImageResource(R.drawable.snowfall)
            }
            "Rain" -> {
                val rain = R.drawable.evesunset
            binding.relative.setBackgroundResource(rain)
            }
            else -> {
                binding.relative.setBackgroundResource(R.drawable.weather_forecast)
            }
        }
        binding.condition.text = condition
        val temp = response.getJSONObject("main").getString("temp")
        binding.tempCondition.text = (((temp).toFloat() - 273.15).toInt()).toString()+"C"
        val feels = response.getJSONObject("main").getString("feels_like")
        binding.temperature.text = (((feels).toFloat() - 273.15).toInt()).toString()+"C"
        binding.humidityValue.text = response.getJSONObject("main").getString("humidity")+"%"
        binding.windValue.text = response.getJSONObject("wind").getString("speed")+"m/s"
        binding.uvValue.text = response.getJSONObject("wind").getString("speed")



    }
}