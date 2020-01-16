package com.googlemapwithdrawpathapp.polilineAnimator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class InfoWindowCustom(internal var context: Context) : GoogleMap.InfoWindowAdapter {
     var inflater: LayoutInflater?= null

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View? {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // R.layout.echo_info_window is a layout in my
        // res/layout folder. You can provide your own

        /*  View v = inflater.inflate(R.layout.echo_info_window, null);

        TextView title = v.findViewById(R.id.info_window_title);
        title.setText(marker.getTitle());*/
        return null
    }
}