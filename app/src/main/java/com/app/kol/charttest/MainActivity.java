package com.app.kol.charttest;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

import com.androidplot.Plot;
import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieLegendWidget;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.ui.Size;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FloatingActionButton fab;
    int currentView = 0;
    PieChart pie;
    AnyChartView anyChartView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pie = findViewById(R.id.pie_plot);
        anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            switch (currentView){
                case 0:
                    pie.setVisibility(View.INVISIBLE);
                    anyChartView.setVisibility(View.VISIBLE);
                    Snackbar.make(anyChartView,"Anychart view", BaseTransientBottomBar.LENGTH_LONG).show();
                    currentView = 1;
                    break;
                case 1:
                    anyChartView.setVisibility(View.INVISIBLE);
                    pie.setVisibility(View.VISIBLE);
                    Snackbar.make(anyChartView,"AndroidPlot view", BaseTransientBottomBar.LENGTH_LONG).show();
                    currentView = 0;
                    break;
            }
        });
        showPieChartFromAnyChart();
        showPieWithAndroidPlot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showPieWithAndroidPlot() {
        for (int i = 0; i < 10; i++) {
            Segment segment = new Segment("my segment " + i, 10);
            SegmentFormatter formatter = new SegmentFormatter(Color.rgb(23 , 23 ,23 * i));
            formatter.setRadialInset(2);
            formatter.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);
            formatter.getLabelPaint().setTextSize(14);
            pie.addSegment(segment, formatter);
        }
        pie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: clicked");
            }
        });
        pie.getLegend().setVisible(true);
        pie.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.performClick();
                PointF click = new PointF(motionEvent.getX(), motionEvent.getY());
                if(pie.getPie().containsPoint(click)) {
                    Segment segment = pie.getRenderer(PieRenderer.class).getContainingSegment(click);

                    if(segment != null) {
                        final boolean isSelected = getFormatter(segment).getOffset() != 0;
                        deselectAll();
                        setSelected(segment, !isSelected);
                        pie.redraw();
                    }
                }
                return false;
            }

            private SegmentFormatter getFormatter(Segment segment) {
                return pie.getFormatter(segment, PieRenderer.class);
            }

            private void deselectAll() {
                List<Segment> segments = pie.getRegistry().getSeriesList();
                for(Segment segment : segments) {
                    setSelected(segment, false);
                }
            }

            private void setSelected(Segment segment, boolean isSelected) {
                SegmentFormatter f = getFormatter(segment);
                if(isSelected) {
                    f.setOffset(50);
                } else {
                    f.setOffset(0);
                }
            }
        });
        setupIntroAnimation(pie);
    }

    protected void setupIntroAnimation(PieChart pie) {

        final PieRenderer renderer = pie.getRenderer(PieRenderer.class);
        // start with a zero degrees pie:

        renderer.setExtentDegs(0);
        // animate a scale value from a starting val of 0 to a final value of 1:
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

        // use an animation pattern that begins and ends slowly:
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scale = valueAnimator.getAnimatedFraction();
                renderer.setExtentDegs(360 * scale);
                pie.redraw();
            }
        });

        // the animation will run for 1.5 seconds:
        animator.setDuration(1500);
        animator.start();
    }

    private void showPieChartFromAnyChart() {

        Pie anyPie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            data.add(new ValueDataEntry("John " + 1, 10000));
            data.add(new ValueDataEntry("Jake "  + 1, 12000));
            data.add(new ValueDataEntry("Peter "  + 1, 18000));
            data.add(new ValueDataEntry("Jonathan "  + 1, 10000));
            data.add(new ValueDataEntry("A very Long texxxx xxxxxx xxxx xxxxxx xxxx xxt "  + 1, 12000));
            data.add(new ValueDataEntry("Chris "  + 1, 18000));
        }
        anyPie.data(data);
        anyChartView.setChart(anyPie);
    }
}