package com.call.log.infinity.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;

import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.utils.BigNumberFormatter;
import com.call.log.infinity.utils.MaterialDesignColor;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DatabaseToMapActivity extends AppCompatActivity {

    private static final float PIE_CHART_LEFT = 20;
    private static final float PIE_CHART_RIGHT = 20;

    private static final float BAR_CHART_LEFT = 10;
    private static final float BAR_CHART_RIGHT = 10;

    private static final int CONTACTS_LIMIT = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_to_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化主题颜色
        setThemeAtStart();

        initChart();
    }

    /**
     * 初始化图表。
     */
    private void initChart() {
        final ArrayList<Integer> pieChartColors = new ArrayList<>();
        pieChartColors.add(ContextCompat.getColor(MyApplication.getContext(), R.color.MDGreen));
        pieChartColors.add(ContextCompat.getColor(MyApplication.getContext(), R.color.MDBlue));

        //设置柱状图采用的颜色
        final ArrayList<Integer> barChartColors = new ArrayList<>();
        for (int c : MaterialDesignColor.MDColorsDeepToLight)
            barChartColors.add(c);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            barChartColors.add(c);

        final ProgressDialog pgDialog = createProgressDialog(null, getString(R.string.updateDatabaseIng));
        pgDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(1000, "去电时长"));
                entries.add(new PieEntry(300, "来电时长"));

                //饼图的数据
                final PieData countPieData = initCountPieChartData(entries, pieChartColors);
                final PieData durationPieData = initDurationPieChartData(entries, pieChartColors);

                final String countPieDesciptionLabel = "总计：386次";
                final String durationPieDesciptionLabel = "总计：500分钟";

                //柱状图的数据
                final BarData countBarData = initCountBarData(barChartColors);
                final BarData durationBarData = initDurationBarData(barChartColors);

                final ArrayList<String> labels = new ArrayList<>();
                labels.add("张三");
                labels.add("李四");
                labels.add("王五");
                labels.add("张三");
                labels.add("李四");
                labels.add("王五");
                labels.add("张三");
                labels.add("李四");
                labels.add("王五");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pgDialog.dismiss();

                        initCountPieChart(countPieData, countPieDesciptionLabel);
                        initDurationPieChart(durationPieData, durationPieDesciptionLabel);

                        initCountBarChart(countBarData, labels, barChartColors);
                        initDurationBarChart(durationBarData, labels, barChartColors);
                    }
                });
            }
        }).start();
    }

    /**
     * 按通话次数统计的饼图。
     */
    private void initCountPieChart(PieData data, String descriptionLabel) {
        PieChart countChart = (PieChart) findViewById(R.id.count_chart);
        countChart.setUsePercentValues(true);

        countChart.setExtraOffsets(PIE_CHART_LEFT, 0, PIE_CHART_RIGHT, 0);
        countChart.setDragDecelerationFrictionCoef(0.95f);

        countChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        countChart.setRotationEnabled(true);
        countChart.setHighlightPerTapEnabled(true);

        countChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);

        countChart.setDrawHoleEnabled(false);
        Description description = countChart.getDescription();
        description.setText(descriptionLabel);
        description.setTextAlign(Paint.Align.LEFT);

        //legend在右侧垂直居中
        Legend legend = countChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        countChart.setData(data);
        countChart.highlightValues(null);
        countChart.invalidate();
    }

    /**
     * 按通话时长统计的饼图。
     */
    private void initDurationPieChart(PieData data, String descriptionLabel) {
        PieChart durationChart = (PieChart) findViewById(R.id.duration_chart);
        durationChart.setUsePercentValues(true);

        durationChart.setExtraOffsets(PIE_CHART_LEFT, 0, PIE_CHART_RIGHT, 0);
        durationChart.setDragDecelerationFrictionCoef(0.95f);

        durationChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        durationChart.setRotationEnabled(true);
        durationChart.setHighlightPerTapEnabled(true);

        durationChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);

        durationChart.setDrawHoleEnabled(false);
        Description description = durationChart.getDescription();
        description.setText(descriptionLabel);
        description.setTextAlign(Paint.Align.LEFT);

        //legend在右侧垂直居中
        Legend legend = durationChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        durationChart.setData(data);
        durationChart.highlightValues(null);
        durationChart.invalidate();
    }

    /**
     * 按通话次数统计的条形图。
     * @param data
     * @param labels
     * @param colors
     */
    private void initCountBarChart(BarData data, ArrayList<String> labels, ArrayList<Integer> colors) {
        BarChart countBarChart = (BarChart) findViewById(R.id.count_bar_chart);

        countBarChart.setExtraOffsets(BAR_CHART_LEFT, 0, BAR_CHART_RIGHT, 0);
        countBarChart.setDrawBarShadow(false);
        countBarChart.setDrawValueAboveBar(true);
        countBarChart.animateY(1000);
        Description description = countBarChart.getDescription();
        description.setText(getString(R.string.countDescription));

        // if more than 60 entries are displayed in the chart, no values will be drawn
        countBarChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        countBarChart.setPinchZoom(false);
        countBarChart.setDrawGridBackground(true);

        XAxis xAxis = countBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);

        //左侧坐标轴
        YAxis leftAxis = countBarChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setValueFormatter(new MyAxisValueFormatter());

        //右侧坐标轴不显示
        countBarChart.getAxisRight().setEnabled(false);

        //legend放在图正下方居中，允许折行
        Legend legend = countBarChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);
        //默认为0.95f，在使用异步线程获取并UI更新的时候，会超出屏幕，十分奇怪
        legend.setMaxSizePercent(0.8f);
        setBarChartLegendData(legend, labels, colors);

        countBarChart.setData(data);
        countBarChart.invalidate();
    }

    /**
     * 按通话时长统计的条形图。
     * @param data
     * @param labels
     * @param colors
     */
    private void initDurationBarChart(BarData data, ArrayList<String> labels, ArrayList<Integer> colors) {
        BarChart durationBarChart = (BarChart) findViewById(R.id.duration_bar_chart);

        durationBarChart.setExtraOffsets(BAR_CHART_LEFT, 0, BAR_CHART_RIGHT, 0);
        durationBarChart.setDrawBarShadow(false);
        durationBarChart.animateY(1000);
        Description description = durationBarChart.getDescription();
        description.setText(getString(R.string.durationDescription));

        // scaling can now only be done on x- and y-axis separately
        durationBarChart.setPinchZoom(false);
        durationBarChart.setDrawGridBackground(true);

        XAxis xAxis = durationBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);

        //左侧坐标轴
        YAxis leftAxis = durationBarChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setValueFormatter(new MyAxisValueFormatter());

        //右侧坐标轴不显示
        durationBarChart.getAxisRight().setEnabled(false);

        //legend放在图正下方居中，允许折行
        Legend legend = durationBarChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setWordWrapEnabled(true);
        //默认为0.95f，在使用异步线程获取并UI更新的时候，会超出屏幕，十分奇怪
        legend.setMaxSizePercent(0.8f);
        setBarChartLegendData(legend, labels, colors);

        durationBarChart.setData(data);
        durationBarChart.invalidate();
    }

    /**
     * 初始化countPieChart的数据。
     * @param entries
     * @param colors
     */
    private PieData initCountPieChartData(ArrayList<PieEntry> entries, ArrayList<Integer> colors) {
        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(1f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        return data;
    }

    /**
     * 初始化durationPieChart的数据。
     * @param entries
     * @param colors
     */
    private PieData initDurationPieChartData(ArrayList<PieEntry> entries, ArrayList<Integer> colors) {
        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(1f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        return data;
    }

    /**
     * 生成countBar的数据。
     * @param colors
     * @return
     */
    private BarData initCountBarData(ArrayList<Integer> colors) {

        float start = 1f;
        int count = 9;
        float range = 200;

        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = (int) start; i < start + count + 1; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);

            if (Math.random() * 100 < 25) {
                yVals1.add(new BarEntry(i, (float) Math.floor(val)));
            } else {
                yVals1.add(new BarEntry(i, (float) Math.floor(val)));
            }
        }

        BarDataSet set1;

        set1 = new BarDataSet(yVals1, "");

        set1.setDrawIcons(false);
        set1.setColors(colors);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        data.setValueFormatter(new MyValueFormatter());

        return data;
    }

    /**
     * 生成durationBar的数据。
     * @param colors
     * @return
     */
    private BarData initDurationBarData(ArrayList<Integer> colors) {

        float start = 1f;
        int count = 9;
        float range = 200;

        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = (int) start; i < start + count + 1; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);

            if (Math.random() * 100 < 25) {
                yVals1.add(new BarEntry(i, (float) Math.floor(val)));
            } else {
                yVals1.add(new BarEntry(i, (float) Math.floor(val)));
            }
        }

        BarDataSet set1;

        set1 = new BarDataSet(yVals1, "");

        set1.setDrawIcons(false);
        set1.setColors(colors);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);
        data.setValueFormatter(new MyValueFormatter());

        return data;
    }

    /**
     * 设置条形图的Legend。
     * @param legend
     * @param labels
     * @param colors
     */
    public void setBarChartLegendData(Legend legend, ArrayList<String> labels, ArrayList<Integer> colors) {
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();

        for (int i = 0; i < labels.size(); i++ ) {
            LegendEntry entry = new LegendEntry();
            entry.label = labels.get(i);
            entry.formColor = colors.get(i % colors.size());
            legendEntries.add(entry);
        }
        legend.setCustom(legendEntries);
    }

    /**
     * 程序初始化时，设置主题颜色。
     */
    private void setThemeAtStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(MyApplication.getThemeColorPrimaryDark());
        }
    }

    /**
     * 格式化DataSet数字。
     */
    private class MyValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return BigNumberFormatter.format(value);
        }
    }

    /**
     * 格式化坐标轴标签。
     */
    private class MyAxisValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return BigNumberFormatter.format(value);
        }
    }

    /**
     * 一个圆圈的进度对话框
     * @param title
     * @param msg
     * @return
     */
    private ProgressDialog createProgressDialog(String title, String msg) {

        ProgressDialog pgDialog = new ProgressDialog(DatabaseToMapActivity.this);

        pgDialog.setTitle(title);
        pgDialog.setMessage(msg);
        pgDialog.setCancelable(false);
        pgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pgDialog.setIndeterminate(false);

        return pgDialog;
    }
}
