package com.example.oneforall

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.CarrierConfigManager.Gps
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class SplashScreen : AppCompatActivity() {
    lateinit var mfusedLocation:FusedLocationProviderClient
    private var myRequestcode = 1010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(this,WeatherApp::class.java)
            startActivity(intent)
            finish()
        },2000)

        mfusedLocation = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (chechPermission()){
            if(LocationEnable()){
                mfusedLocation.lastLocation.addOnCompleteListener{
                    task->
                    var location:Location?=task.result
                    if(location==null){
                        NewLocation()
                    }
                    else{
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this,WeatherApp::class.java)
                            intent.putExtra("lat",location.latitude.toString())
                            intent.putExtra("long",location.longitude.toString())
                            startActivity(intent)
                        },2000)
                    }
                }
            }
            else{
                Toast.makeText(this,"Please Turn on your gps",Toast.LENGTH_LONG).show()
            }

        }
        else {
            RequestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun NewLocation() {
        var locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        mfusedLocation = LocationServices.getFusedLocationProviderClient(this)
        mfusedLocation.requestLocationUpdates(locationRequest,locationCallBack,Looper.myLooper())

    }
    private val locationCallBack = object:LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation:Location=p0.lastLocation
        }
    }


    private fun chechPermission(): Boolean {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun RequestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION),myRequestcode)
    }
    private fun LocationEnable(): Boolean {
        var locationManager =getSystemService(LOCATION_SERVICE)as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == myRequestcode){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getLastLocation()
            }
        }
    }
}