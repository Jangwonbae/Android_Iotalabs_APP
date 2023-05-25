package com.iotalabs.geoar.view.enter_name;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.ActivityEnterNameBinding;
import com.iotalabs.geoar.data.PersonName;
import com.iotalabs.geoar.view.main.MainActivity;

public class EnterNameActivity extends AppCompatActivity {
    //{엑티비티명}Binding
    private ActivityEnterNameBinding binding;
    private EnterNameViewModel enterNameViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //데이터 바인딩
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_name);
        //뷰모델 생성
        enterNameViewModel = new ViewModelProvider(this).get(EnterNameViewModel.class);
        //엑티비티에 뷰모델 연결
        binding.setViewModel(enterNameViewModel);//레이아웃 파일의 name = viewModel로 선언했기 때문에 setviewModel(), set{변수명}

        //mvvm 패턴을 적용했기 때문에 뷰는 입력만 받고 데이터의 저장은 뷰모델이 처리
        binding.btnStart.setOnClickListener(new View.OnClickListener() {//시작하기 버튼 클릭 리스너
            @Override
            public void onClick(View v) {
                //뷰모델에서 이름 pref에 저장
                enterNameViewModel.enterName(binding.editTextName.getText().toString(), getApplicationContext());
            }
        });
        //liveData의 값을 관찰하다가 값이 바뀌면 실행
        enterNameViewModel.enterNameLiveData.observeInOnStart(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //메인 엑티비티 이동
                    Intent intent = new Intent(EnterNameActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        // bundle에 저장되어 있는 데이터 가져오기
        if (savedInstanceState != null) {
            //작은 데이터이기 때문에 savedInstanceState를 사용
            binding.editTextName.setText(savedInstanceState.getString("edit_text_name"));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    // onPause 직전에 호출되는 부분, Bundle에 상태를 저장 할 수 있다.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("edit_text_name", binding.editTextName.toString());
    }
}
