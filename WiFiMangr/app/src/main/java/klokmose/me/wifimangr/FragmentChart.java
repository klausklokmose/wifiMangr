package klokmose.me.wifimangr;

import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * Created by KlausKlokmose on 29/02/16.
 */
public class FragmentChart extends Fragment {
    /*

    www.android-graphview.org

    This fragment will show different charts.
     1. Daily average hourly use of every saved hotspot (if used within the
     last two weeks (default) or within user defined range)
     2. Weekly average
     3. Monthly average

    */

    private void average(Date start, Date end){
        int numberOfDays = 0;
        int numberOfUniqueHotspots = 0;
        //get data set in range (in a "smart" way)
        //make a for loop count, for each saved hotspot, how many hours it
        // has been used in each of the days.
        //calculate the average for each hotspot
        //return the results
    }
}
