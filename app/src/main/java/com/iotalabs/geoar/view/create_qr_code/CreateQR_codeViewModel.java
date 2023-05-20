package com.iotalabs.geoar.view.create_qr_code;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.ViewModel;

import com.example.lotalabsappui.databinding.ActivityCreateQrBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.iotalabs.geoar.data.ClassUUID;
import com.iotalabs.geoar.data.PersonName;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Hashtable;
import java.util.UUID;

public class CreateQR_codeViewModel {
    private PersonName personName;
    private ClassUUID classUUID;
    private String name;
    private String UUID;
    private String QR_text;
    private ActivityCreateQrBinding binding;
    CreateQR_codeViewModel(ActivityCreateQrBinding binding){

        //이름 가져오기
        personName=new PersonName(binding.getActivity().getApplicationContext());//Context
        this.name=personName.getName();
        //UUID 객체 생성
        classUUID = new ClassUUID();
        this.UUID=classUUID.getDeviceUUID(binding.getActivity().getApplicationContext());//Context
        //UUID를 가져와서 이름과 결합
        this.QR_text = UUID+"문자열나누기"+name;

    }
    public String get_QR_text(){
        return QR_text;
    }
}
