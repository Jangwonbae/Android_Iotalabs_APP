package com.iotalabs.geoar.view.enter_name;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.iotalabs.geoar.data.PersonName;

public class EnterNameViewModel extends ViewModel {
    private PersonName personName;
    MutableLiveData<String> enterNameLiveData = new MutableLiveData<>();

    public void enterName(String name, Context context){//이름을 입력했을 때 실행

        personName = PersonName.getInstance(context);
        //liveData의 값을 전달받은 name으로 설정
        enterNameLiveData.setValue(name);
        //""이 아닐경우
        if(!enterNameLiveData.getValue().trim().equals("")){
            //이름을 preference에 저장
            personName.setName(name);
        }
    }
}
