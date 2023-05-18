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
        binding.setActivity(this);//레이아웃 파일의 name = activity로 선언했기 때문에 setActivity(), set{변수명}
        //뷰모델 객체 생성
        CreateQR_codeViewModel createQR_codeViewModel = new CreateQR_codeViewModel(binding);

    }
}