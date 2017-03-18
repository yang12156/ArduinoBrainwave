package com.example.administrator.another;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.achartengine.GraphicalView;


public class MainActivity extends AppCompatActivity {
    private GraphicalView view;
    private LinearLayout view1;
    private LineGraph line = new LineGraph(this);
    private static Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view1 = (LinearLayout) findViewById(R.id.lineGraphView);

        thread = new Thread(){
            public void run(){
                for(int i = 0; i < 200 ; i++){
                    try {
                        line.mRenderer.setXAxisMin(i-20);
                        line.mRenderer.setXAxisMax(i+1);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Point p = new Point(i, i*5); // We got new data
                    Point p2 = Mockdata.getDataFromReceiver(i); // We got new data
                    line.addNewPoints(p, p2);//Add it to our graph
                    view.repaint();

                }
            }
        };
        thread.start();
    }

    protected void onStart(){
        super.onStart();
        view = line.getView(this);
        view1.addView(view);
    }

}
