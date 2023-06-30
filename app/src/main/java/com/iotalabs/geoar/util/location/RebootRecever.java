package com.iotalabs.geoar.util.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class RebootRecever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//Android가 Oreo 이후 버전이면 (SDK_INT >= O)
            //Android에서 제공하는 죽지않는(Foreground) 서비스인 RestartService를 startForegroundService로 실행한다.
            Intent reStartIn = new Intent(context, ReStartLocationService.class);
            context.startForegroundService(reStartIn);
        } else {
            Intent startIn = new Intent(context, LocationService.class);
            context.startService(startIn);
        }
    }
}