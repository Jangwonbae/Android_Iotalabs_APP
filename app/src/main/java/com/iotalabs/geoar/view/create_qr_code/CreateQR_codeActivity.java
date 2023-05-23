package com.iotalabs.geoar.view.create_qr_code;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.ActivityCreateQrBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.UUID;

public class CreateQR_codeActivity extends AppCompatActivity {
    //{엑티비티명}Binding
    private ActivityCreateQrBinding binding;
    private CreateQR_codeViewModel createQRCodeViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //데이터 바인딩
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_qr);
        //뷰모델 생성
        createQRCodeViewModel= new ViewModelProvider(this).get(CreateQR_codeViewModel.class);
        //엑티비티에 뷰모델 연결
        binding.setViewModel(createQRCodeViewModel);//레이아웃 파일의 name = viewModel로 선언했기 때문에 setviewModel(), set{변수명}
        //뷰모델 객체 생성
        makeQR_code();//뷰는 QR코드를 만들 줄만 알면되고 포함하는 정보는 뷰모델에서 정함
    }

    private void makeQR_code(){//QR코드 생성
        createQRCodeViewModel.set_QR_text(this.getBaseContext());
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET,"utf=8");
            //QR코드의 포함되는 정보는 동적으로 변하는 데이터가 아니기 떄문에 get_QR_text()를 호출하여 사용
            BitMatrix bitMatrix = multiFormatWriter.encode(createQRCodeViewModel.get_QR_text(), BarcodeFormat.QR_CODE,200,200,hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            binding.qrCode.setImageBitmap(bitmap);
        }catch (Exception e){}
    }
}