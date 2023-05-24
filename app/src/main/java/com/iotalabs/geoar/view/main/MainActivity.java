package com.iotalabs.geoar.view.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.iotalabs.geoar.util.location.BackgroundLocationUpdateService;
import com.iotalabs.geoar.view.create_qr_code.CreateQR_codeActivity;
import com.iotalabs.geoar.view.read_qr_code.ReadQR_codeActivity;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MapFragment mapFragment;
    private ListFragment listFragment;
    private SettingFragment settingFragment;
    private Animation ft_btn_open, ft_btn_close;
    private Boolean isFabOpen = false;
    private long backKeyPressedTime = 0; //뒤로가기 버튼 눌렀던 시간 저장
    private Toast toast;//첫번째 뒤로가기 버튼을 누를때 표시하는 변수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //데이터 바인딩
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);
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
        binding.ftBtnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
            }
        });
        binding.ftBtnCreateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                Intent intent = new Intent(MainActivity.this, CreateQR_codeActivity.class);
                startActivity(intent);
            }
        });
        binding.ftBtnReadQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
                Intent intent2 = new Intent(MainActivity.this, ReadQR_codeActivity.class);
                startActivity(intent2);
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
            case 0:
                //replace 위에 쌓여진 Fragement들을 버려버리고 새로운 Fragment를 쌓음
                fragmentTransaction.replace(R.id.fragment_container_view_main, mapFragment);
                fragmentTransaction.commit();
                break;

            case 1:
                fragmentTransaction.replace(R.id.fragment_container_view_main, listFragment);
                fragmentTransaction.commit();
                break;

            case 2:
                fragmentTransaction.replace(R.id.fragment_container_view_main, settingFragment);
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