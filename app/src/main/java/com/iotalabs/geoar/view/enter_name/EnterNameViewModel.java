package com.iotalabs.geoar.view.enter_name;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.iotalabs.geoar.data.PersonName;
import com.iotalabs.geoar.data.SingleLiveEvent;

public class EnterNameViewModel extends ViewModel {
    private PersonName personName;
    //SingleLiveEvent 사용해서 Event가 한번만 발동하도록
    SingleLiveEvent<String> enterNameLiveData = new SingleLiveEvent<>();
    //AAC ViewModel은 Activity의 모든 생명주기에 함께하기 때문에 LiveData에 있는 값이 Actvity가 재생성 될때 LiveData의 상태가 Active로 바뀌면서 한번 더 중복 호출됨
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

