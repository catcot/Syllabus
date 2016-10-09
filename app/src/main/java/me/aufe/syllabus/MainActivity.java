package me.aufe.syllabus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

import me.aufe.syllabus.Fragments.Tab_1;
import me.aufe.syllabus.Fragments.Tab_2;
import me.aufe.syllabus.Fragments.Tab_3;
import me.aufe.syllabus.Fragments.Tab_4;

public class MainActivity extends FragmentActivity {

    private ViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentPagerAdapter fragmentPagerAdapter;

    private List<TabItem> tabs = new ArrayList<TabItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        fragments.add(new Tab_1());
        fragments.add(new Tab_2());
        fragments.add(new Tab_3());
        fragments.add(new Tab_4());

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };

        TabItem one = (TabItem) findViewById(R.id.tab_1);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabClicked(v);
            }
        });
        tabs.add(one);
        TabItem two = (TabItem) findViewById(R.id.tab_2);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabClicked(v);
            }
        });
        tabs.add(two);
        TabItem three = (TabItem) findViewById(R.id.tab_3);
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabClicked(v);
            }
        });
        tabs.add(three);
        TabItem four = (TabItem) findViewById(R.id.tab_4);
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabClicked(v);
            }
        });
        tabs.add(four);
        viewPager.setAdapter(fragmentPagerAdapter);



        one.setIconAlpha(1.0f);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(positionOffset>0){
                    TabItem left = tabs.get(position);
                    TabItem right = tabs.get(position+1);
                    left.setIconAlpha(1-positionOffset);
                    right.setIconAlpha(positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        SlidingMenu slidingMenu =new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        slidingMenu.setFadeDegree(0.5f);
        slidingMenu.setMenu(R.layout.menu);
        slidingMenu.setBehindOffset(200);
        slidingMenu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT,true);
    }

    private void tabClicked(View v) {
        resetOthers();
        switch (v.getId()){
            case R.id.tab_1:
                tabs.get(0).setIconAlpha(1.0f);
                viewPager.setCurrentItem(0,false);
                break;
            case R.id.tab_2:
                tabs.get(1).setIconAlpha(1.0f);
                viewPager.setCurrentItem(1,false);
                break;
            case R.id.tab_3:
                tabs.get(2).setIconAlpha(1.0f);
                viewPager.setCurrentItem(2,false);
                break;
            case R.id.tab_4:
                tabs.get(3).setIconAlpha(1.0f);
                viewPager.setCurrentItem(3,false);
                break;
        }
    }

    private void resetOthers() {
        for(int i=0;i<tabs.size();i++){
            tabs.get(i).setIconAlpha(0);
        }
    }
}
