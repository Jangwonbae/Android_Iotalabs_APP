package com.iotalabs.geoar.view.create_qr_code;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

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

public class CreateQR_codeViewModel  {
    private PersonName personName;
    private ClassUUID classUUID;
    private String name;
    private String UUID_name;
    private ActivityCreateQrBinding binding;
    CreateQR_codeViewModel(ActivityCreateQrBinding binding){
        this.binding=binding;
        //이름 가져오기
        personName=new PersonName(binding.getActivity().getApplicationContext());//Context
        this.name=personName.getName();
        //UUID를 가져와서 이름과 결합
        classUUID = new ClassUUID();
        UUID_name = classUUID.getDeviceUUID(binding.getActivity().getApplicationContext())+"문자열나누기"+name;//Context
        makeQR_code();
    }
    void makeQR_code(){//QR코드 생성
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET,"utf=8");
            BitMatrix bitMatrix = multiFormatWriter.encode(UUID_name, BarcodeFormat.QR_CODE,200,200,hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            binding.qrCode.setImageBitmap(bitmap);
        }catch (Exception e){}
    }


}
