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

        mRenderer.setChartTitle("명상도와 집중도의 차이");
        mRenderer.setXTitle("시간");
        mRenderer.setAxisTitleTextSize(40); // x, y축 항목 글자크기
        mRenderer.setLegendTextSize(40); // 선 이름 크기
        mRenderer.setLabelsTextSize(30); // x, y축 숫자 크기
        mRenderer.setChartTitleTextSize(50);
        mRenderer.setLegendHeight(60);
        mRenderer.setYLabelsPadding(35);
        mRenderer.setMarginsColor(Color.DKGRAY);
        mRenderer.setMargins(new int[] {90, 70, 60, 10});

        //배경격자무늬
        mRenderer.setShowGridX(true);
        mRenderer.setGridColor(Color.LTGRAY);
        mRenderer.setXLabels(20);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setPanEnabled(true, false);


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