package com.iotalabs.geoar.view.enter_name;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.lotalabsappui.databinding.ActivityEnterNameBinding;
import com.iotalabs.geoar.data.PersonName;
import com.iotalabs.geoar.view.main.MainActivity;

public class EnterNameViewModel {
    private ActivityEnterNameBinding binding;
    private PersonName personName;

    EnterNameViewModel(ActivityEnterNameBinding binding){
        this.binding=binding;
    }
    public void enterName(String name){
        if(name.trim().equals("")){
            Toast.makeText(binding.getActivity().getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else{
            personName = new PersonName(binding.getActivity().getApplicationContext());
            //이름을 prefs에 저장
            personName.setName(name);
            //메인 엑티비티 이동
            Intent intent = new Intent(binding.getActivity().getApplicationContext(), MainActivity.class);
            binding.getActivity().startActivity(intent);
            binding.getActivity().finish();
        }
    }
}
