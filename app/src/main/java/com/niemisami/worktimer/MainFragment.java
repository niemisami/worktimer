package com.niemisami.worktimer;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


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

    private SharedPreferences mSharedPreferences;
    final private static String PREFERENCE_FILE_KEY = "com.niemisami.worktimer.private_pref_key";
    final private static String PREF_WORKING = "PREF_WORKING";
    final private static String PREF_BREAK = "PREF_BREAK";
    final private static String PREF_START_TIME = "PREF_START_TIME";
    final private static String PREF_BREAK_TIME = "PREF_BREAK_TIME";
    final private static String PREF_END_TIME = "PREF_END_TIME";

    private Toolbar mToolBar;

    private Button mStartStopWorkButton, mBreakStartStopButton;
    private TextView mDateView, mHoursView, mWeekOrDayView;

    private long mStartTime, mEndTime, mBreakStart, mWholeBreakTime;
    private boolean mIsWorking, mIsOnBreak;

    private WorkHandler mWorkHandler;


    //    Timer values
    private long sec;
    private long min;
    private long hrs;
    private String seconds, minutes, hours;

    public MainFragment() {
    }


    /////////FRAGMENT LIFECYCLE METHODS////////
//  region


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getActivity().getSharedPreferences(PREFERENCE_FILE_KEY,
                Activity.MODE_PRIVATE);

        mWorkHandler = WorkHandler.getInstance(getActivity().getApplicationContext());
        loadPreferences();

        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        if (savedInstanceState != null) {
            mIsWorking = savedInstanceState.getBoolean(STATE_WORKING);
            mIsOnBreak = savedInstanceState.getBoolean(STATE_BREAK);
            mStartTime = savedInstanceState.getLong(STATE_START_TIME);
            mEndTime = savedInstanceState.getLong(STATE_END_TIME);
            mWholeBreakTime = savedInstanceState.getLong(STATE_BREAK_TIME);
        }


        //        Find toolbar from the view and set it to support actionbar
//        This can be set into the activity if menu items doesn't change even if fragments does
        setHasOptionsMenu(true);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        mToolBar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolBar.setTitle(TAG);
        appCompatActivity.setSupportActionBar(mToolBar);


        initViews(view);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        setDate();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        savePreferences();
        mWorkHandler.endProcess();
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

        outState.putBoolean(STATE_BREAK, mIsOnBreak);
        outState.putBoolean(STATE_WORKING, mWorkHandler.isWorking());
        outState.putLong(STATE_START_TIME, mStartTime);
        outState.putLong(STATE_END_TIME, mEndTime);
        outState.putLong(STATE_BREAK_TIME, mWholeBreakTime);


        super.onSaveInstanceState(outState);
    }

    /**
     * Store important values to SharedPreferences. Either save or clear values
     */
    private void savePreferences() {

        if (mWorkHandler.isWorking()) {
            Log.d(TAG, "preferences saved");
            mSharedPreferences.edit()
                    .putBoolean(PREF_WORKING, mWorkHandler.isWorking())
                    .putBoolean(PREF_BREAK, mIsOnBreak)
                    .putLong(PREF_START_TIME, mStartTime)
                    .putLong(PREF_BREAK_TIME, mWholeBreakTime)
                    .putLong(PREF_END_TIME, mEndTime)
                    .apply();
        } else {
            mSharedPreferences.edit()
                    .putBoolean(PREF_WORKING, false)
                    .putBoolean(PREF_BREAK, false)
                    .putLong(PREF_START_TIME, 0l)
                    .putLong(PREF_BREAK_TIME, 0l)
                    .putLong(PREF_END_TIME, 0l)
                    .apply();

        }
    }

    private void loadPreferences() {

        if (mSharedPreferences.getBoolean(PREF_WORKING, false)) {
            Log.d(TAG, "Values loaded");
            mIsWorking = mSharedPreferences.getBoolean(PREF_WORKING, false);
            mIsOnBreak = mSharedPreferences.getBoolean(PREF_BREAK, false);
            mStartTime = mSharedPreferences.getLong(PREF_START_TIME, 0l);
            mWholeBreakTime = mSharedPreferences.getLong(PREF_BREAK_TIME, 0l);
            mEndTime = mSharedPreferences.getLong(PREF_END_TIME, 0l);
        } else {
            Log.d(TAG, "Run for the first time");
        }
    }
//    endregion


    //////VIEW INITIALIZATION///////

    //    region
    private void initViews(View view) {

        mDateView = (TextView) view.findViewById(R.id.date_text);
        mHoursView = (TextView) view.findViewById(R.id.hours_stats);
        mWeekOrDayView = (TextView) view.findViewById(R.id.day_week_stat_text);

        mBreakStartStopButton = (Button) view.findViewById(R.id.break_button);
        mBreakStartStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWorkHandler.isOnBreak()) {
                    startBreak();
                } else {
                    endBreak();
                }
            }
        });


        mStartStopWorkButton = (Button) view.findViewById(R.id.start_stop_work_button);
        mStartStopWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWorkHandler.isWorking()) {
                    startWorking();
                } else {
                    stopWorking();
                }
            }
        });


        disableBreakButton();

    }

    private void disableBreakButton() {
        //        Disable break button because works hasn't been started
        mBreakStartStopButton.setEnabled(mWorkHandler.isWorking() || mIsOnBreak);
        if (!mBreakStartStopButton.isEnabled()) {
            mBreakStartStopButton.setBackgroundColor(Color.parseColor("#22442299"));
            mBreakStartStopButton.setTextColor(Color.parseColor("#22000000"));
        } else {
            enableBreakButton();
        }
    }

    private void enableBreakButton() {
        mBreakStartStopButton.setEnabled(true);
        mBreakStartStopButton.setBackgroundColor(getResources().getColor(R.color.primary));
        mBreakStartStopButton.setTextColor(getResources().getColor(R.color.ambient_white));

    }

    private void updateUI() {
        if (mWorkHandler.isWorking()) {
            mStartStopWorkButton.setText(getResources().getString(R.string.leave_work));
        } else {
            mStartStopWorkButton.setText(getResources().getString(R.string.come_to_work));
        }
        if (mWorkHandler.isOnBreak()) {
            mBreakStartStopButton.setText(getResources().getString(R.string.end_break));
        } else {
            mBreakStartStopButton.setText(getResources().getString(R.string.start_break));
        }


    }

    //    endregion
    private void setDate() {

        Format formatter = new SimpleDateFormat("cccc dd.MM.yyyy");
        Date date = new Date();
        String formattedDate = formatter.format(date);
        mDateView.setText(formattedDate);
    }

    private void startWorking() {
        mStartStopWorkButton.setText(getResources().getString(R.string.leave_work));
        if (!mBreakStartStopButton.isEnabled()) {
            enableBreakButton();
        }
        mWholeBreakTime = 0l;
        mBreakStart = 0l;
        mEndTime = 0l;
        mStartTime = mWorkHandler.startWorking();
    }

    private void stopWorking() {
        long workTime = mWorkHandler.stopWorking();
        mHoursView.setText(formatTime(workTime));

        disableBreakButton();
        mStartStopWorkButton.setText(getResources().getString(R.string.come_to_work));
    }

    private void resetWorkValues() {

    }

    private void startBreak() {
        mBreakStartStopButton.setText(getResources().getString(R.string.end_break));
        mBreakStart = mWorkHandler.startBreak();

    }

    private void endBreak() {
        mBreakStartStopButton.setText(getResources().getString(R.string.start_break));
        mWholeBreakTime = mWorkHandler.endBreak();

//        mIsWorking = true;
    }


    private String formatTime(long millis) {

//        345082 / 1000 = 345,082 sekuntia
//        345,082 / 60 = 5,75
        sec = (millis / 1000);
        min = sec / 60;
        hrs = min / 60;

        sec = sec % 60;
        seconds = String.valueOf(Math.abs(sec));
        if (sec == 0) seconds = "00";
        else if (sec < 10 && sec > 0) {
            seconds = "0" + seconds;
        }

        min = min % 60;
        minutes = String.valueOf(Math.abs(min));
        if (min == 0) minutes = "00";
        else if (min < 10) {
            minutes = "0" + minutes;
        }

        hours = String.valueOf(Math.abs(hrs));
        if (hrs == 0) hours = "00";
        else if (hrs < 10) {
            hours = "0" + hours;
        }

        return hours + "h " + minutes + "min " + seconds + "s";
    }
}
