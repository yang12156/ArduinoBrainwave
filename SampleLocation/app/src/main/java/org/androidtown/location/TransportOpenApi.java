package org.androidtown.location;

import android.content.ContentValues;
import android.os.StrictMode;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TransportOpenApi extends Thread {

    String lon,lat;

    ArrayList<ContentValues> mWeatehr;
    MainActivity mContext;

    public ArrayList<ContentValues> getmWeather()
    {
        return mWeatehr;
    }

    public TransportOpenApi(String lon, String lat,MainActivity mContext)
    {
        this.lon = lon ; this.lat = lat;
        this.mContext = mContext;
    }

    public ArrayList<ContentValues> GetOpenWeather(String lon, String lat)
    {
        ArrayList<ContentValues> mTotalValue = new ArrayList<ContentValues>();
        String key = "1490336305139";
        try{
            URL url = new URL("openapi.its.go.kr/api/PosIncidentInfo?" +
                    "key=" + key +
                    "&MinX=" + lon +
                    "&MaxX=" + lon +
                    "&MinY=" + lat +
                    "&MaxY=" + lat );

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // 위에서 생성된 URL을 통하여 서버에 요청하면 결과가 XML Resource로 전달됨
            XmlPullParser parser = factory.newPullParser();
            // XML Resource를 파싱할 parser를 factory로 생성
            parser.setInput(url.openStream(), null);
            // 파서를 통하여 각 요소들의 이벤트성 처리를 반복수행
            int parserEvent = parser.getEventType();
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                if(parserEvent == XmlPullParser.START_TAG  && parser.getName().equals("time")){
                    //시작태그의 이름을 알아냄
                    int checkStartTag = parserEvent;
                    ContentValues mContent = new ContentValues();

                    for( ; ; ) {
                        if (checkStartTag == XmlPullParser.START_TAG  && parser.getName().equals("routeno")) {
                            mContent.put("routeNo", parser.getAttributeValue(null, "routeNo"));
                            break;
                        }
                        checkStartTag = parser.next();
                    }

                }
                parserEvent = parser.next();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mTotalValue;
    }



    @Override    public void run() {
        super.run();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mWeatehr = GetOpenWeather(lon,lat);
        mContext.handler.sendEmptyMessage(mContext.THREAD_HANDLER_SUCCESS_INFO);
        //Thread 작업 종료, UI 작업을 위해 MainHandler에 Message보냄    }

}
