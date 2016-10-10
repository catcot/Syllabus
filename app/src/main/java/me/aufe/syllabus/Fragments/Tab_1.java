package me.aufe.syllabus.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import me.aufe.syllabus.Course;
import me.aufe.syllabus.CourseAdapter;
import me.aufe.syllabus.CourseData;
import me.aufe.syllabus.R;
public class Tab_1 extends Fragment {
    private ListView listView;
    private List<CourseData> mData;
    private CourseAdapter courseAdapter;
    FloatingActionButton floatingActionButton;
    CalendarView calendarView;
    String m =  null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RequestQueue mQueue = Volley.newRequestQueue(getContext());



        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        String sno="20143461";
        String pwd="REWQNK";

        int ww = getWeek(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH));
        String url1 = "http://120.27.113.162/jwc/table_today.php?sno="+sno+"&pwd="+pwd+"&w="+ww;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mData=null;
                        mData = new ArrayList<CourseData>();
                        CourseData courseData;
                        JsonParser jp = new JsonParser();
                        JsonObject jsonObject = (JsonObject) jp.parse(response.toString());
                        Gson gson = new Gson();
                        Course course;

                        course = gson.fromJson(jsonObject.get("one"), Course.class);
                        if(course!=null){
                            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
                            mData.add(courseData);
                        }
                        course = gson.fromJson(jsonObject.get("two"), Course.class);
                        if(course!=null){
                            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
                            mData.add(courseData);
                        }
                        course = gson.fromJson(jsonObject.get("three"), Course.class);
                        if(course!=null){
                            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
                            mData.add(courseData);
                        }
                        course = gson.fromJson(jsonObject.get("four"), Course.class);
                        if(course!=null){
                            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
                            mData.add(courseData);
                        }
                        course = gson.fromJson(jsonObject.get("five"), Course.class);
                        if(course!=null){
                            courseData = new CourseData(course.getName(),praseCourseTime(course.getOrder()+course.getLength()),course.getBuilding()+course.getRoom());
                            mData.add(courseData);
                        }
                        courseAdapter = new CourseAdapter(getContext(),mData);
                        listView = (ListView) getActivity().findViewById(R.id.listView);
                        listView.setAdapter(courseAdapter);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        if("com.android.volley.TimeoutError".equals(error.toString())){
                            Toast.makeText(getContext(),"网络连接超时,课表加载失败",Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();

                    }
                });
        mQueue.add(jsObjRequest);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1,container,false);
        MaterialCalendarView materialCalendarView = (MaterialCalendarView) v.findViewById(R.id.calendarView);
        materialCalendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        return v;
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
        Calendar calendar = Calendar.getInstance();//获得一个日历
        calendar.set(year, month-1, dayOfMonth);//设置当前时间,月份是从0月开始计算
        int a =  calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(a==0){
            a=7;
        }
        return a;
    }
}
