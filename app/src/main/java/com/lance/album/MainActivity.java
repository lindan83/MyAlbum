package com.lance.album;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.lance.album.adapter.AlbumPagerAdapter;
import com.lance.album.fragment.BucketFragment;
import com.lance.album.fragment.PhotoFragment;
import com.lance.common.widget.template.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ViewPager vpMain;
    private TabLayout tabMain;
    private AlbumPagerAdapter pagerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        vpMain = getView(R.id.vp_main);
        tabMain = getView(R.id.tab_main);
    }

    @Override
    public void initEventListeners() {

    }

    @Override
    public void initData() {
        List<Fragment> fragments = new ArrayList<>(2);
        fragments.add(PhotoFragment.newFragment(getString(R.string.app_pager_title_photo)));
        fragments.add(BucketFragment.newFragment(getString(R.string.app_pager_title_album)));
        pagerAdapter = new AlbumPagerAdapter(this, fragments);
        vpMain.setAdapter(pagerAdapter);
        vpMain.setPageMargin(6);
        tabMain.setupWithViewPager(vpMain);
    }

    @Override
    public void processClick(View v) {

    }
}
