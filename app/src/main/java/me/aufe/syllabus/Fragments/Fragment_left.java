package me.aufe.syllabus.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import java.util.HashMap;
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

public class Fragment_left extends Fragment {
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.pager_left,container,false);
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient mOkHttpClient = new OkHttpClient();
                        SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
                        final Request request = new Request.Builder()
                                .url("http://www.aufe.me/jwc/table_all.php?sno="+pref.getString("sno","")+"&pwd="+pref.getString("pwd",""))
                                .build();

                        Call call = mOkHttpClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                LinearLayout l = (LinearLayout)getActivity().findViewById(R.id.activity_main);
                                Snackbar snackbar = Snackbar.make(l, "数据更新失败！", 1000);
                                snackbar.getView().setBackgroundColor(0xFFD34949);
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
                                LinearLayout linearLayout = (LinearLayout)getActivity().findViewById(R.id.activity_main);
                                Snackbar snackbar = Snackbar.make(linearLayout, "数据更新成功！", 1000);
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
        String[] weekday = {"SUN","MON","TUE","WED","THU","FRI","SAT"};
        String re = pref.getString(weekday[c.get(Calendar.DAY_OF_WEEK)-1],"1");

        List<CourseData> mData = new ArrayList<>();

        JsonParser jp = new JsonParser();
        JsonObject jsonObject = (JsonObject) jp.parse(re);

        Course course;
        CourseData courseData;
        Gson gson = new Gson();
        String[] week =  {"one","two","three","four","five"};
        for(int i=0;i<5;i++){
            course = gson.fromJson(jsonObject.get(week[i]), Course.class);
            if(course!=null){
                courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
                mData.add(courseData);
            }
        }
        CourseAdapter courseAdapter = new CourseAdapter(getContext(),mData);
        ListView listView = (ListView) getActivity().findViewById(R.id.listView);
        listView.setAdapter(courseAdapter);
    }

    public String praseCourseTime(String s){
        HashMap<String,String> hashmap = new HashMap<String,String>();
        hashmap.put("12","上午 7:30 - 9:05");
        hashmap.put("32","上午 9:35 - 11:10");
        hashmap.put("33","上午 9:35 - 12:00");
        hashmap.put("62","下午 13:30 - 15:05");
        hashmap.put("82","下午 15:35 - 17:10");
        hashmap.put("83","下午 15:35 - 16:00");
        hashmap.put("112","晚上 19:00 - 20:35");
        hashmap.put("113","晚上 19:00 - 21:25");
        hashmap.put("14","上午 07:30 - 11:10");
        hashmap.put("15","上午 07:30 - 12:00");
        hashmap.put("64","下午 13:30 - 17:10");
        hashmap.put("65","下午 13:30 - 18:00");
        return hashmap.get(s);
    }
}
