package com.iotalabs.geoar.view.main;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.iotalabs.geoar.data.Constants;
import com.iotalabs.geoar.view.main.adapter.friend_list.FriendData;
import com.iotalabs.geoar.view.main.map.MapItem;
import com.unity3d.player.UnityPlayerActivity;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMapBinding binding;
    private DataBaseViewModel dataBaseViewModel;
    private GoogleMap mMap;
    private MapView mapView = null;
    private List<LatLng> users;
    private List<FriendData> mapFriends;
    private SharedPreferences prefs;
    private MapItem mapItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mapItem=new MapItem();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //데어터 바인딩
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        //뷰모델 생성
        dataBaseViewModel = new ViewModelProvider(this).get(DataBaseViewModel.class);
        //뷰모델 연결
        binding.setViewModel(dataBaseViewModel);

        users=new ArrayList<LatLng>();
        mapFriends=new ArrayList<FriendData>();

        dataBaseViewModel.getAllUserData();


        binding.ftBtnRenew.setOnClickListener(new View.OnClickListener() {  //새로고침 버튼 이벤트
            @Override
            public void onClick(View v) {
                //맵갱신
                reNewMap();
            }
        });

        binding.ftBtnGoAR.setOnClickListener(new View.OnClickListener() {  //AR버튼 이벤트
            @Override
            public void onClick(View v) {
                //AR화면으로 이동
                Intent intent = new Intent(getActivity(), UnityPlayerActivity.class);
                startActivity(intent);
            }
        });

        mapView = binding.map;
        mapView.getMapAsync(this);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.928825, 127.498833), 14));//순천국가정원 이동

        //liveData(전체 사용자의 위치)의 값을 관찰하다가 값이 바뀌면 실행
        dataBaseViewModel.allUserLocationList.observeInOnStart(this, new Observer<List<LatLng>>() {
            @Override
            public void onChanged(List<LatLng> latLngs) {
                //DB로 부터 받은 데이터가 바뀌면 실행
                users = latLngs;
                reNewMap();
            }
        });
        dataBaseViewModel.myFriendList.observeInOnStart(this, new Observer<ArrayList<FriendData>>() {
            @Override
            public void onChanged(ArrayList<FriendData> friendData) {
                mapFriends= friendData;
                reNewMap();

            }
        });
        createMyLocation();//내위치만들기
        createFriendMarker();//친구마커 만들기
        createHitMap();//히트맵만들기
        createPloy();
    }

    public void createMyLocation() {//내위치만들기
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "위치권한을 허용해주세요.", Toast.LENGTH_LONG).show();
            mMap.setMyLocationEnabled(false);
        } else {
            if (prefs.getBoolean("key_me", true)) {//세팅에서 온상태면(내위치)
                mMap.setMyLocationEnabled(true);//내위치 만듬
            }
        }
    }

    @SuppressLint("Range")
    public void createFriendMarker() {//친구마커 만들기
        if (prefs.getBoolean("key_friend", true)) {//세팅에서 온상태면(친구위치)
            try {
                for(FriendData fData : mapFriends){
                    MarkerOptions makerOptions = new MarkerOptions();
                    makerOptions
                            .position(new LatLng(fData.getLatitude(),fData.getLongitude()))//위치
                            .title(fData.getName())// 타이틀.
                            .icon(BitmapDescriptorFactory.fromBitmap(mapItem.setMarker(getContext())));//마커 모양
                    //마커 생성 (마커를 나타냄)
                    mMap.addMarker(makerOptions);
                }
            } catch (Exception e) {
                Log.d("MapFragment Marker",e.toString());
            }
        }
    }

    @SuppressLint("Range")
    public void createHitMap() {//히트맵만들기
        try{
            if (prefs.getBoolean("key_add_hitt", true)) {//세팅에서 온상태면(히트맵)
                mapItem.createHitMap(mMap, users);//히트맵 생성
            }
        }catch (Exception e){
            Log.d("createHitMap",e.toString());
        }
    }

    public void createPloy() {
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.clickable(true)
                .strokeColor(Color.RED)
                .strokeWidth(5);
        for(LatLng latLng : Constants.area){
            polygonOptions.add(latLng);
        }
        mMap.addPolygon(polygonOptions);
    }

    public void reNewMap() {
        mMap.clear();
        createMyLocation();
        createFriendMarker();
        createHitMap();
        createPloy();
    }
}