package com.iotalabs.geoar.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Clock {
    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat;
   public Clock(){
       mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
   }

    public String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}
