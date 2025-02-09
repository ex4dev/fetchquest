package dev.tswanson.fetchquest.android

import android.widget.Button
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow

// https://github.com/osmdroid/osmdroid/wiki/Markers%2C-Lines-and-Polygons-%28Kotlin%29
class MapPointInfoWindow(mapView: MapView, private val quest: Event) :
    InfoWindow(R.layout.fragment_map_point_info_window, mapView) {

    override fun onOpen(item: Any?) {
        closeAllInfoWindowsOn(mapView)

        val title = mView.findViewById<TextView>(R.id.text_view)
        title.text = quest.title

        val desc = mView.findViewById<TextView>(R.id.text_description)
        desc.text = quest.description

        val button = mView.findViewById<Button>(R.id.view_button)
        button.setOnClickListener {
            // TODO: navigate to the details page for the event
        }

        mapView.setOnClickListener {
            close()
        }
    }

    override fun onClose() {

    }
}