package zolup.roadapi2;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class RoadManager {
    String longitude;
    String latitude;
    String roadNum = "";

    public RoadManager(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    String searchRoadNum(){
        String key="1490336305139";
        String queryUrl="http://openapi.its.go.kr/api/PosIncidentInfo"
                + "?key=" + key
                + "&MinX=" + longitude
                + "&MaxX=" + longitude
                + "&MinY=" + latitude
                + "&MaxY=" + latitude;
        try {
            URL url= new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is= url.openStream();  //url위치로 입력스트림 연결

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") );  //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();    //테그 이름 얻어오기

                        if(tag.equals("data")) {
                            xpp.next();
                            xpp.next();
                            xpp.next();
                            xpp.next();
                            xpp.next();
                        }
                        else if(tag.equals("rrouteno")){
                            xpp.next();
                            roadNum = xpp.getText();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName();    //테그 이름 얻어오기
                        break;
                }
                eventType= xpp.next();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return roadNum;
    }

    String searchRestArea(){
        StringBuffer buffer=new StringBuffer();
        String key="9466886488";
        String[] restAreaList = new String[100];
        double[] xValueList = new double[100];
        double[] yValueList = new double[100];

        String queryUrl="http://data.ex.co.kr/openapi/locationinfo/locationinfoRest"
                + "?key=" + key
                + "&type=xml"
                + "&routeNo=" + roadNum;
        try {
            URL url= new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is= url.openStream();  //url위치로 입력스트림 연결

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") );  //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();
            int i = 0;

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();    //테그 이름 얻어오기

                        if(tag.equals("list")) {
                            xpp.next();
                            xpp.next();
                            xpp.next();
                        }
                        else if(tag.equals("unitName")){
                            buffer.append("\n휴게소 이름 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        else if(tag.equals("xValue")){
                            buffer.append("\nx : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        else if(tag.equals("yValue")){
                            buffer.append("\ny : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName();    //테그 이름 얻어오기
                        break;
                }
                eventType= xpp.next();
            }

            buffer.append("\nEND");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
