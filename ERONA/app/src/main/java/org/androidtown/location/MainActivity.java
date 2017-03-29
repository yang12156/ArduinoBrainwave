package org.androidtown.location;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.achartengine.GraphicalView;




import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {


    //Calendar now = Calendar.getInstance();

    private static GraphicalView view, viewDifference;
    private LinearLayout view1, view2;
    private LineGraph line = new LineGraph();
    private LineGraph2 line2 = new LineGraph2();
    private static Thread threadGraph;

    TextView textViewAddress, textViewWeather,textViewUserState;
    ImageView imageViewFace;
    Geocoder mGeocoder;
    List<Address> mListAddress;
    Address mAddress;
    Handler h;
    private boolean flag = false;
    private boolean flag2 = false;
    private boolean flagSleepDialog = true;
    private boolean flagSleepDialog2 = false;
    private boolean flagWeather = false;

    private static final String TAG = "bluetooth2";
    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "20:16:05:06:03:71";
    private int poorquality, attention, meditation;
    private int avgFlag=0;
    private int sumPoor=0,sumAtt=0,sumMedi=0;
    private int avgFlagNum=5; //초기값 받아오는 횟수 
    private int avgFlag2=0;
    private int avgPoor,avgAtt,avgMedi;
    private int nowHour=0;
    private int limitFlag=0;

    private int originalAvgGapBetweenAttMedi=0;
    private int absAvgGapBetweenAttMedi=0;
    private int realtimeGapBetweenAttMdei=0;


    private double level1Limit=0;//집중과 정상 사이
    private double level2Limit=0;
    private double level3Limit=0;
    private int sleepLevel=0;//1집중 2정상 3졸림 4 자는중

    private int level1Count=0;
    private int level2Count=0;
    private int level3Count=0;
    private int level4Count=0;


    boolean isRainy=false;  //+1
    int rainFlag=0;
    boolean isNight=false;//+2
    int NightFlag=0;
    boolean isHighway=false;//+1
    int HighwayFlag=0;

    private String userState ="";

    OpenWeatherTask openWeatherTask;
    Rain rain = new Rain();

    RoadManager roadManager;
    private String roadNum="", restArea="";
    private String latitudeString, longitudeString;
    private Double latitude, longitude;
    private String GPSaddress = "";

    SoundPool soundPool;
    int tak;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view1 = (LinearLayout) findViewById(R.id.lineGraphView);
        view2 = (LinearLayout) findViewById(R.id.lineGraphView2);

        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        mGeocoder = new Geocoder(MainActivity.this);
        textViewWeather = (TextView) findViewById(R.id.textViewWeather);
        textViewUserState = (TextView)findViewById(R.id.textViewUserState);
        imageViewFace = (ImageView)findViewById(R.id.imageFace);

        imageViewFace.setImageResource(R.drawable.face1);

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        tak=soundPool.load(this,R.raw.firealarm,1);

        openWeatherTask = new OpenWeatherTask();

        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("알림");
        dialog.setMessage("알림 다이얼로그");
        dialog.setNegativeButton("닫기", null);



       startLocationService();

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:

                       /* nowHour = now.get(Calendar.HOUR);// 시간 갱신
                            if(nowHour>22||nowHour<6){//야간 주행인경우
                                isNight = true;
                                if(NightFlag==0) {
                                    decideSleepLevelForNight();
                                    NightFlag++; //더이상 night덧셈을 못하게 막음
                                }

                            }
                            else{
                                isNight=false;
                                NightFlag=0;// 낮으로 판단되면 다음 밤을 위해서 플래그를 내림.
                            }*/

                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n");
                        if (endOfLineIndex > 0) {
                            String sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());
                            String str[] = sbprint.split("-");
                            poorquality = Integer.parseInt(str[0]);
                            attention = Integer.parseInt(str[1]);
                            meditation = Integer.parseInt(str[2]);
                            realtimeGapBetweenAttMdei=attention-meditation;
                            flag = !flag;

                            if(poorquality==0){//초기 뇌파 설정 start
                                if (avgFlag < avgFlagNum) {//60
                                    sumPoor += poorquality;
                                    sumAtt += attention;
                                    sumMedi += meditation;
                                    avgFlag++;
                                    //textViewArduino.setText("sumPoor : " + sumPoor + ", SumATT : " + sumAtt + ", SumMedi : " + sumMedi);
                                } else {
                                    if (avgFlag2 < 1) {
                                        avgPoor = sumPoor / avgFlagNum;
                                        avgAtt = sumAtt / avgFlagNum;
                                        avgMedi = sumMedi / avgFlagNum;
                                        //textViewArduino.setText("AVGpoor : " + avgPoor + ", AVGatt: " + avgAtt + ", AVGmedi : " + avgMedi);
                                        originalAvgGapBetweenAttMedi=avgAtt-avgMedi;
                                        absAvgGapBetweenAttMedi=Math.abs(originalAvgGapBetweenAttMedi);//절대값



                                        avgFlag2++; //초기뇌파 설정 끝
                                    }
                                    else{
                                        calculateLevelLimit();
                                        warning();
                                        textViewUserState.setText(userState);
                                        if (userState.equals("집중")) {
                                            flagSleepDialog = true;
                                            imageViewFace.setImageResource(R.drawable.face1);
                                        }
                                        else if (userState.equals("정상")) {
                                            flagSleepDialog = true;
                                            imageViewFace.setImageResource(R.drawable.face2);
                                        }
                                        else if (userState.equals("졸림")) {
                                            flagSleepDialog = true;
                                            imageViewFace.setImageResource(R.drawable.face3);
                                        }
                                        else if (userState.equals("자는중")) {
                                            if(flagSleepDialog2) {
                                                dialog.setMessage("가까운 휴게소 : " + restArea);
                                                dialog.show();
                                                soundPool.play(tak,1,1,0,1,1);
                                                flagSleepDialog2 = false;
                                            }
                                            imageViewFace.setImageResource(R.drawable.face4);
                                        }
                                    }
                                }
                            }//초기 뇌파 설정 end


                            textViewAddress.setText(GPSaddress);
                            textViewUserState.setText(userState);
                            if(isRainy)
                                textViewWeather.setText("강수 : X");
                            else
                                textViewWeather.setText("강수 : O");
                        }
                        break;
                }
            };
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        threadGraph = new Thread(){
            public void run(){
                    for(int i = 0; ; i++){
                        while(flag == flag2);
                        flag2 = flag;

                        line.mRenderer.setXAxisMin(i-20);
                        line.mRenderer.setXAxisMax(i+1);
                        line.mRenderer.setYAxisMax(100);
                        line.mRenderer.setYAxisMin(0);

                        line2.mRenderer.setXAxisMin(i-20);
                        line2.mRenderer.setXAxisMax(i+1);
                        line2.mRenderer.setYAxisMax(100);
                        line2.mRenderer.setYAxisMin(-100);
                        Point p1 = new Point(i, attention); // We got new data
                        Point p2 = new Point(i, meditation); // We got new data
                        line.addNewPoints(p1, p2);//Add it to our graph
                        Point p = new Point(i, realtimeGapBetweenAttMdei);
                        line2.addNewPoints(p);
                        view.repaint();
                        viewDifference.repaint();
                        if (userState.equals("자는중")) {
                            if (flagSleepDialog) {
                                roadManager = new RoadManager("127.1039", "37.2552");
                                roadNum = roadManager.searchRoadNum();
                                //roadNum = "0010";
                                restArea = roadManager.searchRestArea(roadNum);
                                flagSleepDialog = false;
                                flagSleepDialog2 = true;
                            }
                        }

                        if(flagWeather) {
                            latitude = 37.2552;
                            longitude = 127.1039;
                            //SearchAddress(37.2552, 127.1039);
                            SearchAddress(latitude, longitude);
                            //SearchWeather(latitude, longitude);
                            flagWeather = false;
                        }

                }
            }
        };
        threadGraph.start();
    }

    //그래프 스타트
    protected void onStart(){
        super.onStart();
        view = line.getView(this);
        view1.addView(view);
        viewDifference = line2.getView(this);
        view2.addView(viewDifference);
    }


    //졸음알림 알고리즘 시작
    public void warning(){
        if(level1Limit<realtimeGapBetweenAttMdei){
            level1Count++;
            if(level1Count>3) {
                userState = "집중";
                level1Count=0;
                level2Count=0;
                level3Count=0;
                level4Count=0;
            }
        }
        else if(level2Limit<realtimeGapBetweenAttMdei){
            level2Count++;
            if(level2Count>3) {
                userState = "정상";
                level1Count=0;
                level2Count=0;
                level3Count=0;
                level4Count=0;
            }
        }
        else if(level3Limit<realtimeGapBetweenAttMdei){
            level3Count++;
            if(level3Count>3) {
                userState = "약간졸림";
                level1Count=0;
                level2Count=0;
                level3Count=0;
                level4Count=0;
            }
        }
        else{
            level4Count++;
            if(level4Count>3) {
                userState = "자는중";
                level1Count=0;
                level2Count=0;
                level3Count=0;
                level4Count=0;
            }
       }


    }

    public void calculateLevelLimit(){
        switch (sleepLevel){
            case 1:
                level1Limit=originalAvgGapBetweenAttMedi + absAvgGapBetweenAttMedi*0.8;//집중과 정상 사이
                level2Limit=originalAvgGapBetweenAttMedi -absAvgGapBetweenAttMedi*1.0;
                level3Limit=originalAvgGapBetweenAttMedi-absAvgGapBetweenAttMedi*1.5;

                break;
            case 2:
                level1Limit=originalAvgGapBetweenAttMedi + absAvgGapBetweenAttMedi*0.8;//집중과 정상 사이
                level2Limit=originalAvgGapBetweenAttMedi -absAvgGapBetweenAttMedi*0.8;
                level3Limit=originalAvgGapBetweenAttMedi-absAvgGapBetweenAttMedi*1.3;
                break;
            case 3:
                level1Limit=originalAvgGapBetweenAttMedi + absAvgGapBetweenAttMedi*0.8;//집중과 정상 사이
                level2Limit=originalAvgGapBetweenAttMedi -absAvgGapBetweenAttMedi*0.5;
                level3Limit=originalAvgGapBetweenAttMedi-absAvgGapBetweenAttMedi*1.0;
                break;
            case 4:
                level1Limit=originalAvgGapBetweenAttMedi + absAvgGapBetweenAttMedi*0.8;//집중과 정상 사이
                level2Limit=originalAvgGapBetweenAttMedi -absAvgGapBetweenAttMedi*0.3;
                level3Limit=originalAvgGapBetweenAttMedi-absAvgGapBetweenAttMedi*0.8;
                break;
            default:
                break;
        }
    }

    public void decideSleepLevelForRain(){
        if(isRainy){
            sleepLevel++;
        }
    }

    public void decideSleepLevelForNight(){
        if(isNight){
            sleepLevel=sleepLevel+2;

        }
    }

    public void decideSleepLevelForHighway(){
        if(isHighway){
            sleepLevel++;
        }

    }
    //졸음알림 알고리즘 끝


    // 위치 서비스 시작
    private void startLocationService() {
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 위치 정보를 받을 리스너 생성
        GPSListener gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;

        try {
            // GPS를 이용한 위치 요청
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            // 네트워크를 이용한 위치 요청
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
                latitudeString = latitude.toString();
                longitudeString = longitude.toString();
                //SearchAddress(latitude, longitude);
                //SearchWeather(latitude, longitude);
                flagWeather = true;
            }
        } catch(SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private class GPSListener implements LocationListener {

        //위치 정보가 확인될 때 자동 호출되는 메소드
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            latitudeString = latitude.toString();
            longitudeString = longitude.toString();

            String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude;
            Log.i("GPSListener", msg);

            //SearchAddress(latitude, longitude);
            //SearchWeather(latitude, longitude);
            flagWeather = true;
        }

        public void onProviderDisabled(String provider) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public void SearchAddress(double latitude, double longitude) {
        try {
            mListAddress = mGeocoder.getFromLocation(latitude, longitude, 1);
            if (mListAddress.size() > 0) {
                mAddress = mListAddress.get(0);
                GPSaddress = mAddress.getAddressLine(0);
            }
            else {
                GPSaddress = "주소 에러";
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SearchWeather(double latitude, double longitude) {
        try {
            int weatherId = openWeatherTask.execute(Double.toString(latitude), Double.toString(longitude)).get();
            isRainy = rain.SearchIsRainy(weatherId);
            if(rainFlag==0&&isRainy){
                rainFlag++;
                decideSleepLevelForRain();
            }
            else if(!isRainy){
                rainFlag=0;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    //위치 서비스 종료


    //블루투스 시작
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;

            // Get the input streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();

            } catch (IOException e) { }

            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
    //블루투스 끝
}

