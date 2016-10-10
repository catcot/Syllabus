package me.aufe.syllabus.Fragments;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import me.aufe.syllabus.R;
import me.aufe.syllabus.Score;
import me.aufe.syllabus.ScoreAdapter;
import me.aufe.syllabus.ScoreData;

import static android.content.Context.MODE_PRIVATE;

public class Tab_2 extends Fragment {
    private ListView listView;
    private List<ScoreData> mData;
    private ScoreAdapter scoreAdapter;
    private boolean isReady=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_2, container, false);

        TextView tv = (TextView) rootView.findViewById(R.id.textView_tab5);
        tv.setText(StringData());

        if(isReady){

            getActivity().findViewById(R.id.sc_content).setVisibility(LinearLayout.VISIBLE);
            getActivity().findViewById(R.id.sc_load).setVisibility(LinearLayout.INVISIBLE);
        }
        return rootView;
    }

    public static String StringData(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String[] weeks = {"天","一","二","三","四","五","六"};
        return c.get(Calendar.YEAR) +"年"+ (c.get(Calendar.MONTH)+1) +"月"+ c.get(Calendar.DAY_OF_MONTH)+"日"+"  星期"+weeks[c.get(Calendar.DAY_OF_WEEK)-1];
    }

    private void initData() {

        SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
        String rf = pref.getString("score","");
        if(!("".equals(rf))){

            Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==1){
                        SharedPreferences pref = getActivity().getSharedPreferences("data",MODE_PRIVATE);
                        String rf = pref.getString("score","");
                        JsonParser jp = new JsonParser();
                        JsonObject jsonObject = (JsonObject) jp.parse(rf);
                        Gson gson = new Gson();
                        Score score = gson.fromJson(jsonObject.get("1"), Score.class);

                        mData = new ArrayList<ScoreData>();
                        ScoreData scoreData;
                        for(int i=1;i<score.getNum();i++){
                            jp = new JsonParser();
                            jsonObject = (JsonObject) jp.parse(rf);
                            score = gson.fromJson(jsonObject.get(i+""), Score.class);
                            scoreData = new ScoreData(score.getName(),"学分："+score.getCredit(),"分数："+score.getScore());
                            mData.add(scoreData);
                        }

                        scoreAdapter = new ScoreAdapter(getContext(),mData);
                        listView = (ListView) getActivity().findViewById(R.id.nsl);
                        listView.setAdapter(scoreAdapter);
                        isReady =  true;
                        getActivity().findViewById(R.id.sc_content).setVisibility(LinearLayout.VISIBLE);
                        getActivity().findViewById(R.id.sc_load).setVisibility(LinearLayout.INVISIBLE);
                    }
                }
            };
            Message message = new Message();
            message.what=1;
            handler.sendMessageDelayed(message,500);



        }else{
            RequestQueue mQueue = Volley.newRequestQueue(getContext());

            String sno=pref.getString("sno","");
            String url = "http://120.27.113.162/jwc/report.php?sno="+sno;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putString("score",response.toString());
                            editor.putBoolean("isReady",true);
                            editor.apply();
                            editor.commit();


                            JsonParser jp = new JsonParser();
                            JsonObject jsonObject = (JsonObject) jp.parse(response.toString());
                            Gson gson = new Gson();
                            Score score = gson.fromJson(jsonObject.get("1"), Score.class);

                            mData = new ArrayList<ScoreData>();
                            ScoreData scoreData;
                            for(int i=1;i<score.getNum();i++){
                                jp = new JsonParser();
                                jsonObject = (JsonObject) jp.parse(response.toString());
                                score = gson.fromJson(jsonObject.get(i+""), Score.class);
                                scoreData = new ScoreData(score.getName(),"学分："+score.getCredit(),"分数："+score.getScore());
                                mData.add(scoreData);
                            }

                            scoreAdapter = new ScoreAdapter(getContext(),mData);
                            listView = (ListView) getActivity().findViewById(R.id.nsl);
                            listView.setAdapter(scoreAdapter);
                            isReady =  true;
                            getActivity().findViewById(R.id.sc_content).setVisibility(LinearLayout.VISIBLE);
                            getActivity().findViewById(R.id.sc_load).setVisibility(LinearLayout.INVISIBLE);

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            // TODO Auto-generated method stub
                            if("com.android.volley.TimeoutError".equals(error.toString())){
                                Toast.makeText(getContext(),"网络连接超时,成绩获取失败",Toast.LENGTH_LONG).show();
                                TextView tv = (TextView) getActivity().findViewById(R.id.tv_loading);
                                tv.setText("网络连接超时,成绩获取失败");
                            }else{
                                Toast.makeText(getContext(),"请检查网络连接",Toast.LENGTH_LONG).show();
                                TextView tv = (TextView) getActivity().findViewById(R.id.tv_loading);
                                tv.setText("请检查网络连接");
                            }
                        }
                    });
            mQueue.add(jsObjRequest);
        }


    }
}
