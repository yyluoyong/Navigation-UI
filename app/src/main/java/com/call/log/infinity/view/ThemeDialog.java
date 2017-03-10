package com.call.log.infinity.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.call.log.infinity.R;
import com.call.log.infinity.utils.LogUtil;

/**
 * Created by Yong on 2017/3/7.
 */

public class ThemeDialog extends DialogFragment implements View.OnClickListener {
    static final String TAG = "ThemeDialog";

    private int primary;
    private int primaryDark;
    private int primaryLight;
    private int accent;

    //颜色在数组中的对应位置
    public static final int PRIMARY_COLOR_INDEX = 0;
    public static final int PRIMARY_DARK_COLOR_INDEX = 1;
    public static final int PRIMARY_LIGHT_COLOR_INDEX = 2;
    public static final int ACCENT_COLOR_INDEX = 3;

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        final View layout = inflater.inflate(R.layout.dialog_theme, container, false);
        layout.findViewById(R.id.blue_theme).setOnClickListener(this);
        layout.findViewById(R.id.indigo_theme).setOnClickListener(this);
        layout.findViewById(R.id.green_theme).setOnClickListener(this);
        layout.findViewById(R.id.red_theme).setOnClickListener(this);
        layout.findViewById(R.id.blue_grey_theme).setOnClickListener(this);
        layout.findViewById(R.id.black_theme).setOnClickListener(this);
        layout.findViewById(R.id.purple_theme).setOnClickListener(this);
        layout.findViewById(R.id.orange_theme).setOnClickListener(this);
        layout.findViewById(R.id.teal_theme).setOnClickListener(this);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.blue_theme:
                primary = ContextCompat.getColor(getActivity(), R.color.blue_primary);
                primaryDark = ContextCompat.getColor(getActivity(), R.color.blue_primary_dark);
                primaryLight = ContextCompat.getColor(getActivity(), R.color.blue_primary_light);
                accent = ContextCompat.getColor(getActivity(), R.color.blue_accent);
                break;
            case R.id.indigo_theme:
                primary = ContextCompat.getColor(getActivity(), R.color.indigo_primary);
                primaryDark = ContextCompat.getColor(getActivity(), R.color.indigo_primary_dark);
                primaryLight = ContextCompat.getColor(getActivity(), R.color.indigo_primary_light);
                accent = ContextCompat.getColor(getActivity(), R.color.indigo_accent);
                break;
            case R.id.green_theme:
                primary = ContextCompat.getColor(getActivity(), R.color.green_primary);
                primaryDark = ContextCompat.getColor(getActivity(), R.color.green_primary_dark);
                primaryLight = ContextCompat.getColor(getActivity(), R.color.green_primary_light);
                accent = ContextCompat.getColor(getActivity(), R.color.green_accent);
                break;
            case R.id.red_theme:
                primary = ContextCompat.getColor(getActivity(), R.color.red_primary);
                primaryDark = ContextCompat.getColor(getActivity(), R.color.red_primary_dark);
                primaryLight = ContextCompat.getColor(getActivity(), R.color.red_primary_light);
                accent = ContextCompat.getColor(getActivity(), R.color.red_accent);
                break;
            case R.id.blue_grey_theme:
                primary = ContextCompat.getColor(getActivity(), R.color.blue_grey_primary);
                primaryDark = ContextCompat.getColor(getActivity(), R.color.blue_grey_primary_dark);
                primaryLight = ContextCompat.getColor(getActivity(), R.color.blue_grey_primary_light);
                accent = ContextCompat.getColor(getActivity(), R.color.blue_grey_accent);
                break;
            case R.id.black_theme:
                primary = ContextCompat.getColor(getActivity(), R.color.black_primary);
                primaryDark = ContextCompat.getColor(getActivity(), R.color.black_primary_dark);
                primaryLight = ContextCompat.getColor(getActivity(), R.color.black_primary_light);
                accent = ContextCompat.getColor(getActivity(), R.color.black_accent);
                break;
            case R.id.orange_theme:
                primary = ContextCompat.getColor(getActivity(), R.color.orange_primary);
                primaryDark = ContextCompat.getColor(getActivity(), R.color.orange_primary_dark);
                primaryLight = ContextCompat.getColor(getActivity(), R.color.orange_primary_light);
                accent = ContextCompat.getColor(getActivity(), R.color.orange_accent);
                break;
            case R.id.purple_theme:
                primary = ContextCompat.getColor(getActivity(), R.color.purple_primary);
                primaryDark = ContextCompat.getColor(getActivity(), R.color.purple_primary_dark);
                primaryLight = ContextCompat.getColor(getActivity(), R.color.purple_primary_light);
                accent = ContextCompat.getColor(getActivity(), R.color.purple_accent);
                break;
            case R.id.teal_theme:
                primary = ContextCompat.getColor(getActivity(), R.color.teal_primary);
                primaryDark = ContextCompat.getColor(getActivity(), R.color.teal_primary_dark);
                primaryLight = ContextCompat.getColor(getActivity(), R.color.teal_primary_light);
                accent = ContextCompat.getColor(getActivity(), R.color.teal_accent);
                break;
            default:
                primary = ContextCompat.getColor(getActivity(), R.color.blue_primary);
                primaryDark = ContextCompat.getColor(getActivity(), R.color.blue_primary_dark);
                primaryLight = ContextCompat.getColor(getActivity(), R.color.blue_primary_light);
                accent = ContextCompat.getColor(getActivity(), R.color.blue_accent);
                break;
        }

        SharedPreferences sharedPreferences = getActivity()
            .getSharedPreferences(getString(R.string.theme), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.themePrimaryColor), primary);
        editor.putInt(getString(R.string.themePrimaryDarkColor), primaryDark);
        editor.putInt(getString(R.string.themePrimaryLightColor), primaryLight);
        editor.putInt(getString(R.string.themeAccentColor), accent);
        editor.commit();

        ThemeDialog.this.dismiss();

        getActivity().recreate();
    }
}
