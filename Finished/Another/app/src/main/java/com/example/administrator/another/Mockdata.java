package com.example.administrator.another;

/**
 * Created by Administrator on 2017-03-15.
 */
import java.util.Random;

public class Mockdata {
    //x is the day number 0, 1, 2, 3...
    public static Point getDataFromReceiver(int x){
        return new Point(x, generateRandomData());
    }

    private static int generateRandomData(){
        Random random = new Random();
        return random.nextInt(40);
    }
}
