package com.iotalabs.geoar.view.main.util.map;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import androidx.core.content.ContextCompat;

import com.example.lotalabsappui.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.iotalabs.geoar.data.Constants;
import com.iotalabs.geoar.view.main.data.FriendData;

import java.util.ArrayList;
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
    private GoogleMap map;
    private Context context;
    public MapItem(){
        gradient = new Gradient(colors, startPoints);//그라데이션
    }
    //mMap : 구글맵, users : 전체 사용자 위치정보

    public void setMap(GoogleMap map,Context context){
        this.map=map;
        this.context=context;
    }

    public void createHeatMap(List<LatLng> users){
        List<LatLng> area = Constants.area;
        List<LatLng> insideUsers = null;
        for(LatLng latlng:users ) {
            boolean inside= PolyUtil.containsLocation(latlng ,area,true);
            if(inside) {//내위치가 지정구역안에 있는지 체크
                insideUsers = new ArrayList<>();
                insideUsers.add(latlng);
            }
        }
        provider = new HeatmapTileProvider.Builder().data(insideUsers).gradient(gradient).build();
        map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));//히트맵 만듬
    }
    public void createMarker(FriendData fData){
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(new LatLng(fData.getLatitude(),fData.getLongitude()))//위치
                .title(fData.getName())// 타이틀.
                .icon(BitmapDescriptorFactory.fromBitmap(setMarker(context)));//마커 모양
        //마커 생성 (마커를 나타냄)
        map.addMarker(makerOptions);
    }
    public Bitmap setMarker(Context context){
        //마커 크기 및 아이콘 생성
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.map_marker);
        Bitmap bitmap = bitmapdraw.getBitmap();
        Bitmap friend_marker = Bitmap.createScaledBitmap(bitmap, markerWidth, markerHeight, false);
        return friend_marker;
    }//friend_marker : Bitmap(커스텀 마커)
}
