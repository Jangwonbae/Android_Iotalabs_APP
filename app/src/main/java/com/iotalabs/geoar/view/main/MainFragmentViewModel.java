package com.iotalabs.geoar.view.main;


import androidx.lifecycle.ViewModel;
import com.iotalabs.geoar.util.auth.Authenticator;


public class MainFragmentViewModel extends ViewModel {


    public MainFragmentViewModel(){

    }
    void userAuth(){
        new Authenticator().authFireBase();
    }
}
