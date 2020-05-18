package com.social.englishclass;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.firebase.database.annotations.Nullable;
import com.social.englishclass.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;

public class level extends AppCompatActivity implements View.OnClickListener {


private String current_lv;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
//현재 보여줄 탭 번호 받기
        Intent intent = getIntent();
        current_lv = intent.getStringExtra("lv_num");
//뷰매칭


//탭 화면구성
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(5); //탭은 보통 3개까지 로드 되고 죽지만 제한을 5개로 늘려준다
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(Integer.parseInt(current_lv));  // 현재 보여줄 탭 세팅
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);





//        FloatingActionButton fab = findViewById(R.id.fab);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }


    @Override
    public void onClick(View v) {

    }
}