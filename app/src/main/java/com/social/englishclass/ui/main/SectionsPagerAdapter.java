package com.social.englishclass.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.social.englishclass.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        PlaceholderFragment fragment = null;
//        fragment = PlaceholderFragment.newInstance(position + 1);
//        fragment = new PlaceholderFragment();

        switch(position) {
            case 0:
                fragment = PlaceholderFragment.newInstance(1);
                Bundle args = new Bundle();
                args.putString("l1lv1", "/storage/emulated/0/englishclass/lesson1/l1lv1.mp4");

                fragment.setArguments(args);
                break;
            case 1:
                fragment = PlaceholderFragment.newInstance(2);
                break;
            case 2:
                fragment = PlaceholderFragment.newInstance(3);
                break;
            case 3:
                fragment = PlaceholderFragment.newInstance(4);
                break;
            case 4:
                fragment = PlaceholderFragment.newInstance(5);
                break;
        }


        return fragment;

        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) return "lv 1";

        else if (position == 1) return "lv 2";

        else if (position == 2) return "lv 3";

        else if (position == 3) return "lv 4";

        else if (position == 4) return "lv 5";
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 5;
    }
}