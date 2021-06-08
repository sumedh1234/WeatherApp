package com.example.weather

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_view.view.*
import org.json.JSONObject

@Suppress("DEPRECATION")
open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lat = intent.getStringExtra("lat")
        val long = intent.getStringExtra("long")

        getJsonData(lat, long)

        @SuppressLint("WrongViewCast")
        val city = findViewById<TextView>(R.id.city)
        city.setOnClickListener{

            Toast.makeText(this,"Refreshing...",Toast.LENGTH_SHORT).show()
            getJsonData(lat, long)

        }

    }


    private fun getJsonData(lat:String?,long:String?) {

        val api ="79417d6102a0ae012c0f277592c26572"

//      Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${long}&appid=${api}"

//      Request a string response from the provided URL.
        val stringRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
//                Toast.makeText(this,response.toString(),Toast.LENGTH_LONG).show()
                setValues(response)
            },
            {

                Toast.makeText(this,"Check you internet connection!",Toast.LENGTH_SHORT).show()
            })

 //     Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    @SuppressLint("SetTextI18n")
    protected fun setValues(response: JSONObject) {

        val imageView = findViewById<ImageView?>(R.id.image)

        city.text = response.getString("name")

        //weather description
//        val weather = "Clear"
//        val description = "few clouds"
        val weather = response.getJSONArray("weather").getJSONObject(0).getString("main")
        val description = response.getJSONArray("weather").getJSONObject(0).getString("description")

        //dayTime
        val dayTime = response.getString("dt")

        //sunRise
        val sunRise = response.getJSONObject("sys").getString("sunrise")

        //sunSet
        val sunSet = response.getJSONObject("sys").getString("sunset")

        when(weather){

            "Clear" -> if (dayTime >= sunRise && dayTime < sunSet){
                imageView.setImageResource(R.drawable.d_clearsky)
            }else imageView.setImageResource(R.drawable.n_clearsky)

            "Clouds" -> when (description){
                "broken clouds" -> imageView.setImageResource(R.drawable.d_n_brokenclouds)

                "few clouds" -> if (dayTime >= sunRise && dayTime < sunSet){
                    imageView.setImageResource(R.drawable.dfewclouds)
                }else imageView.setImageResource(R.drawable.nfewclouds)

                "scattered clouds" -> imageView.setImageResource(R.drawable.d_n_scatteredclouds)

                "overcast clouds" -> imageView.setImageResource(R.drawable.d_n_brokenclouds)
            }

            "Rain" -> when (description){

                "shower rain" -> imageView.setImageResource(R.drawable.shower_rain)

                "light intensity rain" -> imageView.setImageResource(R.drawable.shower_rain)

                "heavy intensity shower rain" -> imageView.setImageResource(R.drawable.shower_rain)

                "ragged rain" -> imageView.setImageResource(R.drawable.shower_rain)

                "light rain" -> if (dayTime >= sunRise && dayTime < sunSet){
                    imageView.setImageResource(R.drawable.drainy)
                }else imageView.setImageResource(R.drawable.nrainy)

                "freezing rain" -> imageView.setImageResource(R.drawable.snow)

                "extreme rain" -> if (dayTime >= sunRise && dayTime < sunSet){
                    imageView.setImageResource(R.drawable.drainy)
                }else imageView.setImageResource(R.drawable.nrainy)

                "moderate rain" -> if (dayTime >= sunRise && dayTime < sunSet){
                    imageView.setImageResource(R.drawable.drainy)
                }else imageView.setImageResource(R.drawable.nrainy)

                "heavy intensity rain" -> if (dayTime >= sunRise && dayTime < sunSet){
                    imageView.setImageResource(R.drawable.drainy)
                }else imageView.setImageResource(R.drawable.nrainy)

                "very heavy rain" -> if (dayTime >= sunRise && dayTime < sunSet){
                    imageView.setImageResource(R.drawable.drainy)
                }else imageView.setImageResource(R.drawable.nrainy)
            }

            "Drizzle" -> imageView.setImageResource(R.drawable.shower_rain)

            "Thunderstorm" -> imageView.setImageResource(R.drawable.thunderstrome)

            "Snow" -> imageView.setImageResource(R.drawable.snow)

            "Mist" -> imageView.setImageResource(R.drawable.mist)

        }

        if (dayTime >= sunRise && dayTime < sunSet){
            imageView.setBackgroundResource(R.drawable.day_background)
        }else imageView.setBackgroundResource(R.drawable.night_background)

        
        //Temperature
        var temperature = response.getJSONObject("main").getString("temp")
        temperature = (((temperature).toFloat()  - 273.15).toInt()).toString()
        temp.text = "$temperature°C"

        //wind speed
        val wind = response.getJSONObject("wind").getString("speed").toString()
        speed.text = "$wind mps"

        //degrees
        val degree = (response.getJSONObject("wind").getString("deg")).toString()
        deg.text =  "$degree°"

        //gust
        val gust1 = response.getJSONObject("wind").getString("gust").toString()
        gust.text = "$gust1 mps"

        //humidity
        val humidity = (response.getJSONObject("main").getString("humidity")).toString()
        humid.text = "$humidity%"

        //pressure
        val pressureVal = response.getJSONObject("main").getString("pressure").toString()
        pressure.text = "$pressureVal hPa"

        //visibility
        var visible = response.getString("visibility")
        visible = ((visible).toInt() / 1000).toString()
        visibility.text = "$visible km"

        //This is for showing images according to weather conditions
        imageView.setOnClickListener {

            val view = View.inflate(this, R.layout.dialog_view, null)

            val builder = AlertDialog.Builder(this)
            builder.setView(view)

            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            when(weather){

                "Clear" -> {
                    if (dayTime >= sunRise && dayTime < sunSet){
                        view.info_image.setImageResource(R.drawable.d_clearsky)
                    }else view.info_image.setImageResource(R.drawable.n_clearsky)

                    view.infoText.text = description
                }

                "Clouds" -> when (description){
                    "broken clouds" -> {
                        view.info_image.setImageResource(R.drawable.d_n_brokenclouds)
                        view.infoText.text = description
                    }

                    "few clouds" -> {
                        if (dayTime >= sunRise && dayTime < sunSet){
                            view.info_image.setImageResource(R.drawable.dfewclouds)
                        }else view.info_image.setImageResource(R.drawable.nfewclouds)

                        view.infoText.text = description
                    }

                    "scattered clouds" -> {
                        view.info_image.setImageResource(R.drawable.d_n_scatteredclouds)
                        view.infoText.text = description
                    }

                    "overcast clouds" -> {
                        view.info_image.setImageResource(R.drawable.d_n_brokenclouds)
                        view.infoText.text = description
                    }
                }

                "Rain" -> when (description){

                    "shower rain" -> {
                        view.info_image.setImageResource(R.drawable.shower_rain)
                        view.infoText.text = description
                    }

                    "light intensity rain" -> {
                        view.info_image.setImageResource(R.drawable.shower_rain)
                        view.infoText.text = description
                    }

                    "heavy intensity shower rain" -> {
                        view.info_image.setImageResource(R.drawable.shower_rain)
                        view.infoText.text = description
                    }

                    "ragged rain" -> {
                        view.info_image.setImageResource(R.drawable.shower_rain)
                        view.infoText.text = description
                    }

                    "light rain" -> {
                        if (dayTime >= sunRise && dayTime < sunSet){
                            view.info_image.setImageResource(R.drawable.drainy)
                        }else view.info_image.setImageResource(R.drawable.nrainy)

                        view.infoText.text = description
                    }

                    "freezing rain" -> {
                        view.info_image.setImageResource(R.drawable.snow)
                        view.infoText.text = description
                    }

                    "extreme rain" -> {
                        if (dayTime >= sunRise && dayTime < sunSet){
                            view.info_image.setImageResource(R.drawable.drainy)
                        }else view.info_image.setImageResource(R.drawable.nrainy)

                        view.infoText.text = description
                    }

                    "moderate rain" -> {
                        if (dayTime >= sunRise && dayTime < sunSet){
                            view.info_image.setImageResource(R.drawable.drainy)
                        }else view.info_image.setImageResource(R.drawable.nrainy)

                        view.infoText.text = description
                    }

                    "heavy intensity rain" ->{
                        if (dayTime >= sunRise && dayTime < sunSet){
                            view.info_image.setImageResource(R.drawable.drainy)
                        }else view.info_image.setImageResource(R.drawable.nrainy)

                        view.infoText.text = description
                    }

                    "very heavy rain" -> {
                        if (dayTime >= sunRise && dayTime < sunSet){
                            view.info_image.setImageResource(R.drawable.drainy)
                        }else view.info_image.setImageResource(R.drawable.nrainy)

                        view.infoText.text = description
                    }
                }

                "Drizzle" -> {
                    view.info_image.setImageResource(R.drawable.shower_rain)
                    view.infoText.text = description
                }

                "Thunderstorm" -> {
                    view.info_image.setImageResource(R.drawable.thunderstrome)
                    view.infoText.text = description
                }

                "Snow" -> {
                    view.info_image.setImageResource(R.drawable.snow)
                    view.infoText.text = description
                }

                "Mist" -> {
                    view.info_image.setImageResource(R.drawable.mist)
                    view.infoText.text = description
                }

            }

        }

    }
}