package com.iotalabs.geoar.data;


import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Arrays;


public class Constants {
    public static final String IP_ADDRESS = "221.147.144.65:8080";

    public static final LatLng startingLoaction = new LatLng(37.2125, 126.9520);//초기 위치 협성대
    public static final int startingZoom = 16;
    public static final ArrayList<LatLng> area = new ArrayList<>(Arrays.asList(//협성대
            new LatLng(37.2104, 126.9528),
            new LatLng(37.2107, 126.9534),
            new LatLng(37.2116, 126.9534),
            new LatLng(37.2126, 126.9542),
            new LatLng(37.2140, 126.9543),
            new LatLng(37.2151, 126.9526),
            new LatLng(37.2149, 126.9517),
            new LatLng(37.2143, 126.9517),
            new LatLng(37.2132, 126.9503),
            new LatLng(37.2122, 126.9495),
            new LatLng(37.2111, 126.9504)
    ));
}
