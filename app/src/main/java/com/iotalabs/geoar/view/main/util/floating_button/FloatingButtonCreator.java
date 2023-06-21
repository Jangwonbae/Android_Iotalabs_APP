package com.iotalabs.geoar.view.main.util.floating_button;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.example.lotalabsappui.R;
import com.example.lotalabsappui.databinding.ActivityMainBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.iotalabs.geoar.view.create_qr_code.CreateQR_codeActivity;


public class FloatingButtonCreator {
    private Animation ft_btn_open, ft_btn_close;
    private Boolean isFabOpen = false;
    private IntentIntegrator qrScan;

    private ActivityMainBinding binding;

    public FloatingButtonCreator(Activity mainActivity,ActivityMainBinding binding){

        this.binding=binding;
        //애니메시션 객체생성
        ft_btn_open = AnimationUtils.loadAnimation(mainActivity.getBaseContext(), R.anim.anim_ft_btn_open);
        ft_btn_close = AnimationUtils.loadAnimation(mainActivity.getBaseContext(), R.anim.anim_ft_btn_close);

        //플로팅 버튼 클릭메소드
        binding.ftBtnMain.setOnClickListener(new View.OnClickListener() {//열렸다 닫혔다 이벤트
            @Override
            public void onClick(View v) {
                anim();
            }
        });
        binding.ftBtnCreateQR.setOnClickListener(new View.OnClickListener() {//QR생성 클릭시 QR코드 엑티비티로 이동
            @Override
            public void onClick(View v) {
                anim();
                Intent intent = new Intent(mainActivity, CreateQR_codeActivity.class);
                mainActivity.startActivity(intent);
            }
        });
        binding.ftBtnReadQR.setOnClickListener(new View.OnClickListener() {//QR리더기 클릭시 카메라 엑티비티로 이동
            @Override
            public void onClick(View v) {
                anim();
                qrScan = new IntentIntegrator(mainActivity);
                qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
                qrScan.initiateScan();

            }
        });
    }

    public void anim() {
        if (isFabOpen) {
            binding.ftBtnCreateQR.startAnimation(ft_btn_close);
            binding.ftBtnReadQR.startAnimation(ft_btn_close);
            binding.ftBtnCreateQR.setClickable(false);
            binding.ftBtnReadQR.setClickable(false);
            isFabOpen = false;
        } else {
            binding.ftBtnCreateQR.startAnimation(ft_btn_open);
            binding.ftBtnReadQR.startAnimation(ft_btn_open);
            binding.ftBtnCreateQR.setClickable(true);
            binding.ftBtnReadQR.setClickable(true);
            isFabOpen = true;
        }
    }
}
