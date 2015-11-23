package com.niemisami.worktimer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.niemisami.worktimer.R;

import org.w3c.dom.Text;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    final private String TAG = "MainFragment";

    final private static String STATE_WORKING = "STATE_WORKING";
    final private static String STATE_BREAK = "STATE_BREAK";
    final private static String STATE_START_TIME = "STATE_START_TIME";
    final private static String STATE_BREAK_TIME = "STATE_BREAK_TIME";
    final private static String STATE_END_TIME = "STATE_END_TIME";





    private Toolbar mToolBar;

    private Button mStartStopWorkButton, mBreakButton;
    private TextView mDateView, mHoursView, mWeekOrDayView;
    
    private long mStartTime, mEndTime, mBreakStart, mWholeBreakTime;
    private boolean mWorking, mOnBreak;
    

    public MainFragment() {
    }


/////////FRAGMENT LIFECYCLE METHODS////////
//  region
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        if (savedInstanceState != null) {
            mWorking = savedInstanceState.getBoolean(STATE_WORKING);
            mOnBreak = savedInstanceState.getBoolean(STATE_BREAK);
            mStartTime = savedInstanceState.getLong(STATE_START_TIME);
            mWholeBreakTime = savedInstanceState.getLong(STATE_BREAK_TIME);
        }

//        Find toolbar from the view and set it to support actionbar
//        This can be set into the activity if menu items doesn't change even if fragments does
        setHasOptionsMenu(true);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        mToolBar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolBar.setTitle(TAG);
        appCompatActivity.setSupportActionBar(mToolBar);


        initViews(view);

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


//    Create material design Toolbar as menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        
        outState.putBoolean(STATE_BREAK, mOnBreak);
        outState.putBoolean(STATE_WORKING, mWorking);
        outState.putLong(STATE_START_TIME, mStartTime);
        outState.putLong(STATE_END_TIME, mEndTime);
        outState.putLong(STATE_BREAK_TIME, mWholeBreakTime);


        super.onSaveInstanceState(outState);
    }
//    endregion


    //////VIEW INITIALIZATION///////

//    region
    private void initViews(View view) {

        mDateView = (TextView) view.findViewById(R.id.date_text);
        mHoursView = (TextView) view.findViewById(R.id.hours_stats);
        mWeekOrDayView = (TextView) view.findViewById(R.id.day_week_stat_text);

        mBreakButton = (Button) view.findViewById(R.id.break_button);
        mStartStopWorkButton = (Button) view.findViewById(R.id.start_stop_work_button);
        mStartStopWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mWorking) {
                    startWorking();
                    mStartStopWorkButton.setText(getResources().getString(R.string.leave_work));
                }
                else {
                    stopWorking();
                    mStartStopWorkButton.setText(getResources().getString(R.string.come_to_work));
                }
            }
        });

    }

    private void updateUI() {
        setTime();
        if(mWorking) {
            mStartStopWorkButton.setText(getResources().getString(R.string.leave_work));
        }
        else {
            mStartStopWorkButton.setText(getResources().getString(R.string.come_to_work));
        }

    }

//    endregion
    private void setTime() {

        Format formatter = new SimpleDateFormat("cccc dd.MM.yyyy");
        Date date = new Date();
        String formattedDate = formatter.format(date);
        mDateView.setText(formattedDate);
    }
    
    private void startWorking() {
        mWorking = true;
        mOnBreak = false;
        mStartTime = System.currentTimeMillis();        
    }
    
    private void stopWorking() {
        if (mWorking) {
            mWorking = !mWorking;
            long endTime = System.currentTimeMillis();

            long workTime = endTime - mStartTime - mWholeBreakTime;
//            workTime = Math.round(workTime);
//
//            Format formatter = new SimpleDateFormat("hh:mm:ss");
//            Date formatDate = new Date();
//            formatDate.setTime(workTime);
//            String formattedDate = formatter.format(formatDate);
            mHoursView.setText(formatTime(workTime));
        }
    }
    private void startBreak() {
        mBreakStart = System.currentTimeMillis();
    }
    private void endBreak() {
        mWholeBreakTime += System.currentTimeMillis() - mBreakStart;
    }



    private long sec;
    private long min;
    private long hrs;
    private String seconds, minutes, hours;

    private String formatTime(long millis) {

//        345082 / 1000 = 345,082 sekuntia
//        345,082 / 60 = 5,75
        sec = (millis / 1000);
        min = sec / 60;
        hrs = min / 60;

        sec = sec % 60;
        seconds = String.valueOf(Math.abs(sec));
        if(sec == 0) seconds = "00";
        else if(sec < 10 && sec > 0) {
            seconds = "0"+seconds;
        }

        min = min % 60;
        minutes = String.valueOf(Math.abs(min));
        if(min == 0) minutes = "00";
        else if(min < 10) {
            minutes = "0" + minutes;
        }

        hours = String.valueOf(Math.abs(hrs));
        if(hrs == 0) hours = "00";
        else if(hrs < 10) {
            hours = "0" + hours;
        }

        return hours + "h " + minutes + "min " + seconds + "s";
    }
}
