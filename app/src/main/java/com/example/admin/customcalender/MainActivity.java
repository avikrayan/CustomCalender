package com.example.admin.customcalender;

import android.app.ProgressDialog;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button selectedDayMonthYearButton;
    private Button currentMonth;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;
    private GridCellAdapter_ adapter;
    private Calendar _calendar;
    private int month, year;
    private static final String dateTemplate = "MMMM-yyyy";
    String flag = "abc";
    String date_month_year;
    ProgressDialog progressDialog;
    private String DATA_URL;
    HashMap<String, AttendanceDTO> date_list = null;
    HashMap<String, MonthDTO> arraylist_month = null;
    TextView txt_absent_count, txt_present_count, txt_leave_count;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        date_list = new HashMap<>();
        arraylist_month = new HashMap<>();
        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);

        txt_absent_count = (TextView) findViewById(R.id.txt_absent_count);
        txt_present_count = (TextView) findViewById(R.id.txt_present_count);
        txt_leave_count = (TextView) findViewById(R.id.txt_leave_count);

        //selectedDayMonthYearButton = (Button) this.findViewById(R.id.selectedDayMonthYear);
        //selectedDayMonthYearButton.setText("Select Date");

        prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (Button) this.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));

        nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (GridView) this.findViewById(R.id.calendar);
        getCalenderDate();

    }

    private void setSummary(int month, int year) {
        MonthDTO summaryDto = arraylist_month.get((month) + "-" + year);
        //Log.e("arraylist_month DATE ==>> ", (month) + "-" + year);

        String present_count = (summaryDto != null) ? summaryDto.getPresent() : "";
        String absent_count = (summaryDto != null) ? summaryDto.getAbsent() : "";
        String leave_count = (summaryDto != null) ? summaryDto.getLeave() : "";
        Log.e("Leave_count ==>> ", leave_count);
        Log.e("Present_count ==>> ", present_count);
        txt_present_count.setText(present_count);
        txt_absent_count.setText(absent_count);
        txt_leave_count.setText(leave_count);
    }

    private void getCalenderDate() {
        DATA_URL = getResources().getString(R.string.baseUrl) + "parentAttendanceDataLoader";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, DATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(CircularParent.this, response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject reader = new JSONObject(response);
                    JSONObject responseJSON = reader.getJSONObject("response");
                    String resultSuccess = responseJSON.getString("result");
                    JSONArray json_calender_array = responseJSON.getJSONArray("Calender");
                    for (int i = 0; i < json_calender_array.length(); i++) {
                        JSONObject object_calender = json_calender_array.getJSONObject(i);
                        String date = object_calender.getString("date");
                        String status = object_calender.getString("status");
                        Log.e("Date ==>> ", date);
                        date_list.put(date, new AttendanceDTO(date, status));
                    }
                    JSONArray jsonarray_summary = responseJSON.getJSONArray("Summary");
                    for (int j = 0; j < jsonarray_summary.length(); j++) {
                        JSONObject object_calender_summary = jsonarray_summary.getJSONObject(j);
                        String month = object_calender_summary.getString("month");
                        String present_count = object_calender_summary.getString("present");
                        String absent_count = object_calender_summary.getString("absent");
                        String leave_count = object_calender_summary.getString("leave");
                        Log.e("Present_Json ==>> ", present_count);
                        arraylist_month.put(month, new MonthDTO(month, present_count, absent_count, leave_count));
                        //Log.e("ArrayList_Json_month ==>> ", Integer.toString(arraylist_month.size()));
                    }
                    progressDialog.dismiss();
                    adapter = new GridCellAdapter_(getApplicationContext(), R.id.calendar_day_gridcell, month, year);
                    calendarView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    setSummary(month, year);

                } catch (final JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("schoolId", "1");
                params.put("studentId", "2");
                params.put("classId", "3");
                params.put("sectionId", "4");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        progressDialog = ProgressDialog.show(MainActivity.this, "Please Wait...", null, true, true);
        //progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.bookloadinganim));
        progressDialog.setMessage("Fetching Your Data ! Please wait...!");
        progressDialog.setCancelable(false);
    }


    private void setGridCellAdapterToDate(int month, int year) {
        adapter = new GridCellAdapter_(getApplicationContext(), R.id.calendar_day_gridcell, month, year);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(DateFormat.format(dateTemplate, _calendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == prevMonth) {
            if (month <= 1) {
                month = 12;
                year--;
            } else
                month--;
            setGridCellAdapterToDate(month, year);
        }
        if (v == nextMonth) {
            if (month > 11) {
                month = 1;
                year++;
            } else
                month++;
            setGridCellAdapterToDate(month, year);
        }


        setSummary(month, year);


    }


    // ///////////////////////////////////////////////////////////////////////////////////////
    // Inner Class
    public class GridCellAdapter_ extends BaseAdapter implements View.OnClickListener {
        private final Context _context;

        private final List<String> list;
        private static final int DAY_OFFSET = 1;
        private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        private int daysInMonth;
        private int currentDayOfMonth;
        private int currentWeekDay;
        private Button gridcell;
        private TextView num_events_per_day;
        private final HashMap<String, Integer> eventsPerMonthMap;

        // Days in Current Month
        public GridCellAdapter_(Context context, int textViewResourceId, int month, int year) {
            super();
            this._context = context;
            this.list = new ArrayList<String>();
            Calendar calendar = Calendar.getInstance();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

            // Print Month
            printMonth(month, year);

            // Find Number of Events
            eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
        }

        private String getMonthAsString(int i) {
            return months[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return daysOfMonth[i];
        }

        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        private void printMonth(int mm, int yy) {
            int trailingSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;

            int currentMonth = mm - 1;
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);


            // Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

            if (currentMonth == 11) {
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
            } else if (currentMonth == 0) {
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 1;
            } else {
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            }

            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            if (cal.isLeapYear(cal.get(Calendar.YEAR)) && mm == 1) {
                ++daysInMonth;
            }

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
                list.add(String.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET) + i) + "-GREY" + "-" + getMonthAsString(prevMonth) + "-" + prevYear);
            }

            // Current Month Days
            for (int i = 1; i <= daysInMonth; i++) {
                if (i == getCurrentDayOfMonth())
                    list.add(String.valueOf(i) + "-BLUE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
                else
                    list.add(String.valueOf(i) + "-WHITE" + "-" + getMonthAsString(currentMonth) + "-" + yy);
            }

            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++) {
                list.add(String.valueOf(i + 1) + "-GREY" + "-" + getMonthAsString(nextMonth) + "-" + nextYear);
            }
        }

        private HashMap<String, Integer> findNumberOfEventsPerMonth(int year, int month) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            return map;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.e("MESSAGE ==>> ", "getView Called");
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.calender_day_gridcell, parent, false);
            }

            // Get a reference to the Day gridcell
            gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
            num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
            //num_events_per_day.setText(list.get(position));
            //System.out.println("Day_response ==>> " + date);
            gridcell.setOnClickListener(this);

            // ACCOUNT FOR SPACING
            String calDate = list.get(position);
            String[] day_color = calDate.split("-");
            String theday = day_color[0];
            //String theday_response = day_color[0];
            String themonth = day_color[2];
            String theyear = day_color[3];

            if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
                if (eventsPerMonthMap.containsKey(theday)) {
                    num_events_per_day = (TextView) row.findViewById(R.id.num_events_per_day);
                    Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
                    num_events_per_day.setText(numEvents);
                    //Log.e("Day_response ==>> ", date);
                }
            }

            // Set the Day GridCell
            gridcell.setText(theday);

            gridcell.setTag(theday + "-" + themonth + "-" + theyear);

            if (day_color[1].equals("GREY"))
                gridcell.setTextColor(Color.LTGRAY);

            if (day_color[1].equals("WHITE"))
                gridcell.setTextColor(Color.BLACK);

            if (day_color[1].equals("BLUE"))
                gridcell.setTextColor(getResources().getColor(R.color.colorPrimary));


            Log.e("date_list SIZE ==>> ", date_list.toString());
            Log.e("date_list DATE ==>> ", theday + "/" + themonth + "/" + theyear);
            AttendanceDTO dto = date_list.get(theday + "/" + themonth + "/" + theyear);
            String status = (dto != null) ? dto.getStatue() : "";
            Log.e("Status ==>> ", status);

            if (status.equals("present")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    gridcell.setBackground(getResources().getDrawable(R.drawable.green32dp));
                }
                // Log.e("ArrayList_present_size ==>> ", Integer.toString(date_list.size()));
            } else if (status.equals("absent")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    gridcell.setBackground(getResources().getDrawable(R.drawable.red32dp));
                }
                //Log.e("ArrayList_absent_size ==>> ", Integer.toString(date_list.size()));
            } else if (status.equals("leave")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    gridcell.setBackground(getResources().getDrawable(R.drawable.yellow32dp));
                }
            }
            return row;
        }

        @Override
        public void onClick(View view) {
            date_month_year = (String) view.getTag();
            flag = "Date selected ...";
            //selectedDayMonthYearButton.setText("Selected: " + date_month_year);
        }

        public int getCurrentDayOfMonth() {
            return currentDayOfMonth;
        }

        private void setCurrentDayOfMonth(int currentDayOfMonth) {
            this.currentDayOfMonth = currentDayOfMonth;
        }

        public void setCurrentWeekDay(int currentWeekDay) {
            this.currentWeekDay = currentWeekDay;
        }

        public int getCurrentWeekDay() {
            return currentWeekDay;
        }
    }
}
