package com.pr.kotlin_antiradar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.media.AudioManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),OnMapReadyCallback {
    //only last loocation
    lateinit var fusedLocationClient: FusedLocationProviderClient
    //location updates
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    lateinit var locationCallback: LocationCallback
    val REQUEST_CHECK_SETTINGS = 10001;
    lateinit var googleMap: GoogleMap
    val arraylist=ArrayList<MyLocatioin>()

    var lat: Double? = 40.0
    var lon: Double? = 60.0

    var lat2: Double? = 40.0
    var lon2: Double? = 60.0
    lateinit var mediaPlayer: MediaPlayer

    var camera=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googlemap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mediaPlayer=MediaPlayer.create(this@MainActivity, R.raw.estradar)
        lastlocation()
        buildLocationRequest()
        startLocationUpdates()




        arraylist.add(MyLocatioin(41.5073230,60.4802440))
        arraylist.add(MyLocatioin(41.5193550,60.5510650))
        arraylist.add(MyLocatioin(41.5229400,60.5628200))
        arraylist.add(MyLocatioin(41.5329430,60.5955920))
        arraylist.add(MyLocatioin(41.5420680,60.6054800))
        arraylist.add(MyLocatioin(41.5581680,60.5974010))
        arraylist.add(MyLocatioin(41.5780330,60.5739930))
        arraylist.add(MyLocatioin(41.6765130,60.7389000))

        linearlay.setOnClickListener {
            if (camera){
                imageview1.setImageResource(R.drawable.ic_round_map_24)
                camera=false
            }else{
                imageview1.setImageResource(R.drawable.ic_round_directions_car_24)
                camera=true
            }
        }


    }



    fun lastlocation() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                Toast.makeText(
                    this@MainActivity,
                    "${it.latitude} / ${it.longitude}",
                    Toast.LENGTH_LONG
                ).show()

//                lat=it.latitude
//                lon=it.longitude


            }
        }
    }

    //location updates
    fun buildLocationRequest() {
        locationRequest = com.google.android.gms.location.LocationRequest.create()
        locationRequest.priority =
            com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 5000
    }

    //location updates
    fun startLocationUpdates() {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return

                for (location in p0.locations) {

                    GlobalScope.launch(Dispatchers.Main) {
                        delay(500)
                        lat = location.latitude
                        lon = location.longitude
                        onMapReady(googleMap)
                    }

                    // Log.d("Pr", "${location.latitude} / ${location.longitude}")
                      //Toast.makeText(this@MainActivity, " ${location.latitude} / ${location.longitude}", Toast.LENGTH_SHORT).show()
                }

            }
        }
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap=p0
        googleMap.clear()

        var lastlocation = LatLng(lat!!, lon!!) //Beruniy
        var markerOptions = MarkerOptions()
                    .position(lastlocation)
                    .title("Я")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.qizil2))
        googleMap.addMarker(markerOptions)
        if (camera){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastlocation, 15f))
        }

        GlobalScope.launch(Dispatchers.Main) {
            delay(1000)
        for (i in 0..arraylist.size-1){
            delay(1000)
            var lastlocation2 = readlatlang(i)
            var markerOptions2 = MarkerOptions()
                .position(lastlocation2)
                .title("Radar $i")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.radarformap))
            googleMap.addMarker(markerOptions2)



            var location1 = Location("Я")//41.67739/60.738
            location1.latitude = lat as Double
            location1.longitude = lon as Double
            var location2 = Location("Radar")
            location2.latitude = lat2 as Double
            location2.longitude = lon2 as Double
            var distance = location1.distanceTo(location2)
            Log.d("Pr7","${distance.toInt()} m")

                if (distance.toInt()<100){
                    mediaPlayer.start()
                    Log.d("Pr7","distansiya 100 dan kichkina m")

                    delay(4000)

                }else{

                   // mediaPlayer.stop()

                }


            //Toast.makeText(this@MainActivity, "${distance.toInt()} m", Toast.LENGTH_SHORT).show()
        }
    }


    }

    fun readlatlang(i:Int):LatLng{
        lat2=arraylist.get(i).lat
        lon2=arraylist.get(i).lon
        return LatLng(lat2!!,lon2!!)
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1 -> {
                startActivity(Intent(this@MainActivity, MainActivity2::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }
}