package com.pankaj.complaintmanagement.util;

public enum DurationHelper {

    SECOND(1000),
    MINUTE(60 * 1000),
    HOUR(60 * 60 * 1000),
    DAY(24 * 60 * 60 * 1000),
    WEEK(7 * 24 * 60 * 60 * 1000);
    //har object ke liye alag millis variable hoga
    //enum internally  kya karta hai like for second
    /*DurationHelper SECOND = new DurationHelper(1000);
    DurationHelper MINUTE = new DurationHelper(60000);
    DurationHelper HOUR   = new DurationHelper(3600000);
...*/
    //har object ke ander khud ka millis variable hoga with value independent
    private final long millis ;
    DurationHelper(long millis){
        this.millis = millis;
    }

    //this is use to tell how many minutes, hour, day or week
    // like DurationHelper.MINUTE.of(5); indirectly ye mujhe 5 mint dega
    public long of(long value){
        return value * millis;
    }


}
