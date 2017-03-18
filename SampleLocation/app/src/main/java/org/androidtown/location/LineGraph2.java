package org.androidtown.location;

import android.content.Context;
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class LineGraph2 {
    private GraphicalView view;

    private TimeSeries dataset1 = new TimeSeries("집중도 - 명상도");

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    public XYSeriesRenderer renderer1 = new XYSeriesRenderer();
    public XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    public LineGraph2() {

        //Add single dataset to multiple dataset
        mDataset.addSeries(dataset1);

        //customization time for line 1
        renderer1.setColor(Color.GREEN);
        renderer1.setPointStyle(PointStyle.SQUARE);
        renderer1.setFillPoints(true);
        renderer1.setLineWidth(3);

        //Enable Zoom
        //mRenderer.setZoomButtonsVisible(true);
        mRenderer.setXTitle("시간");
        mRenderer.setYTitle("명상도와 집중도의 차이");
        mRenderer.setAxisTitleTextSize(50); // x, y축 항목 글자크기
        mRenderer.setLegendTextSize(50); // 선 이름 크기

        //Add single renderer to multiple renderer
        mRenderer.addSeriesRenderer(renderer1);
    }

    public GraphicalView getView(Context context){
        view = ChartFactory.getLineChartView(context, mDataset, mRenderer);
        return view;
    }

    public void addNewPoints(Point p){
        dataset1.add(p.getX(), p.getY());
    }

}