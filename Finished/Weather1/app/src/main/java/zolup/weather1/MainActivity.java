package zolup.weather1;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;

// '주소 -> 위도,경도', '위도,경도 -> 주소' 로 변환하는 코드
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_GeoInput, et_LatInput, et_LonInput;
    TextView tv_GeoText, tv_AddressText;
    Button btn_GeoStart, btn_AddressStart;

    Geocoder mGeocoder;
    List<Address> mListAddress;
    Address mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
    }

    public void Init() {
        et_GeoInput = (EditText) findViewById(R.id.et_GeoInput);
        tv_GeoText = (TextView) findViewById(R.id.tv_GeoTextView);
        btn_GeoStart = (Button) findViewById(R.id.btn_GeoStart);
        btn_GeoStart.setOnClickListener(this);
        mGeocoder = new Geocoder(this);
        et_LatInput = (EditText) findViewById(R.id.et_LatInput);
        et_LonInput = (EditText) findViewById(R.id.et_LonInput);
        tv_AddressText = (TextView) findViewById(R.id.tv_AddressTextView);
        btn_AddressStart = (Button) findViewById(R.id.btn_AddressStart);
        btn_AddressStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_GeoStart:
                String result = SearchLocation(String.valueOf(et_GeoInput.getText()));
                tv_GeoText.setText(result);
                et_GeoInput.setText(mAddress.getAddressLine(0));
                break;
            case R.id.btn_AddressStart:
                Toast.makeText(this, "latitude : " + et_LatInput, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "longtitude : " + et_LonInput, Toast.LENGTH_SHORT).show();
                String result2 = SearchAddress(Double.parseDouble(et_LatInput.getText().toString()), Double.parseDouble(et_LonInput.getText().toString()));
                tv_AddressText.setText(result2);
                break;
        }
    }

    public String SearchLocation(String location) {
        String result = "";
        try {
            mListAddress = mGeocoder.getFromLocationName(location, 5);
            if (mListAddress.size() > 0) {
                mAddress = mListAddress.get(0);
                result = "lat : " + mAddress.getLatitude() + "\r\n"
                        + "lon : " + mAddress.getLongitude() + "\r\n"
                        + "Address : " + mAddress.getAddressLine(0);
            }
            else {
                Toast.makeText(this, "위치 검색 실패", Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String SearchAddress(double latitude, double longitude) {
        String result = "";
        Toast.makeText(this, "latitude : " + latitude, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "longtitude : " + longitude, Toast.LENGTH_SHORT).show();
        try {
            mListAddress = mGeocoder.getFromLocation(latitude, longitude, 1);
            if (mListAddress.size() > 0) {
                mAddress = mListAddress.get(0);
                result = "Address : " + mAddress.getAddressLine(0);
            }
            else {
                Toast.makeText(this, "주소 취득 실패", Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}