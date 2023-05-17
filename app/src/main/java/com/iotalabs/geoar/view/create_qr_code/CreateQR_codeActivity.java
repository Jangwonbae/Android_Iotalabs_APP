package com.iotalabs.geoar.view.create_qr_code;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.example.lotalabsappui.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.UUID;

public class CreateQR_codeActivity extends AppCompatActivity {
    private ImageView iv;
    private String text;
    private String name;
    private final static String CACHE_DEVICE_ID = "CacheDeviceID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);
        iv = (ImageView)findViewById(R.id.qrcode);
        SharedPreferences prefs = getSharedPreferences("person_name",0);
        name = prefs.getString("name","");
        text = GetDeviceUUID(CreateQR_codeActivity.this)+"문자열나누기"+name;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET,"utf=8");

            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200,hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            iv.setImageBitmap(bitmap);
        }catch (Exception e){}
    }

    public static String GetDeviceUUID(Context context)
    {   //QR만들기
        UUID deviceUUID = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
        String cachedDeviceID = sharedPreferences.getString(CACHE_DEVICE_ID, "");
        if ( cachedDeviceID != "" )
        {
            deviceUUID = UUID.fromString( cachedDeviceID );
        }
        else
        {
            final String androidUniqueID = Settings.Secure.getString( context.getContentResolver(), Settings.Secure.ANDROID_ID );
            if ( androidUniqueID != "" )
            {
                deviceUUID = UUID.nameUUIDFromBytes( androidUniqueID.getBytes(StandardCharsets.UTF_8) );
            }
            else
            {
                final String anotherUniqueID = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                if ( anotherUniqueID != null )
                {
                    deviceUUID = UUID.nameUUIDFromBytes( anotherUniqueID.getBytes(StandardCharsets.UTF_8) );
                }
                else
                {
                    deviceUUID = UUID.randomUUID();
                }
            }
        }
        // save cur UUID.
        sharedPreferences.edit().putString(CACHE_DEVICE_ID, deviceUUID.toString()).apply();
        return deviceUUID.toString();
    }
}