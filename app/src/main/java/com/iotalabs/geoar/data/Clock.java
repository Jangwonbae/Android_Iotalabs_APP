package com.iotalabs.geoar.data;

import com.unity3d.player.l;

import java.text.ParseException;
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
    public long diffTime(String time) throws ParseException {
       Date t1 = mFormat.parse(getTime());
       Date t2 = mFormat.parse(time);
       //Date t1 = mFormat.parse(time);
       //Date t2 = mFormat.parse(getTime());
       long t1_mil = t1.getTime();
       long t2_mil = t2.getTime();

       long diffMin = (t1_mil-t2_mil)/(1000 * 60);

       return diffMin;
    }
}
