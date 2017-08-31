package com.lckiss.rangedatepicker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.lckiss.rangedatepicker.lib.datepicker.DatePickerOne;
import com.lckiss.rangedatepicker.lib.datepicker.DateRangePicker;
import com.lckiss.rangedatepicker.lib.filepicker.FilePicker;

import java.util.Calendar;

import static android.os.Build.VERSION_CODES.M;
import static com.lckiss.rangedatepicker.lib.datepicker.DateRangePicker.YEAR_MONTH_DAY;

public class MainActivity extends AppCompatActivity {
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = Calendar.getInstance();
    }


    public void pickerOne(View v) {
        //选择器初始化
        DatePickerOne picker = new DatePickerOne(this);
        picker.setRange(2000, 2030);
        picker.setTextSize(16);
        picker.setGravity(Gravity.CENTER);
        picker.setSelectedItem(year(), month(), day());
        picker.setOnDatePickListener(new DatePickerOne.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String yearStart, String monthStart, String dayStart, String yearEnd, String monthEnd, String dayEnd) {
                String dateStart = yearStart + "-" + monthStart + "-" + dayStart;
                String dateEnd = yearEnd + "-" + monthEnd + "-" + dayEnd;
                showToast("起始日期" + dateStart + "\n" + "截止日期" + dateEnd);
            }
        });
        picker.show();

    }

    public void pickerTwo(View v) {
        //初始化选择器
        DateRangePicker picker = new DateRangePicker(this, YEAR_MONTH_DAY, true);
        //选择器
        picker.setGravity(Gravity.CENTER);
        picker.setDateRangeStart(1997, 1, 1);
        picker.setDateRangeEnd(2030, 12, 30);
        picker.setTextSize(16);
        picker.setSelectedItem(year(), month(), day());
        picker.setSelectedSecondItem(year(), month(), day());
        picker.setOnDatePickListener(new DateRangePicker.OnYearMonthDayDoublePickListener() {
            @Override
            public void onDatePicked(String startYear, String startMonth, String startDay, String endYear, String endMonth, String endDay) {
                String dateStart = startYear + "-" + startMonth + "-" + startDay;
                String dateEnd = endYear + "-" + endMonth + "-" + endDay;
                showToast("起始日期" + dateStart + "\n" + "截止日期" + dateEnd);
            }
        });
        picker.show();
    }

    public void pickerThree(View v) {
//        也可以使用DatePicker实现
        //初始化选择器
        DateRangePicker picker = new DateRangePicker(this, YEAR_MONTH_DAY, false);
        //选择器
        picker.setGravity(Gravity.CENTER);
        picker.setDateRangeStart(1997, 1, 1);
        picker.setDateRangeEnd(2030, 12, 30);
        picker.setTextSize(16);
        picker.setSelectedItem(year(), month(), day());
        picker.setOnDatePickListener(new DateRangePicker.OnYearMonthDayRangePickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                String date = year + "-" + month + "-" + day;
                showToast("日期" + date);
            }
        });
        picker.show();
    }


    public void pickerFour(View v) {
        if (Build.VERSION.SDK_INT >= M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                showFilePicker();
            }else {
                showFilePicker();
            }
        }else {
            showFilePicker();
        }
    }

   private void showFilePicker(){
       FilePicker picker = new FilePicker(this, FilePicker.FILE);
       picker.setShowHideDir(false);
       picker.setShowHomeDir(true);
       picker.setShowUpDir(true);
       picker.setGravity(Gravity.CENTER);
       String rootPath=Environment.getExternalStorageDirectory()+"";
       picker.setRootPath(rootPath);
       picker.setAllowExtensions(new String[]{".csv"});
       picker.setOnFilePickListener(new FilePicker.OnFilePickListener() {
           @Override
           public void onFilePicked(String currentPath) {
               showToast(currentPath);
           }
       });
       picker.show();
   }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private int year() {
        return calendar.get(Calendar.YEAR);
    }

    private int month() {
        return calendar.get(Calendar.MONTH);
    }

    private int day() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
}
