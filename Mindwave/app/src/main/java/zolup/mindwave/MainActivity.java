package zolup.mindwave;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    double GPSLatitude, GPSLongtitude;
    TextView textViewAddress;
    Geocoder mGeocoder;
    List<Address> mListAddress;
    Address mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewAddress = (TextView) findViewById(R.id.textAddress);
        mGeocoder = new Geocoder(MainActivity.this);
        textViewAddress.setText("hello");

        startLocationService(); //gps로 GPSLatitude, GPSLongtitude값 최신으로 유지
    }


    private void startLocationService() {

        // 위치 관리자 객체 참조
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 위치 정보를 받을 리스너 생성
        GPSListener gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;

        try {
            // GPS를 이용한 위치 요청
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);

            // 네트워크를 이용한 위치 요청
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);

            // 위치 확인이 안되는 경우에도 최근에 확인된 위치 정보 먼저 확인
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

                textViewAddress.setText("내 위치 : " + latitude + ", " + longitude);
                Toast.makeText(getApplicationContext(), "Last Known Location : " + "Latitude : " + latitude + "\nLongitude:" + longitude, Toast.LENGTH_LONG).show();
                //SearchAddress(latitude, longitude);
                //GPSLatitude = latitude;
                //GPSLongtitude = longitude;
            }
        } catch(SecurityException ex) {
            ex.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "위치 확인이 시작되었습니다. 로그를 확인하세요.", Toast.LENGTH_SHORT).show();
    }

    private class GPSListener implements LocationListener {

        //위치 정보가 확인될 때 자동 호출되는 메소드
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            //GPSLatitude = latitude;
            //GPSLongtitude = longitude;

            String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude;
            Log.i("GPSListener", msg);

            textViewAddress.setText("내 위치 : " + latitude + ", " + longitude);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            //SearchAddress(latitude, longitude);
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
                //Toast.makeText(this, "주소 에러", Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
