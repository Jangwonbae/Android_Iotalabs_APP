package com.iotalabs.geoar.view.intro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.ActivityIntroBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.iotalabs.geoar.data.ClassUUID;
import com.iotalabs.geoar.data.PersonName;
import com.iotalabs.geoar.util.auth.Authenticator;
import com.iotalabs.geoar.view.enter_name.EnterNameActivity;
import com.iotalabs.geoar.view.main.activity.DataBaseViewModel;
import com.iotalabs.geoar.view.main.activity.MainActivity;

public class IntroActivity extends AppCompatActivity {
    private ActivityIntroBinding binding;
    private static final String TAG = IntroActivity.class.getSimpleName();
    private static final int GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE = 100; //퍼미션 REQUEST_CODE
    private static final int GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE = 101; //퍼미션 REQUEST_CODE
    public static final int DEFAULT_LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY; //가장 정확하게
    public static final long DEFAULT_LOCATION_REQUEST_INTERVAL = 10 * 1000;//최대 10초
    public static final long DEFAULT_LOCATION_REQUEST_FAST_INTERVAL = 5 * 1000;//최소 5초
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //데이터 바인딩
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
        binding.setActivity(this);

        new ClassUUID(getBaseContext());//UUID를 static으로 저장
        new DataBaseViewModel().getAllUserData();//데이터받기
        new Authenticator().authFireBase();//인증
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        int accessLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (accessLocation == PackageManager.PERMISSION_GRANTED) {//위치권한이 허용됐는지 체크
            checkLocationSetting();//gps세팅 확인하는 메소드
        }
        else {//위치권한이 허용되지않았으면 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE);//위치권한 요청
        }
    }

    //요청한 결과를 확인
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GPS_UTIL_LOCATION_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //위치권한이 확인됐을 때
                        checkLocationPermission();
                    } else {
                        //확인 되지 않을 때 팝업을 띄움
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("위치 권한이 꺼져있습니다.");
                        builder.setMessage("[권한] 설정에서 위치 권한을 허용해야 합니다.");
                        builder.setPositiveButton("설정으로 가기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).setNegativeButton("종료", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    break;
                }
            }
        }
    }

    private void checkLocationSetting() {
        //GPS 설정을 확인
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(DEFAULT_LOCATION_REQUEST_PRIORITY);
        locationRequest.setInterval(DEFAULT_LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(DEFAULT_LOCATION_REQUEST_FAST_INTERVAL);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);//위에서 설정한 정보를 세팅
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);
        settingsClient.checkLocationSettings(builder.build())//위에서 설정한 정보를 제공할 수 있는지 확인
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {//제공할 수 있으면 (통상적으로 위치서비스가 활성화 되있을 때)
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                        PersonName personName = PersonName.getInstance(getBaseContext());
                        String name = personName.getName();

                        //인텐트 이동
                        if(name.equals("")){//이름이 없으면
                            //이름입력엑티비티 이동
                            Intent intent = new Intent(IntroActivity.this, EnterNameActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            //메인으로 이동
                            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }
                })
                .addOnFailureListener(IntroActivity.this, new OnFailureListener() {//제공할 수 없으면 (통상적으로 위치서비스가 활성화 되있지 않을 때)
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED://설정을 통해 문제해결이 가능할 때
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(IntroActivity.this, GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.w(TAG, "unable to start resolution for result due to " + sie.getLocalizedMessage());//고칠수있는문제
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE://설정을 통해 문제해결이 가능하지 않을 때
                                String errorMessage = "location settings are inadequate, and cannot be fixed here. Fix in Settings.";//못고치는문제
                                Log.e(TAG, errorMessage);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_UTIL_LOCATION_RESOLUTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                checkLocationSetting();
            } else {
                finish();
            }
        }
    }

}