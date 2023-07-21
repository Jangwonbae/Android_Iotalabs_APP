package com.iotalabs.geoar.view.main.activity.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.iotalabs.geoar.data.Constants;
import com.iotalabs.geoar.view.main.activity.DataBaseViewModel;
import com.iotalabs.geoar.view.main.data.FriendData;
import com.iotalabs.geoar.view.main.util.map.MapItem;
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


        initMapFloatingButton();//플로팅버튼 초기화
        users=new ArrayList<>();
        mapFriends=new ArrayList<>();

        dataBaseViewModel.getAllUserData();//데이터 받아오기

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

        if(mMap != null){
            initObserveLiveData();//다른 엑티비티가 프래그먼트를 가리면 구독이 취소되더라
            dataBaseViewModel.getAllUserData();
        }
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
    public void onHiddenChanged(boolean hidden) {//세팅에서 값을 바꿀 경우 실행
        super.onHiddenChanged(hidden);
        if(!hidden){
            reNewMap();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapItem.setMap(mMap,getContext());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.startingLoaction, Constants.startingZoom));
        initObserveLiveData();
    }


    public void createMyLocation() {//내위치만들기
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "위치권한을 허용해주세요.", Toast.LENGTH_LONG).show();
            mMap.setMyLocationEnabled(false);
        } else {
            if (prefs.getBoolean("switch_my_location", true)) {//세팅에서 온상태면(내위치)
                mMap.setMyLocationEnabled(true);//내위치 만듬
            }
            else{
                mMap.setMyLocationEnabled(false);
            }
        }
    }

    public void createFriendMarker() {//친구마커 만들기
        if (prefs.getBoolean("switch_friend_location", true)) {//세팅에서 온상태면(친구위치)
            try {
                for(FriendData fData : mapFriends){
                    mapItem.createMarker(fData);
                }
            } catch (Exception e) {
                Log.d("MapFragment Marker",e.toString());
            }
        }
    }

    public void createHeatMap() {//히트맵만들기
        try{
            if (prefs.getBoolean("switch_heatmap", true)) {//세팅에서 온상태면(히트맵)
                mapItem.createHeatMap(users);//히트맵 생성
            }
        }catch (Exception e){
            Log.d("createHeatMap",e.toString());
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

    public void reNewMap() {//맵 갱신
        mMap.clear();
        createMyLocation();
        createFriendMarker();
        createHeatMap();
        createPloy();
    }
    public void initMapFloatingButton(){
        binding.ftBtnRenew.setOnClickListener(new View.OnClickListener() {  //새로고침 버튼 이벤트
            @Override
            public void onClick(View v) {
                //맵갱신
                dataBaseViewModel.getAllUserData();
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
    }
    public void initObserveLiveData(){
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
                mapFriends=friendData;
                reNewMap();
            }
        });
    }
}