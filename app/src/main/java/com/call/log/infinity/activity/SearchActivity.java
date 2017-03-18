package com.call.log.infinity.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.model.SearchModel;
import com.call.log.infinity.utils.CallDateFormatter;
import com.call.log.infinity.utils.PhoneNumberFormatter;

public class SearchActivity extends AppCompatActivity {
    static final String TAG = "SearchActivity";

    private Toolbar mToolbar;
    private FloatingActionButton mFloatFB;

    private SearchModel mSearchModel = new SearchModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        createToolbar();

        createFloatingActionButton();

        //初始化主题颜色
        setThemeAtStart();

        setClickListener();
    }

    /**
     * 初始化Toolbar。
     */
    private void createToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化悬浮按钮。
     */
    private void createFloatingActionButton() {
        mFloatFB = (FloatingActionButton) findViewById(R.id.fab);
        mFloatFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSearchBeanDate()) {
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("SearchModel", mSearchModel);
                    startActivity(intent);
                } else {
                    new MaterialDialog.Builder(SearchActivity.this)
                        .iconRes(R.drawable.ic_error)
                        .title(R.string.searchErrorDialogTitle)
                        .content(R.string.searchInputError, true)
                        .positiveText(R.string.dialog_ok)
                        .show();
                }
            }
        });
    }

    /**
     * 设置监听器。
     */
    private void setClickListener() {
        findViewById(R.id.name_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNameListener();
            }
        });

        findViewById(R.id.phone_number_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPhoneNumberListener();
            }
        });

        findViewById(R.id.date_min_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateStart();
            }
        });

        findViewById(R.id.date_max_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateEnd();
            }
        });

        findViewById(R.id.duration_min_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDurationMinimum();
            }
        });

        findViewById(R.id.duration_max_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDurationMaximum();
            }
        });

        findViewById(R.id.call_type_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCallType();
            }
        });
    }

        /**
     * 程序初始化时，设置主题颜色。
     */
    private void setThemeAtStart() {
        mToolbar.setBackgroundColor(MyApplication.getThemeColorPrimary());
        mFloatFB.setBackgroundTintList(ColorStateList.valueOf(MyApplication.getThemeColorAccent()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(MyApplication.getThemeColorPrimaryDark());
        }
    }

    /**
     * 设置待搜索的联系人姓名。
     */
    private void setNameListener() {
        //条目最多显示出多少位字符
        final int maxShowLength = 10;

        MaterialDialog dialog = new MaterialDialog.Builder(this)
            .title(R.string.searchName)
            .customView(R.layout.dialog_name_custom, true)
            .negativeText(R.string.dialog_cancel)
            .positiveText(R.string.dialog_ok)
            .neutralText(R.string.searchSetDefaultBtn)
            .negativeColor(MyApplication.getThemeColorPrimary())
            .positiveColor(MyApplication.getThemeColorPrimaryDark())
            .neutralColor(MyApplication.getThemeColorAccent())
            .build();

        final EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.name);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String name = editText.getText().toString();

                if (TextUtils.isEmpty(name) == false) {
                    mSearchModel.setContactsName(name);
                    if (name.length() > maxShowLength) {
                        ((TextView) findViewById(R.id.name_tv)).setText(name.substring(0, maxShowLength) + "..." );
                    } else {
                        ((TextView) findViewById(R.id.name_tv)).setText(name);
                    }
                } else {
                    mSearchModel.setContactsName(null);
                    ((TextView) findViewById(R.id.name_tv)).setText(getString(R.string.searchDefault));
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchModel.setContactsName(null);
                ((TextView) findViewById(R.id.name_tv)).setText(getString(R.string.searchDefault));
            }
        });

        dialog.show();
    }

    /**
     * 设置待搜索的电话号码。
     */
    private void setPhoneNumberListener() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
            .title(R.string.searchPhoneNumber)
            .customView(R.layout.dialog_phone_number_custom, true)
            .negativeText(R.string.dialog_cancel)
            .positiveText(R.string.dialog_ok)
            .neutralText(R.string.searchSetDefaultBtn)
            .negativeColor(MyApplication.getThemeColorPrimary())
            .positiveColor(MyApplication.getThemeColorPrimaryDark())
            .neutralColor(MyApplication.getThemeColorAccent())
            .build();

        final EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.phone_number);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String phoneNumber = editText.getText().toString();

                if (TextUtils.isEmpty(phoneNumber) == false) {
                    mSearchModel.setPhoneNumber(phoneNumber);
                    ((TextView) findViewById(R.id.phone_number_tv))
                        .setText(PhoneNumberFormatter.phoneNumberFormat(phoneNumber));
                } else {
                    mSearchModel.setPhoneNumber(null);
                    ((TextView) findViewById(R.id.phone_number_tv)).setText(getString(R.string.searchDefault));
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchModel.setPhoneNumber(null);
                ((TextView) findViewById(R.id.phone_number_tv)).setText(getString(R.string.searchDefault));
            }
        });

        dialog.show();
    }

    /**
     * 设置起始日期。
     */
    private void setDateStart() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
            .customView(R.layout.dialog_datepicker_custom, false)
            .negativeText(R.string.dialog_cancel)
            .positiveText(R.string.dialog_ok)
            .neutralText(R.string.searchSetDefaultBtn)
            .negativeColor(MyApplication.getThemeColorPrimary())
            .positiveColor(MyApplication.getThemeColorPrimaryDark())
            .neutralColor(MyApplication.getThemeColorAccent())
            .build();

        final DatePicker mDatePicker = (DatePicker) dialog.getCustomView().findViewById(R.id.datePicker);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth() + 1;
                int day = mDatePicker.getDayOfMonth();

                String dateFormat = getString(R.string.searchDateFormat);
                String dateShow = String.format(dateFormat, year, month, day);
                ((TextView) findViewById(R.id.date_min_tv)).setText(dateShow);

                //将日期转换为毫秒
                String dateStr = year + "," + month + "," + day;
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy,M,d");

                try {
                    //起始日期包含这一天本身
                    long millis = dateFormatter.parse(dateStr).getTime();
                    mSearchModel.setDateStart(millis);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchModel.setDateStart(-1L);
                ((TextView) findViewById(R.id.date_min_tv)).setText(getString(R.string.searchDefault));
            }
        });

        dialog.show();
    }

    /**
     * 设置截止日期。
     */
    private void setDateEnd() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
            .customView(R.layout.dialog_datepicker_custom, false)
            .negativeText(R.string.dialog_cancel)
            .positiveText(R.string.dialog_ok)
            .neutralText(R.string.searchSetDefaultBtn)
            .negativeColor(MyApplication.getThemeColorPrimary())
            .positiveColor(MyApplication.getThemeColorPrimaryDark())
            .neutralColor(MyApplication.getThemeColorAccent())
            .build();

        final DatePicker mDatePicker = (DatePicker) dialog.getCustomView().findViewById(R.id.datePicker);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth() + 1;
                int day = mDatePicker.getDayOfMonth();

                String dateFormat = getString(R.string.searchDateFormat);
                String dateShow = String.format(dateFormat, year, month, day);
                ((TextView) findViewById(R.id.date_max_tv)).setText(dateShow);

                //将日期转换为毫秒
                String dateStr = year + "," + month + "," + day;
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy,M,d");

                try {
                    //截止日期包含这一天本身，需加上当天的毫秒数
                    long millis = dateFormatter.parse(dateStr).getTime() + CallDateFormatter.A_DAY_IN_MILLISECOND;
                    mSearchModel.setDateEnd(millis);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchModel.setDateEnd(-1L);
                ((TextView) findViewById(R.id.date_max_tv)).setText(getString(R.string.searchDefault));
            }
        });

        dialog.show();
    }

    /**
     * 设置通话时长下限。
     */
    private void setDurationMinimum() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
            .title(R.string.searchDurationMinimumTitle)
            .customView(R.layout.dialog_duration_custom, true)
            .negativeText(R.string.dialog_cancel)
            .positiveText(R.string.dialog_ok)
            .neutralText(R.string.searchSetDefaultBtn)
            .negativeColor(MyApplication.getThemeColorPrimary())
            .positiveColor(MyApplication.getThemeColorPrimaryDark())
            .neutralColor(MyApplication.getThemeColorAccent())
            .build();

        final EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.duration);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String duration = editText.getText().toString();

                if (TextUtils.isEmpty(duration) == false) {
                    mSearchModel.setDurationMin(Integer.parseInt(duration));
                    ((TextView) findViewById(R.id.duration_min_tv)).setText(duration + getString(R.string.secondName));
                } else {
                    mSearchModel.setDurationMin(-1);
                    ((TextView) findViewById(R.id.duration_min_tv)).setText(getString(R.string.searchDefault));
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchModel.setDurationMin(-1);
                ((TextView) findViewById(R.id.duration_min_tv)).setText(getString(R.string.searchDefault));
            }
        });

        dialog.show();
    }

    /**
     * 设置通话时长上限。
     */
    private void setDurationMaximum() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
            .title(R.string.searchDurationMinimumTitle)
            .customView(R.layout.dialog_duration_custom, true)
            .negativeText(R.string.dialog_cancel)
            .positiveText(R.string.dialog_ok)
            .neutralText(R.string.searchSetDefaultBtn)
            .negativeColor(MyApplication.getThemeColorPrimary())
            .positiveColor(MyApplication.getThemeColorPrimaryDark())
            .neutralColor(MyApplication.getThemeColorAccent())
            .build();

        final EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.duration);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String duration = editText.getText().toString();

                if (TextUtils.isEmpty(duration) == false) {
                    mSearchModel.setDurationMax(Integer.parseInt(duration));
                    ((TextView) findViewById(R.id.duration_max_tv)).setText(duration + getString(R.string.secondName));
                } else {
                    mSearchModel.setDurationMax(-1);
                    ((TextView) findViewById(R.id.duration_max_tv)).setText(getString(R.string.searchDefault));
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchModel.setDurationMax(-1);
                ((TextView) findViewById(R.id.duration_max_tv)).setText(getString(R.string.searchDefault));
            }
        });

        dialog.show();
    }

    /**
     * 设置通话类型。
     */
    private void setCallType() {
        //通话类型和标签对应关系
        final int[] typeToPosition = getResources().getIntArray(R.array.search_call_type_to_position);

        MaterialDialog dialog = new MaterialDialog.Builder(this)
            .title(R.string.searchCallType)
            .negativeText(R.string.dialog_cancel)
            .positiveText(R.string.dialog_ok)
            .neutralText(R.string.searchSetDefaultBtn)
            .negativeColor(MyApplication.getThemeColorPrimary())
            .positiveColor(MyApplication.getThemeColorPrimaryDark())
            .neutralColor(MyApplication.getThemeColorAccent())
            .items(R.array.search_call_type)
            .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                @Override
                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                    switch (which) {
                        case 0:
                            mSearchModel.setCallType(typeToPosition[0]);
                            ((TextView) findViewById(R.id.call_type_tv)).setText(getString(R.string.searchDefault));
                            break;
                        case 1:
                            mSearchModel.setCallType(typeToPosition[1]);
                            ((TextView) findViewById(R.id.call_type_tv)).setText(getString(R.string.searchCallTypeIn));
                            break;
                        case 2:
                            mSearchModel.setCallType(typeToPosition[2]);
                            ((TextView) findViewById(R.id.call_type_tv)).setText(getString(R.string.searchCallTypeOut));
                            break;
                        case 3:
                            mSearchModel.setCallType(typeToPosition[3]);
                            ((TextView) findViewById(R.id.call_type_tv)).setText(getString(R.string.searchCallTypeMiss));
                            break;
                        default:
                    }

                    return true;
                }
            })
            .show();

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchModel.setCallType(typeToPosition[0]);
                ((TextView) findViewById(R.id.call_type_tv)).setText(getString(R.string.searchDefault));
            }
        });
    }

    /**
     * 检查信息模型数据的合理性。
     * @return
     */
    private boolean checkSearchBeanDate() {
        if (( mSearchModel.getDateStart() > 0) && (mSearchModel.getDateEnd() > 0)) {
            if (mSearchModel.getDateStart() > mSearchModel.getDateEnd()) {
                return false;
            }
        }

        if ((mSearchModel.getDurationMin() >= 0) && (mSearchModel.getDurationMax() >= 0)) {
            if (mSearchModel.getDurationMin() > mSearchModel.getDurationMin()) {
                return false;
            }
        }

        if (TextUtils.isEmpty(mSearchModel.getContactsName()) && TextUtils.isEmpty(mSearchModel.getPhoneNumber())
            && (mSearchModel.getDateStart() <= 0) && (mSearchModel.getDateEnd() <= 0)
            && (mSearchModel.getDurationMin() < 0) && (mSearchModel.getDurationMin() < 0)) {
            return false;
        }

        return true;
    }
}
