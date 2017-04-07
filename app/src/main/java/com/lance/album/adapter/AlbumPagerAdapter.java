package com.lance.album.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lindan on 17-4-5.
 */
public class AlbumPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private List<String> pagerTitles;

    public AlbumPagerAdapter(FragmentActivity activity, List<Fragment> fragments) {
        super(activity.getSupportFragmentManager());
        this.fragments = fragments;
        pagerTitles = new ArrayList<>();
        if (this.fragments != null) {
            for (int i = 0, count = fragments.size(); i < count; i++) {
                Fragment fragment = fragments.get(i);
                pagerTitles.add(fragment.getArguments().getString("title"));
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments == null || position < 0 || position >= fragments.size()) {
            return null;
        }
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments != null ? fragments.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (pagerTitles == null || position < 0 || position >= pagerTitles.size()) {
            return "";
        }
        return pagerTitles.get(position);
    }
}
