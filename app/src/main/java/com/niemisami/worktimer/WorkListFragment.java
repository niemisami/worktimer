package com.niemisami.worktimer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class WorkListFragment extends Fragment {

    private ViewGroup mRootView;
    private ImageButton mButton1, mButton2;
    private TextView mAnimationTitle1, mAnimationTitle2;

    public WorkListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_list, container, false);
        initView(view);
        return view;
    }


    private void initView(View view) {

        mRootView = (ViewGroup) view.findViewById(R.id.fragmentWorkList);
//        mRootView.setOnClickListener(this);

        mAnimationTitle1 = (TextView) view.findViewById(R.id.animation_text_view1);
        mAnimationTitle2 = (TextView) view.findViewById(R.id.animation_text_view1);

        mButton1 = (ImageButton) view.findViewById(R.id.fab1);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(mRootView, new Fade());
                toggleVisibility(mAnimationTitle1);

            }
        });
        mButton2 = (ImageButton) view.findViewById(R.id.fab2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(mRootView, new Fade());
                toggleVisibility(mAnimationTitle1);

            }
        });
    }

    private static void toggleVisibility(View... views) {
        for(View view : views) {
            boolean isVisible = view.getVisibility() == View.VISIBLE;
            view.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        }

    }
}
