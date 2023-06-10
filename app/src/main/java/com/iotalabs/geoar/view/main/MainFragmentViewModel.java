package com.iotalabs.geoar.view.main;


import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.iotalabs.geoar.data.User;
import com.iotalabs.geoar.util.auth.Authenticator;
import com.rugovit.eventlivedata.MutableEventLiveData;

import java.util.ArrayList;
import java.util.List;


public class MainFragmentViewModel extends ViewModel {
    public MainFragmentViewModel(){//3번실행 메인, 맵, 리스트

    }
    void userAuth(){
        new Authenticator().authFireBase();
    }
}
