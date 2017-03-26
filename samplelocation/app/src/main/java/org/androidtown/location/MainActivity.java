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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.achartengine.GraphicalView;




import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {

    private static GraphicalView view, viewDifference;
    private LinearLayout view1, view2;
    private LineGraph line = new LineGraph();
    private LineGraph2 line2 = new LineGraph2();
    private static Thread thread;

    TextView textViewAddress, textViewArduino, textViewWeather;
    Geocoder mGeocoder;
    List<Address> mListAddress;
    Address mAddress;
    Handler h;
    boolean flag = false;
    boolean flag2 = false;

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


    OpenWeatherTask openWeatherTask;
    Rain rain = new Rain();
    public Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view1 = (LinearLayout) findViewById(R.id.lineGraphView);
        view2 = (LinearLayout) findViewById(R.id.lineGraphView2);

        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        mGeocoder = new Geocoder(MainActivity.this);
        textViewArduino = (TextView) findViewById(R.id.textViewArduino);
        textViewWeather = (TextView) findViewById(R.id.textViewWeather);

        openWeatherTask = new OpenWeatherTask();
/*
        startLocationService();
*/
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
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
                            flag = !flag;

                            textViewArduino.setText("Poorquality : " + str[0] + ", Attention : " + str[1] + ", Meditation : " + str[2]+"  AVGpoor : " + avgPoor + ", AVGatt: " + avgAtt + ", AVGmedi : " + avgMedi);
                            if(poorquality==0){
                                if (avgFlag < avgFlagNum) {//60
                                    sumPoor += poorquality;
                                    sumAtt += attention;
                                    sumMedi += meditation;
                                    avgFlag++;
                                    textViewArduino.setText("sumPoor : " + sumPoor + ", SumATT : " + sumAtt + ", SumMedi : " + sumMedi);
                                } else {
                                    if (avgFlag2 < 1) {
                                        avgPoor = sumPoor / avgFlagNum;
                                        avgAtt = sumAtt / avgFlagNum;
                                        avgMedi = sumMedi / avgFlagNum;
                                        textViewArduino.setText("AVGpoor : " + avgPoor + ", AVGatt: " + avgAtt + ", AVGmedi : " + avgMedi);
                                        avgFlag2++;
                                    }
                                }
                            }
                        }
                        break;
                }
            };
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        thread = new Thread(){
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
                    Point p = new Point(i, attention - meditation);
                    line2.addNewPoints(p);
                    view.repaint();
                    viewDifference.repaint();
                }
            }
        };
        thread.start();
    }



    //그래프 스타트
   protected void onStart(){
        super.onStart();
        view = line.getView(this);
       view1.addView(view);
       viewDifference = line2.getView(this);
       view2.addView(viewDifference);
    }

    private void startLocationService() {
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 위치 정보를 받을 리스너 생성
        GPSListener gpsListener = new GPSListener();
        long minTime = 100000;
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
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();
                SearchAddress(latitude, longitude);
                SearchWeather(latitude, longitude);
            }
        } catch(SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private class GPSListener implements LocationListener {

        //위치 정보가 확인될 때 자동 호출되는 메소드
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude;
            Log.i("GPSListener", msg);

            SearchAddress(latitude, longitude);
            SearchWeather(latitude, longitude);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
                textViewAddress.setText(mAddress.getAddressLine(0));
            }
            else {
                textViewAddress.setText("주소 에러");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SearchWeather(double latitude, double longitude) {
        try {
            int weatherId = openWeatherTask.execute(Double.toString(latitude), Double.toString(longitude)).get();
            boolean isRainy = rain.SearchIsRainy(weatherId);
            textViewWeather.setText(String.valueOf(isRainy));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

//bt 모르는 부분 시작
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
    //bt모르는부분 끝

}
