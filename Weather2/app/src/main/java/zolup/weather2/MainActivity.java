package zolup.weather2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

// 위도,경도값으로 날씨정보(오픈웨더 상 0 ~ 800? 그 숫자값) 불러오는 코드
// 즉 여기의 weatherId 변수를 분류하여 isRainy변수의 true, false값 설정 구현 필요(아직 안 함)
public class MainActivity extends AppCompatActivity {
    String latitude, longitude;
    EditText latEditText, lonEditText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latEditText = (EditText) findViewById(R.id.latEditText);
        lonEditText = (EditText) findViewById(R.id.lonEditText);
        textView = (TextView) findViewById(R.id.textView);
    }

    public void onClickStartBtn (View v) {
        latitude = latEditText.getText().toString();
        longitude = lonEditText.getText().toString();
        OpenWeatherTask openWeatherTask = new OpenWeatherTask();
        try {
            int weatherId = openWeatherTask.execute(latitude, longitude).get();
            textView.setText(String.valueOf(weatherId));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
