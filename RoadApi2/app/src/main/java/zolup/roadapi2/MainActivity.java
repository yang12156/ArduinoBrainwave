package zolup.roadapi2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends Activity {
    TextView text, text2;
    EditText edit, edit2;
    String data, data2;
    RoadManager roadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit = (EditText)findViewById(R.id.edit);
        edit2 = (EditText)findViewById(R.id.edit2);
        text = (TextView)findViewById(R.id.text);
        text2 = (TextView)findViewById(R.id.text);
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        String longitude = edit.getText().toString();
                        String latitude = edit2.getText().toString();
                        roadManager = new RoadManager(longitude, latitude);
                        data = roadManager.searchRoadNum();
                        data2 = roadManager.searchRestArea();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                text.setText(data);
                                text2.setText(data2);
                            }
                        });
                    }
                }).start();
                break;
        }
    }

}
