package com.social.englishclass.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.social.englishclass.R;
import com.social.englishclass.SelectLesson;
import com.social.englishclass.level;

import java.io.File;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    public static int count;


    public SectionsPagerAdapter(Context context, FragmentManager fm) {
//        super(fm);
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(level.lesson_type.equals("let") || level.lesson_type.equals("rt1") ){
            LetsreadFragment fragment = LetsreadFragment.newInstance(position);
            return fragment;
        }else {

            PlaceholderFragment fragment = null;
            fragment = PlaceholderFragment.newInstance(position + 1);

            Log.e("MyTag", "프래그먼트 포지션   : " + fragment);
            return fragment;
        }



        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        for(int i=0; i<getCount();i++){
            if(level.lesson_type.equals("let_lv2")) {
                if (position == i) return "Type " + (i + 2);
            }else if(level.lesson_type.equals("let") || level.lesson_type.equals("rt1") ){
                if (position == i) return ""+(i + 1);
            }else{
            if(position == i) return "Type "+(i+1);
            }
        }
//        if (position == 0) return "lv 1";
//        else if (position == 1) return "lv 2";
//        else if (position == 2) return "lv 3";
//        else if (position == 3) return "lv 4";
//
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        Log.e("탭 페이퍼 어답터 겟카운트 ", ""+level.lesson_type);

        if(level.lesson_type.equals("let") ){
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/lesson/letsread/l"+level.lesson+"_let_lv1_picture");
            File[] files = file.listFiles();
            if(files != null) {
                count = files.length;
            }
            return count;
        }else if(level.lesson_type.equals("let_lv2")) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/lesson/letsread/l" + level.lesson + "_let");
            File[] files = file.listFiles();
            if(files != null) {
                count = files.length;
            }
            return count;
        }else if(level.lesson_type.equals("rt1")){
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/englishclass/lesson/readandtalk/l"+level.lesson+"_rt1_picture");
            File[] files = file.listFiles();
            if(files != null) {
                count = files.length;
            }
            return count;
        }else
        // Show 2 total pages.
        return 4;
    }
}