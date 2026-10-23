package com.agrosmart.activities.farmer;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.fragments.CropPlannerFragment;

public class CropRecommendationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        setContentView(container, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(container.getId(), new CropPlannerFragment())
                    .commit();
        }
    }
}
