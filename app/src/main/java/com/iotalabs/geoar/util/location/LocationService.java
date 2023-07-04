package com.iotalabs.geoar.util.location;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.PolyUtil;
import com.iotalabs.geoar.data.Clock;
import com.iotalabs.geoar.util.noti.NotificationCreator;
import com.iotalabs.geoar.view.main.data.PersonLocation;
import com.iotalabs.geoar.data.StaticUUID;
import com.iotalabs.geoar.data.Constants;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class LocationService extends Service implements  LocationListener {
    public static Intent serviceIntent = null;
    private final String TAG = "Service";
    private final String TAG_LOCATION = "TAG_LOCATION";
    private Context context;
    private boolean stopService = false;


    protected LocationSettingsRequest mLocationSettingsRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    private String UUID;
    private String str_latitude = "0.0", str_longitude = "0.0";
    private List<LatLng> area;

    private boolean geo_check = false;//default를 false로 해야 어플 처음 시작할 때 밖에 있으면 알림이 안뜸

    private DatabaseReference mDatabase;
    private PersonLocation personLocation;

    private NotificationCreator getOutNotification;
    private NotificationCreator useingLocationNotification;

    //Service
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        UUID= StaticUUID.UUID;
        area = Constants.area;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        personLocation = PersonLocation.getInstance();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//서비스를 시작하도록 요청
        StartForeground();
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    if (!stopService) {
                        //데이터 베이스에 나의 위치정보를 저장
                        mDatabase.child("USER").child(UUID).child("location").setValue(personLocation);
                        boolean inside= PolyUtil.containsLocation(new LatLng(Double.parseDouble(str_latitude),
                                Double.parseDouble(str_longitude)),area,true);
                        if(inside){//내위치가 지정구역안에 있는지 체크
                            geo_check = true;
                        }else{//벗어났다면 자신에게 알림 후 나를 팔로우하는 친구에게 푸시알림
                            if(geo_check==true){
                                String title = "지정영역 벗어남 알림";
                                String message = "지정영역을 벗어났습니다.";
                                String channelId = "getOutArea";
                                String channelName = "locationGetOutArea";

                                getOutNotification= new NotificationCreator(title, message,
                                        getApplicationContext(), channelId, channelName);//노티 생성
                                getOutNotification.showNotification();

                                //푸시알림
                                /*
                                SharedPreferences prefs = getSharedPreferences("person_name",0);
                                String name = prefs.getString("name","");
                                task4=new PushNoti();//생성
                                task4.execute("http://" + IP_ADDRESS + "/push.php", UUID,name);//친구에게 노티보냄*/

                                geo_check=false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (!stopService) {
                        handler.postDelayed(this, TimeUnit.SECONDS.toMillis(10));
                    }
                }
            }
        };

        handler.postDelayed(runnable, 5000);//5초마다
        startLocationCheck();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {//서비스를 소멸시킬 때 호출
        Log.e(TAG, "Service Stopped");
        stopService = true;
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Log.e(TAG_LOCATION, "Location Update Callback Removed");
        }
        serviceIntent=null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }//다른 구성요소와 서비스를 바인딩하려는 경우 호출

    private void StartForeground() {//서비스가 시작할 때 <위치정보 사용중> 노티 띄우기
        String title = "IotalabsApp";
        String message = "위치 정보 사용중";
        String channelId = "location_channel";
        String channelName = "어플 사용중 위치 서비스 사용 알림";

        useingLocationNotification = new NotificationCreator(title,message,getApplicationContext(),channelId,channelName);
        startForeground(101, useingLocationNotification.showUseingLocationNoti());
    }

    //LocationListener
    @Override
    public void onLocationChanged(Location location) {//위치 이동이나 시간 경과 등으로 호출
        Log.e(TAG_LOCATION, "Location Changed Latitude : " + location.getLatitude() + "\tLongitude : " + location.getLongitude());

        str_latitude = String.valueOf(location.getLatitude());
        str_longitude = String.valueOf(location.getLongitude());

        personLocation.setTime(new Clock().getTime());
        personLocation.setLatitude(str_latitude);
        personLocation.setLongitude(str_longitude);

        if (str_latitude.equalsIgnoreCase("0.0") && str_longitude.equalsIgnoreCase("0.0")) {
            requestLocationUpdate();
        } else {
            Log.e(TAG_LOCATION, "Latitude : " + location.getLatitude() + "\tLongitude : " + location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {//위치 공급자의 상태가 바뀔때 호출
    }

    @Override
    public void onProviderEnabled(String provider) {//위치 공급자가 사용 불가능해질 때 호출
    }

    @Override
    public void onProviderDisabled(String provider) {//위치 공급자가 사용 가능해질 때

    }


    public void startLocationCheck(){
        mLocationRequest = LocationRequest.create()
                .setInterval(10 * 1000)//최대 10초
                .setFastestInterval(5 * 1000)//최대 5초
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//가장 정확하게
        mSettingsClient = LocationServices.getSettingsClient(context);//위에서 설정한 정보를 세팅

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();

        mSettingsClient//세팅한대로 정보를 제공할 수 있는지 체크
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {//통상적으로 GPS설정이 켜져있다면
                        Log.e(TAG_LOCATION, "GPS Success");
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);//위치제공자를 생성
                        requestLocationUpdate();//위치업데이트
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            int REQUEST_CHECK_SETTINGS = 214;
                            ResolvableApiException rae = (ResolvableApiException) e;
                            rae.startResolutionForResult((AppCompatActivity) context, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.e(TAG_LOCATION, "Unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG_LOCATION, "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Log.e(TAG_LOCATION, "checkLocationSettings -> onCanceled");
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.e(TAG_LOCATION, "Location Received");
                mCurrentLocation = locationResult.getLastLocation();
                onLocationChanged(mCurrentLocation);
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

}