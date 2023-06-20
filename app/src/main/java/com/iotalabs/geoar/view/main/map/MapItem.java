package com.iotalabs.geoar.view.main.map;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import androidx.core.content.ContextCompat;

import com.example.lotalabsappui.R;

public class MapItem {

    private final int markerHeight = 110;
    private final int markerWidth = 110;
    public MapItem(){

    }
    public Bitmap setMarker(Context context){
        //마커 크기 및 아이콘 생성
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.map_marker);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap friend_Marker = Bitmap.createScaledBitmap(bitmap, markerWidth, markerHeight, false);
        return friend_Marker;
    }
}
