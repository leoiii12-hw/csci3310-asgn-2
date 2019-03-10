package com.cuhk.floweryspot

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.support.annotation.AnyRes
import android.support.media.ExifInterface
import java.io.IOException
import android.content.Intent


private val images = arrayOf(
    R.drawable.image01,
    R.drawable.image02,
    R.drawable.image03,
    R.drawable.image04,
    R.drawable.image05,
    R.drawable.image06,
    R.drawable.image07,
    R.drawable.image08,
    R.drawable.image09,
    R.drawable.image10
)

const val SHOW_LOCATION = "Show Location on Map"
const val SHOW_PANORAMA = "Show StreetView Panorama"

class MyItemClickListener : MyRecyclerViewAdapter.ItemClickListener {

    override fun onItemClick(view: View, position: Int) {
        // println("$view $position")
    }

}

data class Location(val latitude: String?, val latitudeRef: String?, val longitude: String?, val longitudeRef: String?, val altitude: String?)


object Utils {

    fun parseDMS(dmsStr: String, dmsRef: String): Double {
        val regex = Regex("(\\d+).(\\d+).(\\d+).(\\d+).(\\d+).(\\d+)")
        val groupValues = regex.find(dmsStr)!!.groupValues.subList(1, 7).map { gv -> gv.toInt().toDouble() }

        val dms = Triple(groupValues[0] / groupValues[1], groupValues[2] / groupValues[3], groupValues[4] / groupValues[5])

        var dd = dms.first + dms.second / 60 + dms.third / (60 * 60);
        if (dmsRef == "S" || dmsRef == "W") {
            dd *= -1;
        }

        return dd
    }
}


class MyItemCreateContextMenuListener : MyRecyclerViewAdapter.ItemCreateContextMenuListener {

    var selectedLocation: Location? = null

    private fun getUriFromDrawable(context: Context, @AnyRes drawableId: Int): Uri {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + context.getResources().getResourcePackageName(drawableId)
                    + '/'.toString() + context.getResources().getResourceTypeName(drawableId)
                    + '/'.toString() + context.getResources().getResourceEntryName(drawableId)
        )
    }

    override fun onItemCreateContextMenu(view: View, position: Int, menu: ContextMenu?, menuInfo: ContextMenu.ContextMenuInfo?) {
        if (menu != null) {
            menu.setHeaderTitle("Context Menu")
            menu.add(0, view.id, 0, SHOW_LOCATION)
            menu.add(0, view.id, 0, SHOW_PANORAMA)
        }

        val context = view.context
        val uri = getUriFromDrawable(context, images[position])

        try {
            val location = context.contentResolver.openInputStream(uri).use { inputStream ->
                val exif = ExifInterface(inputStream)

                val latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
                val latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
                val longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
                val longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
                val altitude = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE)

                return@use Location(latitude, latitudeRef, longitude, longitudeRef, altitude)
            }

            this.selectedLocation = location
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}

class MainActivity : AppCompatActivity() {

    private val clickListener = MyItemClickListener()
    private val createContextMenuListener = MyItemCreateContextMenuListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = MyRecyclerViewAdapter(this, images)

        adapter.setClickListener(clickListener)
        adapter.setCreateContextMenuListener(createContextMenuListener)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) as RecyclerView.LayoutManager
        recyclerView.adapter = adapter
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        val selectedLocation = createContextMenuListener.selectedLocation!!

        val intent = Intent(this, MapsActivity::class.java)

        intent
            .putExtra(MapsActivity_Intent.ACTION.toString(), menuItem.toString())
            .putExtra(MapsActivity_Intent.LATITUDE.toString(), selectedLocation.latitude)
            .putExtra(MapsActivity_Intent.LATITUDE_REF.toString(), selectedLocation.latitudeRef)
            .putExtra(MapsActivity_Intent.LONGITUDE.toString(), selectedLocation.longitude)
            .putExtra(MapsActivity_Intent.LONGITUDE_REF.toString(), selectedLocation.longitudeRef)
            .putExtra(MapsActivity_Intent.ALTITUDE.toString(), selectedLocation.altitude)

        startActivity(intent)

        return true
    }

}
