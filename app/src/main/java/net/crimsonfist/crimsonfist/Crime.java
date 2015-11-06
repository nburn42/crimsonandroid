package net.crimsonfist.crimsonfist;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by nburn42 on 11/6/15.
 */
public class Crime {

    public static List<String> dayOfWeekStrings = Arrays.asList(
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat",
            "Sun"
    );

    public LatLng latLon;
    public CrimeType type;
    public int dayOfWeek;
    public Date time;
}
