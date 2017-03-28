package com.call.log.infinity.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.database.CallCountsQueryModel;
import com.call.log.infinity.database.CallCountsQueryModel_QueryTable;
import com.call.log.infinity.database.CallLogModelDBFlow;
import com.call.log.infinity.database.CallLogModelDBFlow_Table;
import com.call.log.infinity.database.LongResultQueryModel;
import com.call.log.infinity.database.TotalDurationQueryModel;
import com.call.log.infinity.database.TotalDurationQueryModel_QueryTable;
import com.call.log.infinity.utils.BigNumberFormatter;
import com.call.log.infinity.utils.CallDurationFormatter;
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
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DatabaseToMapActivity extends AppCompatActivity {
    static final String TAG = "DatabaseToMapActivity";

    private static final float PIE_CHART_LEFT = 20;
    private static final float PIE_CHART_RIGHT = 20;

    private static final float BAR_CHART_LEFT = 10;
    private static final float BAR_CHART_RIGHT = 10;

    private static final int CONTACTS_LIMIT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_to_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化主题颜色
        setThemeAtStart();

        initCountPieChart();
        initDurationPieChart();
        initCountBarChart();
        initDurationBarChart();
    }

    /**
     * 初始化通话次数饼图。
     */
    private void initCountPieChart() {
        final ArrayList<Integer> pieChartColors = new ArrayList<>();
        pieChartColors.add(ContextCompat.getColor(MyApplication.getContext(), R.color.MDGreen));
        pieChartColors.add(ContextCompat.getColor(MyApplication.getContext(), R.color.MDBlue));

        new Thread(new Runnable() {
            @Override
            public void run() {
                LongResultQueryModel madeCallModel = queryCallCounts(CallLog.Calls.OUTGOING_TYPE);
                LongResultQueryModel recievedCallModel = queryCallCounts(CallLog.Calls.INCOMING_TYPE);

                ArrayList<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(madeCallModel.longResult, getString(R.string.madeCallCounts)));
                entries.add(new PieEntry(recievedCallModel.longResult, getString(R.string.recievedCallCounts)));

                //饼图的数据
                final PieData countPieData = initCountPieChartData(entries, pieChartColors);

                DecimalFormat mFormat = new DecimalFormat("###,###,###,##0");
                final String countPieDesciptionLabel =String.format(getString(R.string.totalCallCounts),
                    mFormat.format(madeCallModel.longResult + recievedCallModel.longResult));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCountPieChart(countPieData, countPieDesciptionLabel);
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化通话时长饼图。
     */
    private void initDurationPieChart() {
        final ArrayList<Integer> pieChartColors = new ArrayList<>();
        pieChartColors.add(ContextCompat.getColor(MyApplication.getContext(), R.color.MDGreen));
        pieChartColors.add(ContextCompat.getColor(MyApplication.getContext(), R.color.MDBlue));

        new Thread(new Runnable() {
            @Override
            public void run() {
                LongResultQueryModel madeCallModel = queryTotalDuration(CallLog.Calls.OUTGOING_TYPE);
                LongResultQueryModel recievedCallModel = queryTotalDuration(CallLog.Calls.INCOMING_TYPE);

                ArrayList<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry(madeCallModel.longResult, getString(R.string.madeCallDuration)));
                entries.add(new PieEntry(recievedCallModel.longResult, getString(R.string.recievedCallDuration)));

                //饼图的数据
                final PieData durationPieData = initDurationPieChartData(entries, pieChartColors);

                DecimalFormat mFormat = new DecimalFormat("###,###,###,##0");
                final String durationPieDesciptionLabel =String.format(getString(R.string.totalDuration),
                    mFormat.format(madeCallModel.longResult + recievedCallModel.longResult));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setDurationPieChart(durationPieData, durationPieDesciptionLabel);
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化通话次数的柱状图。
     */
    private void initCountBarChart() {
        //设置柱状图采用的颜色
        final ArrayList<Integer> barChartColors = new ArrayList<>();
        for (int c : MaterialDesignColor.MDColorsDeepToLight)
            barChartColors.add(c);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            barChartColors.add(c);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<CallCountsQueryModel> callCountsQueryModels = queryMaxCallCountsList();

                final ArrayList<String> countBarLabels = new ArrayList<>();
                for (int i = 0; i < callCountsQueryModels.size(); i++) {
                    countBarLabels.add(callCountsQueryModels.get(i).contactsName);
                }

                //柱状图的数据
                final BarData countBarData = initCountBarData(callCountsQueryModels, barChartColors);

                //更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCountBarChart(countBarData, countBarLabels, barChartColors);
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化通话时长的柱状图。
     */
    private void initDurationBarChart() {
        //设置柱状图采用的颜色
        final ArrayList<Integer> barChartColors = new ArrayList<>();
        for (int c : MaterialDesignColor.MDColorsDeepToLight)
            barChartColors.add(c);
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            barChartColors.add(c);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<TotalDurationQueryModel> totalDurationQueryModels = queryMaxTotalDurationList();

                final ArrayList<String> durationBarLabels = new ArrayList<>();
                for (int i = 0; i < totalDurationQueryModels.size(); i++) {
                    durationBarLabels.add(totalDurationQueryModels.get(i).contactsName);
                }

                //柱状图的数据
                final BarData durationBarData = initDurationBarData(totalDurationQueryModels, barChartColors);

                //更新UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setDurationBarChart(durationBarData, durationBarLabels, barChartColors);
                    }
                });
            }
        }).start();
    }

    /**
     * 设置通话次数饼图的数据和属性。
     * @param data
     * @param descriptionLabel
     */
    private void setCountPieChart(PieData data, String descriptionLabel) {
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
     * 设置通话时长饼图的数据和属性。
     * @param data
     * @param descriptionLabel
     */
    private void setDurationPieChart(PieData data, String descriptionLabel) {
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
     * 设置通话次数统计的条形图的数据和属性。
     * @param data
     * @param labels
     * @param colors
     */
    private void setCountBarChart(BarData data, ArrayList<String> labels, ArrayList<Integer> colors) {
        BarChart countBarChart = (BarChart) findViewById(R.id.count_bar_chart);

        countBarChart.setExtraOffsets(BAR_CHART_LEFT, 0, BAR_CHART_RIGHT, 0);
        countBarChart.setDrawBarShadow(false);
        countBarChart.setTouchEnabled(false);
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
        xAxis.setGranularity(1f);
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
     * 设置通话时长统计的条形图的数据和属性。
     * @param data
     * @param labels
     * @param colors
     */
    private void setDurationBarChart(BarData data, ArrayList<String> labels, ArrayList<Integer> colors) {
        BarChart durationBarChart = (BarChart) findViewById(R.id.duration_bar_chart);

        durationBarChart.setExtraOffsets(BAR_CHART_LEFT, 0, BAR_CHART_RIGHT, 0);
        durationBarChart.setDrawBarShadow(false);
        durationBarChart.setTouchEnabled(false);
        durationBarChart.animateY(1000);
        Description description = durationBarChart.getDescription();
        description.setText(getString(R.string.durationDescription));

        // scaling can now only be done on x- and y-axis separately
        durationBarChart.setPinchZoom(false);
        durationBarChart.setDrawGridBackground(true);

        XAxis xAxis = durationBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
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
     * @param models
     * @param colors
     * @return
     */
    private BarData initCountBarData(List<CallCountsQueryModel> models, ArrayList<Integer> colors) {
        BarDataSet set1;

        ArrayList<BarEntry> yValues = new ArrayList<>();

        for (int i = 0; i < models.size(); i++) {
            yValues.add(new BarEntry(i, (float) models.get(i).counts));
        }

        set1 = new BarDataSet(yValues, "");

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
     * @param models
     * @param colors
     * @return
     */
    private BarData initDurationBarData(List<TotalDurationQueryModel> models, ArrayList<Integer> colors) {

        ArrayList<BarEntry> yValues = new ArrayList<>();

        for (int i = 0; i < models.size(); i++) {
            yValues.add(new BarEntry(i, (float) models.get(i).totalDuration));
        }

        BarDataSet set1;

        set1 = new BarDataSet(yValues, "");

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
     * 查询指定类型的通话的数量。
     * @param type
     * @return
     */
    private LongResultQueryModel queryCallCounts(int type) {
        ConditionGroup sqlCondition = ConditionGroup.clause();

        if (type == CallLog.Calls.OUTGOING_TYPE) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.OUTGOING_TYPE));
        } else {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.INCOMING_TYPE))
                .or(ConditionGroup.clause().and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.MISSED_TYPE)));
        }

        LongResultQueryModel model = SQLite.select(Method.count(CallLogModelDBFlow_Table.contactsName)
                .as("longResult"))
            .from(CallLogModelDBFlow.class)
            .where(sqlCondition)
            .queryCustomSingle(LongResultQueryModel.class);

        return model;
    }

    /**
     * 查询指定类型的通话的总通话时长。
     * @param type
     * @return
     */
    private LongResultQueryModel queryTotalDuration(int type) {
        ConditionGroup sqlCondition = ConditionGroup.clause();

        if (type == CallLog.Calls.OUTGOING_TYPE) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.OUTGOING_TYPE));
        } else {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.INCOMING_TYPE))
                .or(ConditionGroup.clause().and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.MISSED_TYPE)));
        }

        LongResultQueryModel model = SQLite.select(Method.sum(CallLogModelDBFlow_Table.duration)
            .as("longResult"))
            .from(CallLogModelDBFlow.class)
            .where(sqlCondition)
            .queryCustomSingle(LongResultQueryModel.class);

        return model;
    }

    /**
     * 查询通话时长总计最长的几个联系人。
     * @return
     */
    private List<TotalDurationQueryModel> queryMaxTotalDurationList() {

        List<TotalDurationQueryModel> lists = SQLite.select(CallLogModelDBFlow_Table.contactsName,
                Method.sum(CallLogModelDBFlow_Table.duration).as("totalDuration"))
            .from(CallLogModelDBFlow.class)
            .groupBy(CallLogModelDBFlow_Table.contactsName)
            .orderBy(TotalDurationQueryModel_QueryTable.totalDuration, false)
            .limit(CONTACTS_LIMIT)
            .queryCustomList(TotalDurationQueryModel.class);

        return lists;
    }

    /**
     * 查询通话次数最多的几个联系人。
     * @return
     */
    private List<CallCountsQueryModel> queryMaxCallCountsList() {

        List<CallCountsQueryModel> lists = SQLite.select(CallLogModelDBFlow_Table.contactsName,
            Method.count(CallLogModelDBFlow_Table.contactsName).as("counts"))
            .from(CallLogModelDBFlow.class)
            .groupBy(CallLogModelDBFlow_Table.contactsName)
            .orderBy(CallCountsQueryModel_QueryTable.counts, false)
            .limit(CONTACTS_LIMIT)
            .queryCustomList(CallCountsQueryModel.class);

        return lists;
    }

}
