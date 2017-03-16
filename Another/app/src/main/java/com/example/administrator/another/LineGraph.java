package com.example.administrator.another;

/**
 * Created by Administrator on 2017-03-15.
 */

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

    private TimeSeries dataset1 = new TimeSeries("Attention");
    private TimeSeries dataset2 = new TimeSeries("Medidation");

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    public XYSeriesRenderer renderer1 = new XYSeriesRenderer();
    public XYSeriesRenderer renderer2 = new XYSeriesRenderer();
    public XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    private Context context;

    public LineGraph(Context context) {

        this.context = context;

        //Add single dataset to multiple dataset
        mDataset.addSeries(dataset1);
        mDataset.addSeries(dataset2);

        //customization time for line 1
        renderer1.setColor(Color.RED);
        renderer1.setPointStyle(PointStyle.SQUARE);
        renderer1.setFillPoints(true);
        renderer1.setLineWidth(3);


        //customization time for line 2
        renderer2.setColor(Color.BLUE);
        renderer2.setPointStyle(PointStyle.SQUARE);
        renderer2.setFillPoints(true);
        renderer2.setLineWidth(3);

        //Enable Zoom
        //mRenderer.setZoomButtonsVisible(true);
        mRenderer.setXTitle("Time");
        mRenderer.setYTitle("Attention and Meditation");


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
