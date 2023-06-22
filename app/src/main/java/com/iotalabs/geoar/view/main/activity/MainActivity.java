package com.iotalabs.geoar.view.main.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.iotalabs.geoar.util.location.BackgroundLocationUpdateService;
import com.iotalabs.geoar.view.main.activity.fragment.ListFragment;
import com.iotalabs.geoar.view.main.activity.fragment.MapFragment;
import com.iotalabs.geoar.view.main.activity.fragment.SettingFragment;
import com.iotalabs.geoar.view.main.util.floating_button.FloatingButtonCreator;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private DataBaseViewModel dataBaseViewModel;

    private FragmentManager fragmentManager;
    //아이템 선택 부분에서 null 인지 체크하여 최초 생성 시에만 초기화해주기 위해서 각각의 프래그먼트를 null로 선언해준다.
    private MapFragment mapFragment = null;
    private ListFragment listFragment = null;
    private SettingFragment settingFragment = null;

    private long backKeyPressedTime = 0; //뒤로가기 버튼 눌렀던 시간 저장
    private Toast toast;//첫번째 뒤로가기 버튼을 누를때 표시하는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //데이터 바인딩
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        dataBaseViewModel = new ViewModelProvider(this).get(DataBaseViewModel.class);
        binding.setViewModel(dataBaseViewModel);
        new FloatingButtonCreator(this,binding);//플로팅버튼 초기화

        binding.frameLayoutMainWhole.bringToFront();//버튼이 있는 fragment가 제일 앞으로 오도록 설정

        initBottomNavigation(); // 첫 프래그먼트 화면 지정

        //백그라운드 위치서비스
        startService(new Intent(this, BackgroundLocationUpdateService.class));

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    // 프레그먼트 교체
    public void initBottomNavigation() {

        //최초로 보이는 프래그먼트
        mapFragment = new MapFragment();
        fragmentManager = getSupportFragmentManager();
        //setReorderingAllowed(true)는 transaction과 관련된 프래그먼트의 상태 변경을 최적화하여 애니메이션과 전환이 올바르게 작동하도록 함
        fragmentManager.beginTransaction().replace(R.id.fragment_container_view_main, mapFragment).commit();

        //네비게이션바 클릭리스너
        binding.navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {//최초 선택 시 fragment add, 선택된 프래그먼트 show, 나머지 프래그먼트 hide
                switch (menuItem.getItemId()) {
                    //프래그먼트 화면 전환시 상태유지
                    case R.id.action_map://MapFragment
                        if(mapFragment == null){
                            mapFragment = new MapFragment();
                            fragmentManager.beginTransaction().setReorderingAllowed(true).add(R.id.fragment_container_view_main, mapFragment).commit();
                        }
                        if(mapFragment != null) fragmentManager.beginTransaction().setReorderingAllowed(true).show(mapFragment).commit();
                        if(listFragment != null) fragmentManager.beginTransaction().setReorderingAllowed(true).hide(listFragment).commit();
                        if(settingFragment != null) fragmentManager.beginTransaction().setReorderingAllowed(true).hide(settingFragment).commit();
                        break;

                    case R.id.action_list://ListFragment
                        if(listFragment == null){
                            listFragment = new ListFragment();
                            fragmentManager.beginTransaction().setReorderingAllowed(true).add(R.id.fragment_container_view_main,listFragment).commit();
                        }
                        if(mapFragment != null) fragmentManager.beginTransaction().setReorderingAllowed(true).hide(mapFragment).commit();
                        if(listFragment != null) fragmentManager.beginTransaction().setReorderingAllowed(true).show(listFragment).commit();
                        if(settingFragment != null) fragmentManager.beginTransaction().setReorderingAllowed(true).hide(settingFragment).commit();
                        break;

                    case R.id.action_set://SettingFragment
                        if(settingFragment == null){
                            settingFragment = new SettingFragment();
                            fragmentManager.beginTransaction().setReorderingAllowed(true).add(R.id.fragment_container_view_main,settingFragment).commit();
                        }
                        if(mapFragment != null) fragmentManager.beginTransaction().setReorderingAllowed(true).hide(mapFragment).commit();
                        if(listFragment != null) fragmentManager.beginTransaction().setReorderingAllowed(true).hide(listFragment).commit();
                        if(settingFragment != null) fragmentManager.beginTransaction().setReorderingAllowed(true).show(settingFragment).commit();
                        break;
                }
                return true;
            }
        });

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