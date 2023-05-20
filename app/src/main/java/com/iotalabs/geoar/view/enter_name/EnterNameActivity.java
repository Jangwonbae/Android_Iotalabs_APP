package com.iotalabs.geoar.view.enter_name;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.ActivityEnterNameBinding;
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
        binding.setActivity(this);//레이아웃 파일의 name = activity로 선언했기 때문에 setActivity(), set{변수명}
        enterNameViewModel = new EnterNameViewModel(binding);

        //mvvm 패턴을 적용했기 때문에 뷰는 입력만 받고 데이터의 저장은 뷰모델이 처리
       binding.btnStart.setOnClickListener(new View.OnClickListener() {//시작하기 버튼 클릭 리스너
            @Override
            public void onClick(View v) {
                //뷰모델에서 이름 pref에 저장
                enterNameViewModel.enterName(binding.editTextName.getText().toString());
            }
        });
    }
}