package com.iotalabs.geoar.view.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.iotalabs.geoar.util.auth.Authenticator;
import com.iotalabs.geoar.util.location.BackgroundLocationUpdateService;
import com.iotalabs.geoar.view.create_qr_code.CreateQR_codeActivity;
import com.iotalabs.geoar.view.main.adapter.friend_list.FriendData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DataBaseViewModel dataBaseViewModel;

    private IntentIntegrator qrScan;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapFragment mapFragment;
    private ListFragment listFragment;
    private SettingFragment settingFragment;
    private Animation ft_btn_open, ft_btn_close;
    private Boolean isFabOpen = false;
    private long backKeyPressedTime = 0; //뒤로가기 버튼 눌렀던 시간 저장
    private Toast toast;//첫번째 뒤로가기 버튼을 누를때 표시하는 변수
    private final String TAG = "MainAactivityTAG";
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //데이터 바인딩
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        dataBaseViewModel = new ViewModelProvider(this).get(DataBaseViewModel.class);
        binding.setViewModel(dataBaseViewModel);

        new Authenticator().authFireBase();//인증

        binding.frameLayoutMainWhole.bringToFront();//버튼이 있는 fragment가 제일 앞으로 오도록 설정

        //Fragment 객체생성
        mapFragment = new MapFragment();
        listFragment = new ListFragment();
        settingFragment = new SettingFragment();

        //백그라운드 위치서비스
        startService(new Intent(this, BackgroundLocationUpdateService.class));

        //애니메시션 객체생성
        ft_btn_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_ft_btn_open);
        ft_btn_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_ft_btn_close);

        //네비게이션바 클릭리스너
        binding.navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_map://MapFragment
                        setFrag(0);
                        break;
                    case R.id.action_list://ListFragment
                        setFrag(1);
                        break;
                    case R.id.action_set://SettingFragment
                        setFrag(2);
                        break;
                }

                return true;
            }
        });
        setFrag(0); // 첫 프래그먼트 화면 지정

        //플로팅 버튼 클릭메소드
        binding.ftBtnMain.setOnClickListener(new View.OnClickListener() {//열렸다 닫혔다 이벤트
            @Override
            public void onClick(View v) {
                anim();
            }
        });
        binding.ftBtnCreateQR.setOnClickListener(new View.OnClickListener() {//QR생성 클릭시 QR코드 엑티비티로 이동
            @Override
            public void onClick(View v) {
                anim();
                Intent intent = new Intent(MainActivity.this, CreateQR_codeActivity.class);
                startActivity(intent);
            }
        });
        binding.ftBtnReadQR.setOnClickListener(new View.OnClickListener() {//QR리더기 클릭시 카메라 엑티비티로 이동
            @Override
            public void onClick(View v) {
                anim();
                qrScan = new IntentIntegrator(MainActivity.this);
                qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
                qrScan.initiateScan();

            }
        });

    }


    public void anim() {
        if (isFabOpen) {
            binding.ftBtnCreateQR.startAnimation(ft_btn_close);
            binding.ftBtnReadQR.startAnimation(ft_btn_close);
            binding.ftBtnCreateQR.setClickable(false);
            binding.ftBtnReadQR.setClickable(false);
            isFabOpen = false;
        } else {
            binding.ftBtnCreateQR.startAnimation(ft_btn_open);
            binding.ftBtnReadQR.startAnimation(ft_btn_open);
            binding.ftBtnCreateQR.setClickable(true);
            binding.ftBtnReadQR.setClickable(true);
            isFabOpen = true;
        }
    }

    // 프레그먼트 교체
    public void setFrag(int n) {

        // FragmentManager를 통해서 FragmentTransaction 획득하기
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        //setReorderingAllowed(true)는 transaction과 관련된 프래그먼트의 상태 변경을 최적화하여 애니메이션과 전환이 올바르게 작동하도록 함
        fragmentTransaction.setReorderingAllowed(true);

        switch (n) {
            case 0://map
                //replace 위에 쌓여진 Fragement들을 버려버리고 새로운 Fragment를 쌓음
                fragmentTransaction.replace(R.id.fragment_container_view_main, mapFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case 1://list
                fragmentTransaction.replace(R.id.fragment_container_view_main, listFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case 2://setting
                fragmentTransaction.replace(R.id.fragment_container_view_main, settingFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

        }

    }

    //백그라운드 서비스 시작
    public void service_start() {
        startService(new Intent(this, BackgroundLocationUpdateService.class));
    }

    //백그라운드 서비스 종료
    public void service_stop() {
        stopService(new Intent(this, BackgroundLocationUpdateService.class));
    }
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//QR스캐너에서 돌아왔을 때
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        dataBaseViewModel.addFriend(result.getContents());
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* 뒤로가기 버튼 메소드*/
    public void onBackPressed() {
        //super.onBackPressed();
        //기존의 뒤로가기 버튼 기능 막기
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 버튼 한번더 누르시면 종료됩니다", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }// 뒤로가기버튼을 한번누르면 현재시간값에 현재버튼누른시간 저장
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }//위에서 저장한 현재시간값에 2초안에 버튼을 한번 더 누르면 앱을 종료함.
    }

}