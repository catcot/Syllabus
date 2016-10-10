package me.aufe.syllabus.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import me.aufe.syllabus.Course;
import me.aufe.syllabus.CourseAdapter;
import me.aufe.syllabus.CourseData;
import me.aufe.syllabus.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
                changeList(c);

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
                SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
                final String username = pref.getString("sno","");
                final String password = pref.getString("pwd","");


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient mOkHttpClient = new OkHttpClient();
                        final Request request = new Request.Builder()
                                .url("http://www.aufe.me/jwc/table_all.php?sno="+username+"&pwd="+password)
                                .build();

                        Call call = mOkHttpClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                LinearLayout l = (LinearLayout)getActivity().findViewById(R.id.activity_main);
                                Snackbar snackbar = Snackbar.make(l, "Regresh Failed", 1000);
                                snackbar.getView().setBackgroundColor(0xFF00BFFF);
                                snackbar.show();
                                handler.sendEmptyMessage(REFRESH_COMPLETE);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                String[] r = response.body().string().split("\\|");
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("data",MODE_PRIVATE).edit();
                                String[] list = {"MON","TUE","WED","THU","FRI","SAT","SUN"};
                                for(int i=0;i<7;i++){
                                    editor.putString(list[i],r[i]);
                                }
                                editor.apply();
                                editor.commit();
                                LinearLayout l = (LinearLayout)getActivity().findViewById(R.id.activity_main);
                                Snackbar snackbar = Snackbar.make(l, "Regresh Complete", 1000);
                                snackbar.getView().setBackgroundColor(0xFF00BFFF);
                                snackbar.show();
                                handler.sendEmptyMessage(REFRESH_COMPLETE);
                            }
                        });
                    }
                }).start();
            }
        });
        return v;
    }

    private void changeList(Calendar c) {
        SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
        String re="";
        switch (c.get(Calendar.DAY_OF_WEEK)-1){
            case 1:
                re= pref.getString("MON","1");
                break;
            case 2:
                re= pref.getString("TUE","1");
                break;
            case 3:
                re= pref.getString("WED","1");
                break;
            case 4:
                re= pref.getString("THU","1");
                break;
            case 5:
                re= pref.getString("FRI","1");
                break;
            case 6:
                re= pref.getString("SAT","1");
                break;
            case 0:
                re= pref.getString("SUN","1");
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
}
