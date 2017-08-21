package com.lckiss.rangedatepicker.lib.datepicker;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lckiss.rangedatepicker.lib.datepicker.WheelPicker;
import com.lckiss.rangedatepicker.lib.common.utils.DateUtils;
import com.lckiss.rangedatepicker.lib.datepicker.widget.WheelView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 日期选择器
 *
 * @author Modified by Lckiss
 * @version End017 /8/3
 */
public class DatePickerOne extends WheelPicker {
    /**
     * 年月日
     */
    private ArrayList<String> years = new ArrayList<String>();
    private ArrayList<String> months = new ArrayList<String>();
    private ArrayList<String> days = new ArrayList<String>();
    private OnDatePickListener onDatePickListener;
    private String yearLabel = "年", monthLabel = "月", dayLabel = "日";
    private int selectedYearIndex = 0, selectedMonthIndex = 0, selectedDayIndex = 0;
    private int selectedYearIndexEnd = 0, selectedMonthIndexEnd = 0, selectedDayIndexEnd = 0;

    /**
     * Instantiates a new Date picker.
     *
     * @param activity the activity
     */
    public DatePickerOne(Activity activity) {
        super(activity);
        for (int i = 2000; i <= 2050; i++) {
            years.add(String.valueOf(i));
        }
        for (int i = 1; i <= 12; i++) {
            months.add(DateUtils.fillZero(i));
        }
        for (int i = 1; i <= 31; i++) {
            days.add(DateUtils.fillZero(i));
        }
    }

    /**
     * Sets label.
     *
     * @param yearLabel  the year label
     * @param monthLabel the month label
     * @param dayLabel   the day label
     */
    public void setLabel(String yearLabel, String monthLabel, String dayLabel) {
        this.yearLabel = yearLabel;
        this.monthLabel = monthLabel;
        this.dayLabel = dayLabel;
    }

    /**
     * Sets range.
     *
     * @param startYear the start year
     * @param endYear   the end year
     */
    public void setRange(int startYear, int endYear) {
        years.clear();
        for (int i = startYear; i <= endYear; i++) {
            years.add(String.valueOf(i));
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
                return Integer.parseInt(lhsStr) - Integer.parseInt(rhsStr);
            }
        });
        if (index < 0) {
            index = 0;
        }
        return index;
    }

    /**
     * Sets selected item.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     */
    public void setSelectedItem(int year, int month, int day) {
        selectedYearIndex = findItemIndex(years, year);
        selectedMonthIndex = findItemIndex(months, month);
        selectedDayIndex = findItemIndex(days, day);
        selectedYearIndexEnd = selectedYearIndex;
        selectedMonthIndexEnd =selectedMonthIndex;
        selectedDayIndexEnd =selectedDayIndex;
    }

    /**
     * Sets selected item.
     *
     * @param yearOrMonth the year or month
     * @param monthOrDay  the month or day
     */
    public void setSelectedItem(int yearOrMonth, int monthOrDay) {
        selectedYearIndex = findItemIndex(years, yearOrMonth);
        selectedMonthIndex = findItemIndex(months, monthOrDay);
    }

    /**
     * Sets on date pick listener.
     *
     * @param listener the listener
     */
    public void setOnDatePickListener(OnDatePickListener listener) {
        this.onDatePickListener = listener;
    }

    @Override
    @NonNull
    protected View makeCenterView() {
        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        TextView beginText=new TextView(activity.getBaseContext());
        beginText.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        beginText.setText("起始日期:");
        beginText.setPadding(50,10,0,0);
        beginText.setTextSize(textSize);
        beginText.setTextColor(textColorFocus);
        root.addView(beginText);
        WheelView yearView = new WheelView(activity.getBaseContext());
        yearView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        yearView.setTextSize(textSize);
        yearView.setTextColor(textColorNormal, textColorFocus);
//        yearView.setLineVisible(lineVisible);
//        yearView.setLineColor(lineColor);
        yearView.setOffset(offset);
        layout.addView(yearView);
        TextView yearTextView = new TextView(activity);
        yearTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        yearTextView.setTextSize(textSize);
        yearTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(yearLabel)) {
            yearTextView.setText(yearLabel);
        }
        layout.addView(yearTextView);
        WheelView monthView = new WheelView(activity.getBaseContext());
        monthView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        monthView.setTextSize(textSize);
        monthView.setTextColor(textColorNormal, textColorFocus);
        monthView.setOffset(offset);
        layout.addView(monthView);
        TextView monthTextView = new TextView(activity);
        monthTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        monthTextView.setTextSize(textSize);
        monthTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(monthLabel)) {
            monthTextView.setText(monthLabel);
        }
        layout.addView(monthTextView);
        final WheelView dayView = new WheelView(activity.getBaseContext());
        dayView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        dayView.setTextSize(textSize);
        dayView.setTextColor(textColorNormal, textColorFocus);
        dayView.setOffset(offset);
        layout.addView(dayView);
        TextView dayTextView = new TextView(activity);
        dayTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        dayTextView.setTextSize(textSize);
        dayTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(dayLabel)) {
            dayTextView.setText(dayLabel);
        }
        layout.addView(dayTextView);
        if (!TextUtils.isEmpty(yearLabel)) {
            yearTextView.setText(yearLabel);
        }
        if (selectedYearIndex == 0) {
            yearView.setItems(years);
        } else {
            yearView.setItems(years, selectedYearIndex);
        }
        yearView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                selectedYearIndex = index;
                //需要根据年份及月份动态计算天数
                days.clear();
                int maxDays = DateUtils.calculateDaysInMonth(stringToYearMonthDay(years.get(selectedYearIndex)), stringToYearMonthDay(months.get(selectedMonthIndex)));
                for (int i = 1; i <= maxDays; i++) {
                    days.add(DateUtils.fillZero(i));
                }
                if (selectedDayIndex >= maxDays) {
                    //年或月变动时，保持之前选择的日不动：如果之前选择的日是之前年月的最大日，则日自动为该年月的最大日
                    selectedDayIndex = days.size() - 1;
                }
                dayView.setItems(days, selectedDayIndex);
            }
        });
        if (!TextUtils.isEmpty(monthLabel)) {
            monthTextView.setText(monthLabel);
        }
        if (selectedMonthIndex == 0) {
            monthView.setItems(months);
        } else {
            monthView.setItems(months, selectedMonthIndex);
        }
        monthView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int selectedIndex) {
                selectedMonthIndex = selectedIndex;
                //年月日或年月模式下，需要根据年份及月份动态计算天数
                days.clear();
                int maxDays = DateUtils.calculateDaysInMonth(stringToYearMonthDay(years.get(selectedYearIndex)), stringToYearMonthDay(months.get(selectedMonthIndex)));
                for (int i = 1; i <= maxDays; i++) {
                    days.add(DateUtils.fillZero(i));
                }
                if (selectedDayIndex >= maxDays) {
                    //年或月变动时，保持之前选择的日不动：如果之前选择的日是之前年月的最大日，则日自动为该年月的最大日
                    selectedDayIndex = days.size() - 1;
                }
                dayView.setItems(days, selectedDayIndex);
            }
        });
        if (!TextUtils.isEmpty(dayLabel)) {
            dayTextView.setText(dayLabel);
        }
        if (selectedDayIndex == 0) {
            dayView.setItems(days);
        } else {
            dayView.setItems(days, selectedDayIndex);
        }
        dayView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int selectedIndex) {
                selectedDayIndex = selectedIndex;
            }
        });
        root.addView(layout);

        TextView endText=new TextView(activity.getBaseContext());
        endText.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        endText.setText("截止日期:");
        endText.setTextSize(textSize);
        endText.setTextColor(textColorFocus);
        endText.setPadding(50,20,0,0);
        root.addView(endText);
        LinearLayout layoutBottom = new LinearLayout(activity);
        layoutBottom.setOrientation(LinearLayout.HORIZONTAL);
        layoutBottom.setGravity(Gravity.CENTER);
        WheelView yearViewBottom = new WheelView(activity.getBaseContext());
        yearViewBottom.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        yearViewBottom.setTextSize(textSize);
        yearViewBottom.setTextColor(textColorNormal, textColorFocus);
        yearViewBottom.setOffset(offset);
        layoutBottom.addView(yearViewBottom);

        yearTextView = new TextView(activity);
        yearTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        yearTextView.setTextSize(textSize);
        yearTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(yearLabel)) {
            yearTextView.setText(yearLabel);
        }
        layoutBottom.addView(yearTextView);
        WheelView monthViewBottom = new WheelView(activity.getBaseContext());
        monthViewBottom.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        monthViewBottom.setTextSize(textSize);
        monthViewBottom.setTextColor(textColorNormal, textColorFocus);
        monthViewBottom.setOffset(offset);
        layoutBottom.addView(monthViewBottom);
        monthTextView = new TextView(activity);
        monthTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        monthTextView.setTextSize(textSize);
        monthTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(monthLabel)) {
            monthTextView.setText(monthLabel);
        }
        layoutBottom.addView(monthTextView);
        final WheelView dayViewBottom = new WheelView(activity.getBaseContext());
        dayViewBottom.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        dayViewBottom.setTextSize(textSize);
        dayViewBottom.setTextColor(textColorNormal, textColorFocus);
        dayViewBottom.setOffset(offset);
        layoutBottom.addView(dayViewBottom);
        dayTextView = new TextView(activity);
        dayTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        dayTextView.setTextSize(textSize);
        dayTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(dayLabel)) {
            dayTextView.setText(dayLabel);
        }
        layoutBottom.addView(dayTextView);
        if (!TextUtils.isEmpty(yearLabel)) {
            yearTextView.setText(yearLabel);
        }
        if (selectedYearIndexEnd == 0) {
            yearViewBottom.setItems(years);
        } else {
            yearViewBottom.setItems(years, selectedYearIndexEnd);
        }
        yearViewBottom.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected( int selectedIndex) {
                selectedYearIndexEnd = selectedIndex;
                //需要根据年份及月份动态计算天数
                days.clear();
                int maxDays = DateUtils.calculateDaysInMonth(stringToYearMonthDay(years.get(selectedYearIndex)), stringToYearMonthDay(months.get(selectedMonthIndex)));
                for (int i = 1; i <= maxDays; i++) {
                    days.add(DateUtils.fillZero(i));
                }
                if (selectedDayIndexEnd >= maxDays) {
                    //年或月变动时，保持之前选择的日不动：如果之前选择的日是之前年月的最大日，则日自动为该年月的最大日
                    selectedDayIndexEnd = days.size() - 1;
                }
                dayViewBottom.setItems(days, selectedDayIndexEnd);
            }
        });
        if (!TextUtils.isEmpty(monthLabel)) {
            monthTextView.setText(monthLabel);
        }
        if (selectedMonthIndexEnd == 0) {
            monthViewBottom.setItems(months);
        } else {
            monthViewBottom.setItems(months, selectedMonthIndexEnd);
        }
        monthViewBottom.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int selectedIndex) {
                selectedMonthIndexEnd = selectedIndex;
                //年月日或年月模式下，需要根据年份及月份动态计算天数
                days.clear();
                int maxDays = DateUtils.calculateDaysInMonth(stringToYearMonthDay(years.get(selectedYearIndex)),stringToYearMonthDay(months.get(selectedMonthIndex)));
                for (int i = 1; i <= maxDays; i++) {
                    days.add(DateUtils.fillZero(i));
                }
                if (selectedDayIndexEnd >= maxDays) {
                    //年或月变动时，保持之前选择的日不动：如果之前选择的日是之前年月的最大日，则日自动为该年月的最大日
                    selectedDayIndexEnd = days.size() - 1;
                }
                dayViewBottom.setItems(days, selectedDayIndexEnd);
            }
        });
        if (!TextUtils.isEmpty(dayLabel)) {
            dayTextView.setText(dayLabel);
        }
        if (selectedDayIndexEnd == 0) {
            dayViewBottom.setItems(days);
        } else {
            dayViewBottom.setItems(days, selectedDayIndexEnd);
        }
        dayViewBottom.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected( int selectedIndex) {
                selectedDayIndexEnd = selectedIndex;
            }
        });
        root.addView(layoutBottom);
        return root;
    }

    private int stringToYearMonthDay(String text) {
        if (text.startsWith("0")) {
            //截取掉前缀0以便转换为整数
            text = text.substring(1);
        }
        return Integer.parseInt(text);
    }

    @Override
    protected void onSubmit() {
        if (onDatePickListener != null) {
            String year = getSelectedYear();
            String month = getSelectedMonth();
            String day = getSelectedDay();
            String yearEnd = getSelectedYearEnd();
            String monthEnd = getSelectedMonthEnd();
            String dayEnd = getSelectedDayEnd();
            ((OnYearMonthDayPickListener) onDatePickListener).onDatePicked(year, month, day,yearEnd, monthEnd, dayEnd);
        }
    }

    /**
     * Gets selected year.
     *
     * @return the selected year
     */
    public String getSelectedYear() {
        return years.get(selectedYearIndex);
    }

    /**
     * Gets selected month.
     *
     * @return the selected month
     */
    public String getSelectedMonth() {
        return months.get(selectedMonthIndex);
    }

    /**
     * Gets selected day.
     *
     * @return the selected day
     */
    public String getSelectedDay() {
        return days.get(selectedDayIndex);
    }
    /**
     * Gets selected year.
     *
     * @return the selected year
     */
    public String getSelectedYearEnd() {
        return years.get(selectedYearIndexEnd);
    }

    /**
     * Gets selected month.
     *
     * @return the selected month
     */
    public String getSelectedMonthEnd() {
        return months.get(selectedMonthIndexEnd);
    }

    /**
     * Gets selected day.
     *
     * @return the selected day
     */
    public String getSelectedDayEnd() {
        return days.get(selectedDayIndexEnd);
    }

    /**
     * The interface On date pick listener.
     */
    protected interface OnDatePickListener {

    }

    /**
     * The interface On year month day pick listener.
     */
    public interface OnYearMonthDayPickListener extends OnDatePickListener {

        /**
         * On date picked.
         *
         * @param year  the year
         * @param month the month
         * @param day   the day
         */
        void onDatePicked(String year, String month, String day,String yearEnd, String monthEnd, String dayEnd);

    }



}