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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_GeoInput;
    TextView tv_GeoText;
    Button btn_GeoStart;

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_GeoStart:
                String result = SearchLocation(String.valueOf(et_GeoInput.getText()));
                tv_GeoText.setText(result);
                et_GeoInput.setText(mAddress.getAddressLine(0));
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

}