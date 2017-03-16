package com.call.log.infinity.activity;

import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.constant.ViewPagerPosition;
import com.call.log.infinity.fragment.CallLogFragment;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //初始化主题颜色
        setThemeAtStart();

        if (savedInstanceState == null) {
            CallLogFragment mFragment = new CallLogFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(ViewPagerPosition.POSITION, ViewPagerPosition.POSITION_ALL_TYPE);
            mFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, mFragment);
            fragmentTransaction.commit();
        }
    }

    /**
     * 程序初始化时，设置主题颜色。
     */
    private void setThemeAtStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(MyApplication.getThemeColorPrimaryDark());
        }
    }
}
