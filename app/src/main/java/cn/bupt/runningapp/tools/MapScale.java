package cn.bupt.runningapp.tools;

/**
 * Created by apple on 2018/3/13.
 */

public class MapScale {
    public static int getScale(int length){
        if(length == 0){
            return 19;
        }else if(length < 1000){
            return 15;
        }else if(length < 2000){
            return 14;
        }else if(length < 5000){
            return 13;
        }else if(length < 10000){
            return 12;
        }else{
            return 11;
        }
    }
}
