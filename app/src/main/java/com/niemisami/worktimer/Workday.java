package com.niemisami.worktimer;

/**
 * Created by Sami on 24.12.2015.
 */
public class Workday {

    private int mId;
    private long mStartTime, mEndTime, mBreakStart, mWholeBreakTime;
//    private boolean mIsWorking, mIsOnBreak;


    public Workday(int id) {
        mId = id;
    }

    public void setId(int id) {
        mId = id;
    }
    public int getId() {
        return mId;

    }
    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }
    public long getStartTime() {
        return mStartTime;
    }

    public void setEndTime(long endTime) {
        mEndTime = endTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setBreakTime(long breakTime) {
        mWholeBreakTime += breakTime;
    }
    public long getBreakTime() {
        return mWholeBreakTime;
    }
    public long getWorkTime() {
        return mEndTime-mStartTime-mWholeBreakTime;
    }

}
