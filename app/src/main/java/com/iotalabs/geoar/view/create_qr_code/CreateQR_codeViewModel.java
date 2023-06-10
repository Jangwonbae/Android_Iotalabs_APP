package com.iotalabs.geoar.view.create_qr_code;

import android.app.Activity;
import android.content.Context;
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
import com.iotalabs.geoar.data.StaticUUID;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Hashtable;
import java.util.UUID;

public class CreateQR_codeViewModel extends ViewModel{
    private PersonName personName;
    private String name;
    private String UUID;
    private String QR_text;

    public void set_QR_text(Context context){
        personName=PersonName.getInstance(context);
        this.name=personName.getName();
        this.UUID= StaticUUID.UUID;
        this.QR_text = UUID+"문자열나누기"+name;
    }

    public String get_QR_text(){
        return QR_text;
    }
}
