package com.cuhk.floweryspot

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


enum class MapsActivity_Intent {
    ACTION,
    LATITUDE,
    LATITUDE_REF,
    LONGITUDE,
    LONGITUDE_REF,
    ALTITUDE
}

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var panorama: StreetViewPanorama

    private lateinit var mapTypesFragment: MapTypesFragment
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var panoramaFragment: SupportStreetViewPanoramaFragment

    private var intentAction: String = ""
    private var intentLatitude: Double = 0.0
    private var intentLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        intentAction = intent.getStringExtra(MapsActivity_Intent.ACTION.toString())
        intentLatitude = Utils.parseDMS(intent.getStringExtra(MapsActivity_Intent.LATITUDE.toString()), intent.getStringExtra(MapsActivity_Intent.LATITUDE_REF.toString()))
        intentLongitude = Utils.parseDMS(intent.getStringExtra(MapsActivity_Intent.LONGITUDE.toString()), intent.getStringExtra(MapsActivity_Intent.LONGITUDE_REF.toString()))

        mapTypesFragment = supportFragmentManager.findFragmentById(R.id.map_types_fragment)!! as MapTypesFragment
        mapFragment = supportFragmentManager.findFragmentById(R.id.map)!! as SupportMapFragment
        panoramaFragment = supportFragmentManager.findFragmentById(R.id.panorama)!! as SupportStreetViewPanoramaFragment

        switch(intentAction)

        mapTypesFragment.setOnCheckedChangeListener(object : MapTypesFragment.OnCheckedChangeListener {
            override fun onCheckChange(checkedId: Int) {
                val action = when (checkedId) {
                    0 -> SHOW_LOCATION
                    1 -> SHOW_PANORAMA
                    else -> ""
                }

                switch(action)
            }
        })

        mapFragment.getMapAsync(this)
        panoramaFragment.getStreetViewPanoramaAsync(this)
    }

    fun switch(action: String) {
        when (action) {
            SHOW_LOCATION -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.show(mapFragment)
                fragmentTransaction.hide(panoramaFragment)
                fragmentTransaction.commit()

                mapTypesFragment.setMap()
            }
            SHOW_PANORAMA -> {
                val fragmentTransaction = supportFragmentManager.beginTransaction()
                fragmentTransaction.show(panoramaFragment)
                fragmentTransaction.hide(mapFragment)
                fragmentTransaction.commit()

                mapTypesFragment.setPanorama()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val latLng = LatLng(intentLatitude, intentLongitude)
        map.addMarker(MarkerOptions().position(latLng).title("Marker"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19.0f))
    }

    override fun onStreetViewPanoramaReady(panorama: StreetViewPanorama) {
        this.panorama = panorama

        val latLng = LatLng(intentLatitude, intentLongitude)
        panorama.setPosition(latLng)

        panorama.isStreetNamesEnabled = true
        panorama.isUserNavigationEnabled = false
        panorama.isZoomGesturesEnabled = false

    }

}
