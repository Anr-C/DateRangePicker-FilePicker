package com.lckiss.rangedatepicker.lib.datepicker;

import android.app.Activity;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.lckiss.rangedatepicker.lib.datepicker.WheelPicker;
import com.lckiss.rangedatepicker.lib.common.utils.DateUtils;
import com.lckiss.rangedatepicker.lib.common.utils.LogUtils;
import com.lckiss.rangedatepicker.lib.datepicker.widget.WheelView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * 日期选择器，可选中日期段
 * <p/>
 */
public class DateRangePicker extends WheelPicker {
    /**
     * 不显示
     */
    public static final int NONE = -1;
    /**
     * 年月日
     */
    public static final int YEAR_MONTH_DAY = 0;
    /**
     * 年月
     */
    public static final int YEAR_MONTH = 1;
    /**
     * 月日
     */
    public static final int MONTH_DAY = 2;


    private ArrayList<String> years = new ArrayList<>();
    private ArrayList<String> months = new ArrayList<>();
    private ArrayList<String> days = new ArrayList<>();
    private String yearLabel = "年", monthLabel = "月", dayLabel = "日";
    private int selectedYearIndex = 0, selectedMonthIndex = 0, selectedDayIndex = 0;
    private int selectedSecondYearIndex = 0, selectedSecondMonthIndex = 0, selectedSecondDayIndex = 0;
    private OnWheelListener onWheelListener;
    private OnDatePickListener onDatePickListener;
    private int dateMode = YEAR_MONTH_DAY;
    private int startYear = 2010, startMonth = 1, startDay = 1;
    private int endYear = 2020, endMonth = 12, endDay = 31;
    private int textSize = WheelView.TEXT_SIZE;
    private boolean useWeight = false;
    //滚动年份是否重置月日
    private boolean resetWhileWheel = false;
    private boolean isRange = false;

    @IntDef(value = {NONE, YEAR_MONTH_DAY, YEAR_MONTH, MONTH_DAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DateMode {
    }

    public DateRangePicker(Activity activity, @DateMode int dateMode, boolean rangeMode) {
        super(activity);
        if (dateMode == NONE) {
            throw new IllegalArgumentException("The modes are NONE at the same time");
        }
        if (dateMode == YEAR_MONTH_DAY) {
            if (screenWidthPixels < 720) {
                textSize = 14;//年月日时分，比较宽，设置字体小一点才能显示完整
            } else if (screenWidthPixels < 480) {
                textSize = 12;
            }
        }
        this.dateMode = dateMode;
        this.isRange = rangeMode;
    }

    /**
     * 是否使用比重来平分布局
     */
    public void setUseWeight(boolean useWeight) {
        this.useWeight = useWeight;
    }

    /**
     * 滚动时是否重置下一级的索引
     */
    public void setResetWhileWheel(boolean resetWhileWheel) {
        this.resetWhileWheel = resetWhileWheel;
    }

    /**
     * 设置范围：开始的年月日
     */
    public void setDateRangeStart(int startYear, int startMonth, int startDay) {
        if (dateMode == NONE) {
            throw new IllegalArgumentException("Date mode invalid");
        }
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
        initYearData();
    }

    /**
     * 设置范围：结束的年月日
     */
    public void setDateRangeEnd(int endYear, int endMonth, int endDay) {
        if (dateMode == NONE) {
            throw new IllegalArgumentException("Date mode invalid");
        }
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDay = endDay;
        initYearData();
    }

    /**
     * 设置范围：开始的年月日
     */
    public void setDateRangeStart(int startYearOrMonth, int startMonthOrDay) {
        if (dateMode == NONE) {
            throw new IllegalArgumentException("Date mode invalid");
        }
        if (dateMode == YEAR_MONTH_DAY) {
            throw new IllegalArgumentException("Not support year/month/day mode");
        }
        if (dateMode == YEAR_MONTH) {
            this.startYear = startYearOrMonth;
            this.startMonth = startMonthOrDay;
        } else if (dateMode == MONTH_DAY) {
            int year = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
            startYear = endYear = year;
            this.startMonth = startYearOrMonth;
            this.startDay = startMonthOrDay;
        }
        initYearData();
    }

    /**
     * 设置范围：结束的年月日
     */
    public void setDateRangeEnd(int endYearOrMonth, int endMonthOrDay) {
        if (dateMode == NONE) {
            throw new IllegalArgumentException("Date mode invalid");
        }
        if (dateMode == YEAR_MONTH_DAY) {
            throw new IllegalArgumentException("Not support year/month/day mode");
        }
        if (dateMode == YEAR_MONTH) {
            this.endYear = endYearOrMonth;
            this.endMonth = endMonthOrDay;
        } else if (dateMode == MONTH_DAY) {
            this.endMonth = endYearOrMonth;
            this.endDay = endMonthOrDay;
        }
        initYearData();
    }

    /**
     * 设置年月日的显示单位
     */
    public void setLabel(String yearLabel, String monthLabel, String dayLabel, String hourLabel, String minuteLabel) {
        this.yearLabel = yearLabel;
        this.monthLabel = monthLabel;
        this.dayLabel = dayLabel;
    }

    /**
     * 设置默认选中的年月日
     */
    public void setSelectedItem(int year, int month, int day) {
        if (dateMode != YEAR_MONTH_DAY) {
            throw new IllegalArgumentException("Date mode invalid");
        }
        LogUtils.verbose(this, "change months and days while set selected");
        changeMonthData(year);
        changeDayData(year, month);
        selectedYearIndex = findItemIndex(years, year);
        selectedMonthIndex = findItemIndex(months, month);
        selectedDayIndex = findItemIndex(days, day);
    }
    public void setSelectedSecondItem(int year, int month, int day) {
        if (dateMode != YEAR_MONTH_DAY) {
            throw new IllegalArgumentException("Date mode invalid");
        }
        LogUtils.verbose(this, "change months and days while set selected");
        changeMonthData(year);
        changeDayData(year, month);
        selectedSecondYearIndex = findItemIndex(years, year);
        selectedSecondMonthIndex = findItemIndex(months, month);
        selectedSecondDayIndex = findItemIndex(days, day);
    }

    /**
     * 设置默认选中的年月时分或者月日时分
     */
    public void setSelectedItem(int yearOrMonth, int monthOrDay) {
        if (dateMode == YEAR_MONTH_DAY) {
            throw new IllegalArgumentException("Date mode invalid");
        }
        if (dateMode == MONTH_DAY) {
            LogUtils.verbose(this, "change months and days while set selected");
            int year = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
            startYear = endYear = year;
            changeMonthData(year);
            changeDayData(year, yearOrMonth);
            selectedMonthIndex = findItemIndex(months, yearOrMonth);
            selectedDayIndex = findItemIndex(days, monthOrDay);
        } else if (dateMode == YEAR_MONTH) {
            LogUtils.verbose(this, "change months while set selected");
            changeMonthData(yearOrMonth);
            selectedYearIndex = findItemIndex(years, yearOrMonth);
            selectedMonthIndex = findItemIndex(months, monthOrDay);
        }
    }

    public void setOnWheelListener(OnWheelListener onWheelListener) {
        this.onWheelListener = onWheelListener;
    }

    public void setOnDatePickListener(OnDatePickListener listener) {
        this.onDatePickListener = listener;
    }

    public String getSelectedYear() {
        if (dateMode == YEAR_MONTH_DAY || dateMode == YEAR_MONTH) {
            if (years.size() <= selectedYearIndex) {
                selectedYearIndex = years.size() - 1;
            }
            return years.get(selectedYearIndex);
        }
        return "";
    }

    public String getSelectedMonth() {
        if (dateMode != NONE) {
            if (months.size() <= selectedMonthIndex) {
                selectedMonthIndex = months.size() - 1;
            }
            return months.get(selectedMonthIndex);
        }
        return "";
    }

    public String getSelectedDay() {
        if (dateMode == YEAR_MONTH_DAY || dateMode == MONTH_DAY) {
            if (days.size() <= selectedDayIndex) {
                selectedDayIndex = days.size() - 1;
            }
            return days.get(selectedDayIndex);
        }
        return "";
    }
    public String getSelectedSecondYear() {
        if (dateMode == YEAR_MONTH_DAY || dateMode == YEAR_MONTH) {
            if (years.size() <= selectedSecondYearIndex) {
                selectedSecondYearIndex = years.size() - 1;
            }
            return years.get(selectedSecondYearIndex);
        }
        return "";
    }

    public String getSelectedSecondMonth() {
        if (dateMode != NONE) {
            if (months.size() <= selectedSecondMonthIndex) {
                selectedSecondMonthIndex = months.size() - 1;
            }
            return months.get(selectedSecondMonthIndex);
        }
        return "";
    }

    public String getSelectedSecondDay() {
        if (dateMode == YEAR_MONTH_DAY || dateMode == MONTH_DAY) {
            if (days.size() <= selectedSecondDayIndex) {
                selectedSecondDayIndex = days.size() - 1;
            }
            return days.get(selectedSecondDayIndex);
        }
        return "";
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        // 如果未设置默认项，则需要在此初始化数据
        if ((dateMode == YEAR_MONTH_DAY || dateMode == YEAR_MONTH) && years.size() == 0) {
            LogUtils.verbose(this, "init years before make view");
            initYearData();
        }
        if (dateMode != NONE && months.size() == 0) {
            LogUtils.verbose(this, "init months before make view");
            int selectedYear = DateUtils.trimZero(getSelectedYear());
            changeMonthData(selectedYear);
        }
        if ((dateMode == YEAR_MONTH_DAY || dateMode == MONTH_DAY) && days.size() == 0) {
            LogUtils.verbose(this, "init days before make view");
            int selectedYear;
            if (dateMode == YEAR_MONTH_DAY) {
                selectedYear = DateUtils.trimZero(getSelectedYear());
            } else {
                selectedYear = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
            }
            int selectedMonth = DateUtils.trimZero(getSelectedMonth());
            changeDayData(selectedYear, selectedMonth);
        }
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);

        LinearLayout startLayout = new LinearLayout(activity);
        startLayout.setOrientation(LinearLayout.HORIZONTAL);
        startLayout.setGravity(Gravity.CENTER);

        LinearLayout endLayout = new LinearLayout(activity);
        endLayout.setOrientation(LinearLayout.HORIZONTAL);
        endLayout.setGravity(Gravity.CENTER);

        final WheelView startYearView = createWheelView();
        final WheelView startMonthView = createWheelView();
        final WheelView startDayView = createWheelView();
        startYearView.setTextSize(textSize);
        startMonthView.setTextSize(textSize);
        startDayView.setTextSize(textSize);
        startYearView.setUseWeight(useWeight);
        startMonthView.setUseWeight(useWeight);
        startDayView.setUseWeight(useWeight);

        final WheelView endYearView = createWheelView();
        final WheelView endMonthView = createWheelView();
        final WheelView endDayView = createWheelView();
        endYearView.setTextSize(textSize);
        endMonthView.setTextSize(textSize);
        endDayView.setTextSize(textSize);
        endYearView.setUseWeight(useWeight);
        endMonthView.setUseWeight(useWeight);
        endDayView.setUseWeight(useWeight);


        if (dateMode == YEAR_MONTH_DAY || dateMode == YEAR_MONTH) {
            startYearView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f));
            startYearView.setItems(years, selectedYearIndex);
            //yearView.setLabel(yearLabel);
            startYearView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
                @Override
                public void onSelected(int index) {
                    selectedYearIndex = index;
                    String selectedYearStr = years.get(selectedYearIndex);
                    if (onWheelListener != null) {
                        onWheelListener.onYearWheeled(selectedYearIndex, selectedYearStr);
                    }
                    LogUtils.verbose(this, "change months after year wheeled");
                    if (resetWhileWheel) {
                        selectedMonthIndex = 0;//重置月份索引
                        selectedDayIndex = 0;//重置日子索引
                    }
                    //需要根据年份及月份动态计算天数
                    int selectedYear = DateUtils.trimZero(selectedYearStr);
                    changeMonthData(selectedYear);
                    startMonthView.setItems(months, selectedMonthIndex);
                    if (onWheelListener != null) {
                        onWheelListener.onMonthWheeled(selectedMonthIndex, months.get(selectedMonthIndex));
                    }
                    changeDayData(selectedYear, DateUtils.trimZero(months.get(selectedMonthIndex)));
                    startDayView.setItems(days, selectedDayIndex);
                    if (onWheelListener != null) {
                        onWheelListener.onDayWheeled(selectedDayIndex, days.get(selectedDayIndex));
                    }
                }
            });
            startLayout.addView(startYearView);
            if (!TextUtils.isEmpty(yearLabel)) {
                TextView labelView = createLabelView();
                labelView.setTextSize(textSize);
                labelView.setText(yearLabel);
                startLayout.addView(labelView);
            }
            if (isRange) {
                endYearView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f));
                endYearView.setItems(years, selectedSecondYearIndex);
                //yearView.setLabel(yearLabel);
                endYearView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
                    @Override
                    public void onSelected(int index) {
                        selectedSecondYearIndex = index;
                        String selectedYearStr = years.get(selectedSecondYearIndex);
                        if (onWheelListener != null) {
                            onWheelListener.onSecondYearWheeled(selectedSecondYearIndex, selectedYearStr);
                        }
                        LogUtils.verbose(this, "change months after year wheeled");
                        if (resetWhileWheel) {
                            selectedSecondMonthIndex = 0;//重置月份索引
                            selectedSecondDayIndex = 0;//重置日子索引
                        }
                        //需要根据年份及月份动态计算天数
                        int selectedYear = DateUtils.trimZero(selectedYearStr);
                        changeMonthData(selectedYear);
                        endMonthView.setItems(months, selectedSecondMonthIndex);
                        if (onWheelListener != null) {
                            onWheelListener.onSecondMonthWheeled(selectedSecondMonthIndex, months.get(selectedSecondMonthIndex));
                        }
                        changeDayData(selectedYear, DateUtils.trimZero(months.get(selectedSecondMonthIndex)));
                        endDayView.setItems(days, selectedSecondDayIndex);
                        if (onWheelListener != null) {
                            onWheelListener.onSecondDayWheeled(selectedSecondDayIndex, days.get(selectedSecondDayIndex));
                        }
                    }
                });
                endLayout.addView(endYearView);
                if (!TextUtils.isEmpty(yearLabel)) {
                    TextView labelView = createLabelView();
                    labelView.setTextSize(textSize);
                    labelView.setText(yearLabel);
                    endLayout.addView(labelView);
                }
            }
        }

        if (dateMode != NONE) {
            startMonthView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f));
            startMonthView.setItems(months, selectedMonthIndex);
            //monthView.setLabel(monthLabel);
            startMonthView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
                @Override
                public void onSelected(int index) {
                    selectedMonthIndex = index;
                    String selectedMonthStr = months.get(selectedMonthIndex);
                    if (onWheelListener != null) {
                        onWheelListener.onMonthWheeled(selectedMonthIndex, selectedMonthStr);
                    }
                    if (dateMode == YEAR_MONTH_DAY || dateMode == MONTH_DAY) {
                        LogUtils.verbose(this, "change days after month wheeled");
                        if (resetWhileWheel) {
                            selectedDayIndex = 0;//重置日子索引
                        }
                        int selectedYear;
                        if (dateMode == YEAR_MONTH_DAY) {
                            selectedYear = DateUtils.trimZero(getSelectedYear());
                        } else {
                            selectedYear = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
                        }
                        changeDayData(selectedYear, DateUtils.trimZero(selectedMonthStr));
                        startDayView.setItems(days, selectedDayIndex);
                        if (onWheelListener != null) {
                            onWheelListener.onDayWheeled(selectedDayIndex, days.get(selectedDayIndex));
                        }
                    }
                }
            });
            startLayout.addView(startMonthView);
            if (!TextUtils.isEmpty(monthLabel)) {
                TextView labelView = createLabelView();
                labelView.setTextSize(textSize);
                labelView.setText(monthLabel);
                startLayout.addView(labelView);
            }
            if (isRange) {
                endMonthView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f));
                endMonthView.setItems(months, selectedSecondMonthIndex);
                //monthView.setLabel(monthLabel);
                endMonthView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
                    @Override
                    public void onSelected(int index) {
                        selectedSecondMonthIndex = index;
                        String selectedMonthStr = months.get(selectedSecondMonthIndex);
                        if (onWheelListener != null) {
                            onWheelListener.onSecondMonthWheeled(selectedSecondMonthIndex, selectedMonthStr);
                        }
                        if (dateMode == YEAR_MONTH_DAY || dateMode == MONTH_DAY) {
                            LogUtils.verbose(this, "change days after month wheeled");
                            if (resetWhileWheel) {
                                selectedSecondDayIndex = 0;//重置日子索引
                            }
                            int selectedYear;
                            if (dateMode == YEAR_MONTH_DAY) {
                                selectedYear = DateUtils.trimZero(getSelectedYear());
                            } else {
                                selectedYear = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
                            }
                            changeDayData(selectedYear, DateUtils.trimZero(selectedMonthStr));
                            endDayView.setItems(days, selectedSecondDayIndex);
                            if (onWheelListener != null) {
                                onWheelListener.onSecondDayWheeled(selectedSecondDayIndex, days.get(selectedSecondDayIndex));
                            }
                        }
                    }
                });
                endLayout.addView(endMonthView);
                if (!TextUtils.isEmpty(monthLabel)) {
                    TextView labelView = createLabelView();
                    labelView.setTextSize(textSize);
                    labelView.setText(monthLabel);
                    endLayout.addView(labelView);
                }
            }
        }

        if (dateMode == YEAR_MONTH_DAY || dateMode == MONTH_DAY) {
            startDayView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f));
            startDayView.setItems(days, selectedDayIndex);
            //dayView.setLabel(dayLabel);
            startDayView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
                @Override
                public void onSelected(int index) {
                    selectedDayIndex = index;
                    if (onWheelListener != null) {
                        onWheelListener.onDayWheeled(selectedDayIndex, days.get(selectedDayIndex));
                    }
                }
            });
            startLayout.addView(startDayView);
            if (!TextUtils.isEmpty(dayLabel)) {
                TextView labelView = createLabelView();
                labelView.setTextSize(textSize);
                labelView.setText(dayLabel);
                startLayout.addView(labelView);
            }
            if (isRange) {
                endDayView.setLayoutParams(new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1.0f));
                endDayView.setItems(days, selectedSecondDayIndex);
                //dayView.setLabel(dayLabel);
                endDayView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
                    @Override
                    public void onSelected(int index) {
                        selectedSecondDayIndex = index;
                        if (onWheelListener != null) {
                            onWheelListener.onSecondDayWheeled(selectedSecondDayIndex, days.get(selectedSecondDayIndex));
                        }
                    }
                });
                endLayout.addView(endDayView);
                if (!TextUtils.isEmpty(dayLabel)) {
                    TextView labelView = createLabelView();
                    labelView.setTextSize(textSize);
                    labelView.setText(dayLabel);
                    endLayout.addView(labelView);
                }
            }
        }

        //范围选择起始标识
        if (isRange) {
            TextView startTips = new TextView(activity.getBaseContext());
            startTips.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            startTips.setGravity(Gravity.LEFT);
            startTips.setText("起始日期:");
            startTips.setPadding(50, 10, 0, 10);
            startTips.setTextSize(textSize);
            startTips.setTextColor(textColorFocus);
            root.addView(startTips);
        }
        root.addView(startLayout);
        if (isRange) {
            //范围选择结束标识
            TextView endTips = new TextView(activity.getBaseContext());
            endTips.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            endTips.setGravity(Gravity.LEFT);
            endTips.setText("截止日期:");
            endTips.setTextSize(textSize);
            endTips.setTextColor(textColorFocus);
            endTips.setPadding(50, 20, 0, 10);
            root.addView(endTips);
            root.addView(endLayout);
        }
        return root;
    }

    @Override
    protected void onSubmit() {
        if (onDatePickListener == null) {
            return;
        }
        String year = getSelectedYear();
        String month = getSelectedMonth();
        String day = getSelectedDay();

        String endyear = getSelectedSecondYear();
        String endmonth = getSelectedSecondMonth();
        String endday = getSelectedSecondDay();
        switch (dateMode) {
            case YEAR_MONTH_DAY:
                if (isRange) {
                    ((OnYearMonthDayDoublePickListener) onDatePickListener)
                            .onDatePicked(year, month, day, endyear, endmonth, endday);

                } else {
                    ((OnYearMonthDayRangePickListener) onDatePickListener).onDatePicked(year, month, day);
                }
                break;
            case YEAR_MONTH:
                if (isRange) {
                    ((OnYearMonthDoublePickListener) onDatePickListener)
                            .onDatePicked(year, month, endyear, endmonth);
                } else {
                    ((OnYearMonthRangePickListener) onDatePickListener).onDatePicked(year, month);
                }

                break;
            case MONTH_DAY:
                if (isRange) {
                    ((OnMonthDayDoublePickListener) onDatePickListener)
                            .onDatePicked(month, day, endmonth, endday);
                } else {
                    ((OnMonthDayRangePickListener) onDatePickListener).onDatePicked(month, day);
                }
                break;
        }
    }

    private int findItemIndex(ArrayList<String> items, int item) {
        //折半查找有序元素的索引
        int index = Collections.binarySearch(items, item, new Comparator<Object>() {
            @Override
            public int compare(Object lhs, Object rhs) {
                String lhsStr = lhs.toString();
                String rhsStr = rhs.toString();
                lhsStr = lhsStr.startsWith("0") ? lhsStr.substring(1) : lhsStr;
                rhsStr = rhsStr.startsWith("0") ? rhsStr.substring(1) : rhsStr;
                try {
                    return Integer.parseInt(lhsStr) - Integer.parseInt(rhsStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        if (index < 0) {
            throw new IllegalArgumentException("Item[" + item + "] out of range");
        }
        return index;
    }

    private void initYearData() {
        years.clear();
        if (startYear == endYear) {
            years.add(String.valueOf(startYear));
        } else if (startYear < endYear) {
            //年份正序
            for (int i = startYear; i <= endYear; i++) {
                years.add(String.valueOf(i));
            }
        } else {
            //年份逆序
            for (int i = startYear; i >= endYear; i--) {
                years.add(String.valueOf(i));
            }
        }
        if (!resetWhileWheel) {
            if (dateMode == YEAR_MONTH_DAY || dateMode == YEAR_MONTH) {
                int index = years.indexOf(DateUtils.fillZero(Calendar.getInstance().get(Calendar.YEAR)));
                if (index == -1) {
                    //当前设置的年份不在指定范围，则默认选中范围开始的年
                    selectedYearIndex = 0;
                } else {
                    selectedYearIndex = index;
                }
            }
        }
    }

    private void changeMonthData(int selectedYear) {
        String preSelectMonth = "";
        if (!resetWhileWheel) {
            if (months.size() > selectedMonthIndex) {
                preSelectMonth = months.get(selectedMonthIndex);
            } else {
                preSelectMonth = DateUtils.fillZero(Calendar.getInstance().get(Calendar.MONTH) + 1);
            }
            LogUtils.verbose(this, "preSelectMonth=" + preSelectMonth);
        }
        months.clear();
        if (startMonth < 1 || endMonth < 1 || startMonth > 12 || endMonth > 12) {
            throw new IllegalArgumentException("Month out of range [1-12]");
        }
        if (startYear == endYear) {
            if (startMonth > endMonth) {
                for (int i = endMonth; i >= startMonth; i--) {
                    months.add(DateUtils.fillZero(i));
                }
            } else {
                for (int i = startMonth; i <= endMonth; i++) {
                    months.add(DateUtils.fillZero(i));
                }
            }
        } else if (selectedYear == startYear) {
            for (int i = startMonth; i <= 12; i++) {
                months.add(DateUtils.fillZero(i));
            }
        } else if (selectedYear == endYear) {
            for (int i = 1; i <= endMonth; i++) {
                months.add(DateUtils.fillZero(i));
            }
        } else {
            for (int i = 1; i <= 12; i++) {
                months.add(DateUtils.fillZero(i));
            }
        }
        if (!resetWhileWheel) {
            //当前设置的月份不在指定范围，则默认选中范围开始的月份
            int preSelectMonthIndex = months.indexOf(preSelectMonth);
            selectedMonthIndex = preSelectMonthIndex == -1 ? 0 : preSelectMonthIndex;
        }
    }

    private void changeDayData(int selectedYear, int selectedMonth) {
        int maxDays = DateUtils.calculateDaysInMonth(selectedYear, selectedMonth);
        String preSelectDay = "";
        if (!resetWhileWheel) {
            if (selectedDayIndex >= maxDays) {
                //如果之前选择的日是之前年月的最大日，则日自动为该年月的最大日
                selectedDayIndex = maxDays - 1;
            }
            if (days.size() > selectedDayIndex) {
                //年或月变动时，保持之前选择的日不动
                preSelectDay = days.get(selectedDayIndex);
            } else {
                preSelectDay = DateUtils.fillZero(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            }
            LogUtils.verbose(this, "maxDays=" + maxDays + ", preSelectDay=" + preSelectDay);
        }
        days.clear();
        if (selectedYear == startYear && selectedMonth == startMonth
                && selectedYear == endYear && selectedMonth == endMonth) {
            //开始年月及结束年月相同情况
            for (int i = startDay; i <= endDay; i++) {
                days.add(DateUtils.fillZero(i));
            }
        } else if (selectedYear == startYear && selectedMonth == startMonth) {
            //开始年月相同情况
            for (int i = startDay; i <= maxDays; i++) {
                days.add(DateUtils.fillZero(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            //结束年月相同情况
            for (int i = 1; i <= endDay; i++) {
                days.add(DateUtils.fillZero(i));
            }
        } else {
            for (int i = 1; i <= maxDays; i++) {
                days.add(DateUtils.fillZero(i));
            }
        }
        if (!resetWhileWheel) {
            //当前设置的日子不在指定范围，则默认选中范围开始的日子
            int preSelectDayIndex = days.indexOf(preSelectDay);
            selectedDayIndex = preSelectDayIndex == -1 ? 0 : preSelectDayIndex;
        }
    }

    public interface OnWheelListener {

        void onYearWheeled(int index, String year);

        void onMonthWheeled(int index, String month);

        void onDayWheeled(int index, String day);

        void onSecondYearWheeled(int index, String year);

        void onSecondMonthWheeled(int index, String month);

        void onSecondDayWheeled(int index, String day);

    }

    protected interface OnDatePickListener {

    }

    public interface OnYearMonthDayDoublePickListener extends OnDatePickListener {

        void onDatePicked(String startYear, String startMonth, String startDay, String endYear, String endMonth, String endDay);

    }

    public interface OnYearMonthDoublePickListener extends OnDatePickListener {

        void onDatePicked(String startYear, String startMonth, String endYear, String endMonth);

    }

    public interface OnMonthDayDoublePickListener extends OnDatePickListener {

        void onDatePicked(String startMonth, String startDay, String endMonth, String endDay);
    }

    public interface OnYearMonthDayRangePickListener extends OnDatePickListener {

        void onDatePicked(String year, String month, String day);

    }

    public interface OnYearMonthRangePickListener extends OnDatePickListener {

        void onDatePicked(String year, String month);

    }

    public interface OnMonthDayRangePickListener extends OnDatePickListener {

        void onDatePicked(String month, String day);
    }


}
