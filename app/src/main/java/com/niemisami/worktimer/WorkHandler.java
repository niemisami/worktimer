package com.niemisami.worktimer;

import android.content.Context;

import java.util.List;

/**
 * Created by Sami on 27.12.2015.
 */
public class WorkHandler {


    private long mStartTime, mEndTime, mBreakStart, mWholeBreakTime;

    private boolean mIsWorking, mIsOnBreak;

    private List<Workday> mWorkdayList;
    private static WorkHandler mWorkHandler;
    private Workday mWorkday;
    private DatabaseHelper mDatabaseHelper;

    public WorkHandler(Context context) {
        mDatabaseHelper = DatabaseHelper.getInstance(context);
    }

    public static WorkHandler getInstance(Context context) {
        if (mWorkHandler == null) {
            mWorkHandler = new WorkHandler(context);
        }
        return mWorkHandler;
    }


    public long startWorking() {
        mIsWorking = true;
        mIsOnBreak = false;
        mStartTime = System.currentTimeMillis();
        mWorkday = new Workday(mWorkdayList.size());
        mWorkday.setStartTime(mStartTime);
        mWorkdayList.add(mWorkday);
        return mStartTime;
    }

    public long startBreak() {
        mIsOnBreak = true;
        mBreakStart = System.currentTimeMillis();
        return mBreakStart;
    }

    public long endBreak() {
        mIsOnBreak = false;
        long time = System.currentTimeMillis() - mBreakStart;
        mWorkday.setBreakTime(time);
        return time;
    }


    public long stopWorking() {
        long workTime = 0l;
        if (mIsWorking) {
            if (mIsOnBreak) {
                endBreak();
            }
            long endTime = System.currentTimeMillis();
            workTime = endTime - mStartTime - mWholeBreakTime;
            mWorkday.setEndTime(endTime);
            mIsWorking = false;
            mIsOnBreak = false;

            mDatabaseHelper.insertWork(mWorkday);
        }
        mWorkday = null;

        return workTime;
    }

    public void endProcess() {
        mDatabaseHelper.close();
    }

    public boolean isWorking() {
        return mIsWorking;
    }

    public boolean isOnBreak() {
        return mIsOnBreak;
    }

}
