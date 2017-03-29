package org.androidtown.location;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class LineGraph {
    private GraphicalView view;

    private TimeSeries dataset1 = new TimeSeries("집중도     ");
    private TimeSeries dataset2 = new TimeSeries("명상도");

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    public XYSeriesRenderer renderer1 = new XYSeriesRenderer();
    public XYSeriesRenderer renderer2 = new XYSeriesRenderer();
    public XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    public LineGraph() {

        //Add single dataset to multiple dataset
        mDataset.addSeries(dataset1);
        mDataset.addSeries(dataset2);

        //customization time for line 1
        renderer1.setColor(Color.rgb(204,051,000));
        renderer1.setPointStyle(PointStyle.SQUARE);
        renderer1.setFillPoints(true);
        renderer1.setLineWidth(8);

        //customization time for line 2
        renderer2.setColor(Color.rgb(000,152,173));
        renderer2.setPointStyle(PointStyle.SQUARE);
        renderer2.setFillPoints(true);
        renderer2.setLineWidth(8);

        mRenderer.setXTitle("시간");
        mRenderer.setChartTitle("집중도와 명상도");
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setAxisTitleTextSize(40); // x, y축 항목 글자크기
        mRenderer.setLegendTextSize(40); // 선 이름 크기
        mRenderer.setLabelsTextSize(30); // x, y축 숫자 크기
        mRenderer.setChartTitleTextSize(50);
        mRenderer.setLegendHeight(60);
        mRenderer.setYLabelsPadding(35);
        mRenderer.setMarginsColor(Color.BLACK);
        mRenderer.setBackgroundColor(Color.BLACK);
        mRenderer.setMargins(new int[] {90, 70, 60, 10});

        //배경격자무늬
        mRenderer.setShowGridX(true);
        mRenderer.setGridColor(Color.LTGRAY);
        mRenderer.setXLabels(20);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setPanEnabled(true, false);

        //Add single renderer to multiple renderer
        mRenderer.addSeriesRenderer(renderer1);
        mRenderer.addSeriesRenderer(renderer2);
    }

    public GraphicalView getView(Context context){
        view = ChartFactory.getLineChartView(context, mDataset, mRenderer);
        return view;
    }

    public void addNewPoints(Point p1, Point p2){
        dataset1.add(p1.getX(), p1.getY());
        dataset2.add(p2.getX(), p2.getY());
    }

}