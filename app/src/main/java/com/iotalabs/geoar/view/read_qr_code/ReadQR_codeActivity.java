package com.iotalabs.geoar.view.read_qr_code;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lotalabsappui.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.iotalabs.geoar.data.ClassUUID;
import com.iotalabs.geoar.data.Constants;
import com.iotalabs.geoar.util.db.DbOpenHelper;
import com.iotalabs.geoar.util.network.GetFriendData;
import com.iotalabs.geoar.util.network.InsertFriendData;
import com.iotalabs.geoar.view.create_qr_code.CreateQR_codeActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadQR_codeActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    private DatabaseReference mDatabase;
    private String UUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_qractivity);
        UUID= ClassUUID.getDeviceUUID(getBaseContext());
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan.initiateScan();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//QR스캐너에서 돌아왔을 때
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String checkUUID="[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}";
        String uuidFriend;
        String nameFriend;
        if(result != null) {
            if(result.getContents() != null) {
                //성공
                Pattern patten = Pattern.compile(checkUUID);
                Matcher matcher = patten.matcher(result.getContents());
                boolean regex= matcher.find();
                if(regex){//UUID형식인지 체크
                    uuidFriend = result.getContents().split("문자열나누기")[0];
                    nameFriend = result.getContents().split("문자열나누기")[1];
                    //이미 등록된 친구인지 확인
                    mDatabase.child("USER").child(UUID).child("follow").child(uuidFriend).setValue(nameFriend);

                }
                else{
                    Toast.makeText(this, "친구추가 QR코드가 아닙니다." , Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        finish();
    }
}