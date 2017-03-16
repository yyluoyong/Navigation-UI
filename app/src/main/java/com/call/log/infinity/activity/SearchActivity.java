package com.call.log.infinity.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.database.CallLogModelDBFlow;
import com.call.log.infinity.database.CallLogModelDBFlow_Table;
import com.call.log.infinity.utils.CallDateFormatter;
import com.call.log.infinity.utils.CallerLocationAndOperatorQueryUtil;
import com.call.log.infinity.utils.LogUtil;
import com.call.log.infinity.utils.PhoneNumberFormatter;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

public class SearchActivity extends AppCompatActivity {
    static final String TAG = "SearchActivity";

    private Toolbar mToolbar;
    private FloatingActionButton mFloatFB;

    private SearchBean mSearchBean = new SearchBean();

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
                    Toast.makeText(SearchActivity.this, mSearchBean.toString(), Toast.LENGTH_LONG).show();
                    getCallLogModelList();

                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
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

    private void getCallLogModelList() {
        List<CallLogModelDBFlow> mCallLogModelDBFlowList = SQLite.select().from(CallLogModelDBFlow.class)
            .where(getDBFlowConditionGroup())
            .orderBy(CallLogModelDBFlow_Table.dateInMilliseconds, false)
            .queryList();

        for (CallLogModelDBFlow model : mCallLogModelDBFlowList) {
            String[] areaAndOperator = CallerLocationAndOperatorQueryUtil
                .callerLocationAndOperatorQuery(model.getPhoneNumber());
            String callerLoc = areaAndOperator[0];
            String operator = areaAndOperator[1];

            if (TextUtils.isEmpty(callerLoc)) {
                callerLoc = CallerLocationAndOperatorQueryUtil.UNKOWN_AREA;
                operator = CallerLocationAndOperatorQueryUtil.UNKOWN_OPERATOR;
            }

            model.setCallerLoc(callerLoc);
            model.setOperator(operator);

            LogUtil.d(TAG, model.toString());
        }
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
     * 用搜索模型构造搜索条件。
     */
    private ConditionGroup getDBFlowConditionGroup() {
        ConditionGroup sqlCondition = ConditionGroup.clause();

        if (!TextUtils.isEmpty(mSearchBean.contactsName)) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.contactsName.eq(mSearchBean.contactsName));
        }

        if (!TextUtils.isEmpty(mSearchBean.phoneNumber)) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.phoneNumber.eq(mSearchBean.phoneNumber));
        }

        if (mSearchBean.dateStart > 0) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.dateInMilliseconds
                .greaterThanOrEq(mSearchBean.dateStart));
        }

        if (mSearchBean.dateEnd > 0) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.dateInMilliseconds
                .lessThanOrEq(mSearchBean.dateEnd));
        }

        if (mSearchBean.durationMin >= 0) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.duration
                .greaterThanOrEq(mSearchBean.durationMin));
        }

        if (mSearchBean.durationMax >= 0) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.duration
                .lessThanOrEq(mSearchBean.durationMax));
        }

        if (mSearchBean.callType == CallLog.Calls.OUTGOING_TYPE) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.OUTGOING_TYPE));
        } else if (mSearchBean.callType == CallLog.Calls.INCOMING_TYPE) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.INCOMING_TYPE))
                .or(ConditionGroup.clause().and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.MISSED_TYPE)));
        } else if (mSearchBean.callType == CallLog.Calls.MISSED_TYPE) {
            sqlCondition = sqlCondition.and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.MISSED_TYPE))
                .or(ConditionGroup.clause().and(CallLogModelDBFlow_Table.callType.eq(CallLog.Calls.INCOMING_TYPE))
                    .and(CallLogModelDBFlow_Table.duration.eq(0)));
        }

        return sqlCondition;
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
                    mSearchBean.contactsName = name;
                    if (name.length() > maxShowLength) {
                        ((TextView) findViewById(R.id.name_tv)).setText(name.substring(0, maxShowLength) + "..." );
                    } else {
                        ((TextView) findViewById(R.id.name_tv)).setText(name);
                    }
                } else {
                    mSearchBean.contactsName = null;
                    ((TextView) findViewById(R.id.name_tv)).setText(getString(R.string.searchDefault));
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchBean.contactsName = null;
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
                    mSearchBean.phoneNumber = phoneNumber;
                    ((TextView) findViewById(R.id.phone_number_tv))
                        .setText(PhoneNumberFormatter.phoneNumberFormat(phoneNumber));
                } else {
                    mSearchBean.phoneNumber = null;
                    ((TextView) findViewById(R.id.phone_number_tv)).setText(getString(R.string.searchDefault));
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchBean.phoneNumber = null;
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
                    mSearchBean.dateStart = millis;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchBean.dateStart = -1;
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
                    mSearchBean.dateEnd = millis;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchBean.dateEnd = -1;
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
                    mSearchBean.durationMin = Integer.parseInt(duration);
                    ((TextView) findViewById(R.id.duration_min_tv)).setText(duration + getString(R.string.secondName));
                } else {
                    mSearchBean.durationMin = -1;
                    ((TextView) findViewById(R.id.duration_min_tv)).setText(getString(R.string.searchDefault));
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchBean.durationMin = -1;
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
                    mSearchBean.durationMax = Integer.parseInt(duration);
                    ((TextView) findViewById(R.id.duration_max_tv)).setText(duration + getString(R.string.secondName));
                } else {
                    mSearchBean.durationMax = -1;
                    ((TextView) findViewById(R.id.duration_max_tv)).setText(getString(R.string.searchDefault));
                }
            }
        });

        dialog.getBuilder().onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mSearchBean.durationMax = -1;
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
                            mSearchBean.callType = typeToPosition[0];
                            ((TextView) findViewById(R.id.call_type_tv)).setText(getString(R.string.searchDefault));
                            break;
                        case 1:
                            mSearchBean.callType = typeToPosition[1];
                            ((TextView) findViewById(R.id.call_type_tv)).setText(getString(R.string.searchCallTypeIn));
                            break;
                        case 2:
                            mSearchBean.callType = typeToPosition[2];
                            ((TextView) findViewById(R.id.call_type_tv)).setText(getString(R.string.searchCallTypeOut));
                            break;
                        case 3:
                            mSearchBean.callType = typeToPosition[3];
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
                mSearchBean.callType = typeToPosition[0];
                ((TextView) findViewById(R.id.call_type_tv)).setText(getString(R.string.searchDefault));
            }
        });
    }

    /**
     * 检查信息模型数据的合理性。
     * @return
     */
    private boolean checkSearchBeanDate() {
        if (( mSearchBean.dateStart > 0) && (mSearchBean.dateEnd > 0)) {
            if (mSearchBean.dateStart > mSearchBean.dateEnd) {
                return false;
            }
        }

        if ((mSearchBean.durationMin >= 0) && (mSearchBean.durationMax >= 0)) {
            if (mSearchBean.durationMin > mSearchBean.durationMax) {
                return false;
            }
        }

        if (TextUtils.isEmpty(mSearchBean.contactsName) && TextUtils.isEmpty(mSearchBean.phoneNumber)
            && (mSearchBean.dateStart <= 0) && (mSearchBean.dateEnd <= 0)
            && (mSearchBean.durationMin < 0) && (mSearchBean.durationMax < 0)) {
            return false;
        }

        return true;
    }

    /**
     * 搜索信息模型。
     */
    public class SearchBean {
        String contactsName = null;
        String phoneNumber = null;
        long dateStart = -1L;
        long dateEnd = -1L;
        int durationMin = -1;
        int durationMax = -1;
        int callType = 0;

        @Override
        public String toString() {
            String format = "name:%1$s, phone:%2$s, date:%3$d --> %4$d, duration:%5$d --> %6$d, type:%7$d";
            return String.format(format, contactsName, phoneNumber, dateStart, dateEnd,
                durationMin, durationMax, callType);
        }
    }
}
