package com.manage.hospital.hmapp.ui.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.canelmas.let.Let;
import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.adapter.MainFragmentPagerAdapter;


public class ReminderMainActivity extends AppCompatActivity
        {

    private FragmentPagerAdapter adapterViewPager;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ViewPager mVpPager;
    private Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_main);



        mVpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MainFragmentPagerAdapter(fragmentManager);
        mVpPager.setAdapter(adapterViewPager);

        act = this;

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reminder List");
        //getSupportActionBar().setIcon(R.mipmap.logo);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.pending)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.done)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.all)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ReminderMainActivity.this.adapterViewPager.notifyDataSetChanged();
                mVpPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        mVpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
                ReminderMainActivity.this.adapterViewPager.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if( state == ViewPager.SCROLL_STATE_SETTLING){
                    ReminderMainActivity.this.adapterViewPager.notifyDataSetChanged();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NewTaskActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Let.handle(this, requestCode, permissions, grantResults);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent alarm = new Intent(this, AlarmActivity.class);
            this.startActivity(alarm);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
