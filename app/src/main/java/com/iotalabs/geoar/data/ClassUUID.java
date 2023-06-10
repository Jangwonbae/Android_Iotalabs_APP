package com.iotalabs.geoar.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.nio.charset.StandardCharsets;

public class ClassUUID {
    private final static String CACHE_DEVICE_ID = "CacheDeviceID";

    public ClassUUID(Context context){
        StaticUUID.UUID=getDeviceUUID(context);
    }


    public static String getDeviceUUID(Context context)//UUID를 리턴하는 메소드
    {
        java.util.UUID deviceUUID = null;
        //sharedPreferences에 저장된 UUID를 가져옴
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
        String cachedDeviceID = sharedPreferences.getString(CACHE_DEVICE_ID, "");
        if ( cachedDeviceID != "" )//저장된 UUID가 있다면 가져옴
        {
            deviceUUID = java.util.UUID.fromString( cachedDeviceID );
        }
        else//저장된 UUID가 없다면 새로 생성함
        {
            final String androidUniqueID = Settings.Secure.getString( context.getContentResolver(), Settings.Secure.ANDROID_ID );
            if ( androidUniqueID != "" )
            {
                deviceUUID = java.util.UUID.nameUUIDFromBytes( androidUniqueID.getBytes(StandardCharsets.UTF_8) );
            }
            else
            {
                final String anotherUniqueID = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                if ( anotherUniqueID != null )
                {
                    deviceUUID = java.util.UUID.nameUUIDFromBytes( anotherUniqueID.getBytes(StandardCharsets.UTF_8) );
                }
                else
                {
                    deviceUUID = java.util.UUID.randomUUID();
                }
            }
        }
        // save cur UUID.
        sharedPreferences.edit().putString(CACHE_DEVICE_ID, deviceUUID.toString()).apply();
        return deviceUUID.toString();
    }
}