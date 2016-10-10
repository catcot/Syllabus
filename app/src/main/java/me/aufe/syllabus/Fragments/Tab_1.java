package me.aufe.syllabus.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import me.aufe.syllabus.Course;
import me.aufe.syllabus.CourseAdapter;
import me.aufe.syllabus.CourseData;
import me.aufe.syllabus.R;

import static android.content.Context.MODE_PRIVATE;

public class Tab_1 extends Fragment {
    private ListView listView;
    private List<CourseData> mData;
    private CourseAdapter courseAdapter;

    private final int REFRESH_COMPLETE = 5;

    @Override
    public void onResume() {
        super.onResume();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        MaterialCalendarView materialCalendarView = (MaterialCalendarView) getActivity().findViewById(R.id.calendarView);
        materialCalendarView.setDateSelected(materialCalendarView.getSelectedDate().getCalendar(),false);
        materialCalendarView.setDateSelected(c,true);
        changeList(c);

        getActivity().findViewById(R.id.app_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                MaterialCalendarView materialCalendarView = (MaterialCalendarView) getActivity().findViewById(R.id.calendarView);
                materialCalendarView.setDateSelected(materialCalendarView.getSelectedDate().getCalendar(),false);
                materialCalendarView.setDateSelected(c,true);
                changeList(c);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.tab_1,container,false);
        MaterialCalendarView materialCalendarView = (MaterialCalendarView) v.findViewById(R.id.calendarView);
        materialCalendarView.setSelectionColor(0xFF1A89F3);
        materialCalendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        materialCalendarView.setDateSelected(c,true);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Calendar c = date.getCalendar();
                c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

                SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
                String re="";

                changeList(c);

                //Toast.makeText(getActivity(),c.get(Calendar.MONTH)+1+"-"+c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.DAY_OF_WEEK)-1),Toast.LENGTH_LONG).show();


            }
        });
        final PullRefreshLayout layout = (PullRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==REFRESH_COMPLETE){
                    layout.setRefreshing(false);
                }
            }
        };
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(REFRESH_COMPLETE,3000);
            }
        });



        SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
        String re= pref.getString("aa","1");
        mData=null;
        mData = new ArrayList<CourseData>();
        CourseData courseData;
        JsonParser jp = new JsonParser();
        JsonObject jsonObject = (JsonObject) jp.parse(re);
        Gson gson = new Gson();
        Course course;

        course = gson.fromJson(jsonObject.get("one"), Course.class);
        if(course!=null){
            Log.d("course",course.getName());
            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
            mData.add(courseData);
        }
        course = gson.fromJson(jsonObject.get("two"), Course.class);
        if(course!=null){
            Log.d("course",course.getName());
            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
            mData.add(courseData);
        }
        course = gson.fromJson(jsonObject.get("three"), Course.class);
        if(course!=null){
            Log.d("course",course.getName());
            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
            mData.add(courseData);
        }
        course = gson.fromJson(jsonObject.get("four"), Course.class);
        if(course!=null){
            Log.d("course",course.getName());
            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
            mData.add(courseData);
        }
        course = gson.fromJson(jsonObject.get("five"), Course.class);
        if(course!=null){
            Log.d("course",course.getName());
            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
            mData.add(courseData);
        }
        courseAdapter = new CourseAdapter(getContext(),mData);
        listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(courseAdapter);
        return v;
    }

    private void changeList(Calendar c) {
        SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
        String re="";
        switch (c.get(Calendar.DAY_OF_WEEK)-1){
            case 1:
                re= pref.getString("aa","1");
                break;
            case 2:
                re= pref.getString("bb","1");
                break;
            case 3:
                re= pref.getString("cc","1");
                break;
            case 4:
                re= pref.getString("dd","1");
                break;
            case 5:
                re= pref.getString("ee","1");
                break;
            case 6:
                re= pref.getString("ff","1");
                break;
            case 0:
                re= pref.getString("gg","1");
                break;

        }

        mData=null;
        mData = new ArrayList<CourseData>();
        CourseData courseData;
        JsonParser jp = new JsonParser();
        JsonObject jsonObject = (JsonObject) jp.parse(re);
        Gson gson = new Gson();
        Course course;

        course = gson.fromJson(jsonObject.get("one"), Course.class);
        if(course!=null){
            Log.d("course",course.getName());
            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
            mData.add(courseData);
        }
        course = gson.fromJson(jsonObject.get("two"), Course.class);
        if(course!=null){
            Log.d("course",course.getName());
            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
            mData.add(courseData);
        }
        course = gson.fromJson(jsonObject.get("three"), Course.class);
        if(course!=null){
            Log.d("course",course.getName());
            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
            mData.add(courseData);
        }
        course = gson.fromJson(jsonObject.get("four"), Course.class);
        if(course!=null){
            Log.d("course",course.getName());
            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
            mData.add(courseData);
        }
        course = gson.fromJson(jsonObject.get("five"), Course.class);
        if(course!=null){
            Log.d("course",course.getName());
            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
            mData.add(courseData);
        }
        courseAdapter = new CourseAdapter(getContext(),mData);
        listView = (ListView) getActivity().findViewById(R.id.listView);
        listView.setAdapter(courseAdapter);
    }

    public String praseCourseTime(String s){
        String result=null;
        switch (s){
            case "12":
                result = "上午 7:30 - 9:05";
                break;
            case "32":
                result = "上午 9:35 - 11:10";
                break;
            case "33":
                result = "上午 9:35 - 12:00";
                break;
            case "62":
                result = "下午 13:30 - 15:05";
                break;
            case "82":
                result = "下午 15:35 - 17:10";
                break;
            case "83":
                result = "下午 15:35 - 16:00";
                break;
            case "112":
                result = "晚上 19:00 - 20:35";
                break;
            case "113":
                result = "晚上 19:00 - 21:25";
                break;
            case "14":
                result = "上午 07:30 - 11:10";
                break;
            case "15":
                result = "上午 07:30 - 12:00";
                break;
            case "64":
                result = "下午 13:30 - 17:10";
                break;
            case "65":
                result = "下午 13:30 - 18:00";
        }
        return result;
    }

    public int getWeek(int year, int month, int dayOfMonth){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, dayOfMonth);
        int a =  calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(a==0){
            a=7;
        }
        return a;
    }
}
