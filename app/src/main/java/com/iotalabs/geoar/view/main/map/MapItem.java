package com.iotalabs.geoar.view.main.map;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import androidx.core.content.ContextCompat;

import com.example.lotalabsappui.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.List;

public class MapItem {

    private final int markerHeight = 110;
    private final int markerWidth = 110;
    private final int[] colors = {
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0)    // red
    };
    private final float[] startPoints = {0.2f, 1f};
    private Gradient gradient;
    private HeatmapTileProvider provider;

    public MapItem(){
        gradient = new Gradient(colors, startPoints);//그라데이션
    }
    public void createHitMap(GoogleMap mMap, List<LatLng> users){
        provider = new HeatmapTileProvider.Builder().data(users).gradient(gradient).build();
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));//히트맵 만듬
    }
    public Bitmap setMarker(Context context){
        //마커 크기 및 아이콘 생성
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.map_marker);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap friend_marker = Bitmap.createScaledBitmap(bitmap, markerWidth, markerHeight, false);
        return friend_marker;
    }
}
