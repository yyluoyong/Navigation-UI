package com.call.log.infinity.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.provider.CallLog;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.database.CallLogModelDBFlow;
import com.call.log.infinity.utils.CallDateFormatter;
import com.call.log.infinity.utils.CallDurationFormatter;
import com.call.log.infinity.utils.PermissionUtil;
import com.call.log.infinity.utils.PhoneNumberFormatter;

import java.util.List;

/**
 * Created by Yong on 2017/2/23.
 */

public class CallLogDetailRecyclerViewAdapter extends
    RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final String TAG = "CallLogRecyclerViewAdapter";

    private Context mContext;

    //对应电话号码类型
    private static final int VIEW_TYPE_PHONE_ITEM = 0;
    //对应中间分隔的字符类型
    private static final int VIEW_TYPE_STRING = 1;
    //对应通话记录类型
    private static final int VIEW_TYPE_CALL_ITEM = 2;

    //电话号码列表
    private List<PhoneNumberItemModel> mPhoneNumberList;
    //通话记录列表
    private List<CallLogModelDBFlow> mCallLogList;
    //字符item数目
    private static final int COUNT_STRING_ITEM = 1;

    private static final String CALL_MADE_IN = MyApplication.getContext().getString(R.string.callInStringName);
    private static final String CALL_MADE_OUT = MyApplication.getContext().getString(R.string.callOutStringName);
    private static final String CALL_MISSED_STRING = MyApplication.getContext().getString(R.string.callMissed);
    private static final String CALL_MADE_FAILED_STRING = MyApplication.getContext().getString(R.string.callOutFailed);

    /**
     * 电话条目数据的Bean
     */
    public static class PhoneNumberItemModel {
        //电话号码
        private String phoneNumber;
        //电话号码归属地
        private String callerLoc;
        //所属运营商
        private String phoneOperator;

        //格式化的电话号码
        private String phoneNumberFormat;

        public PhoneNumberItemModel(String phoneNumber, String callerLoc, String phoneOperator) {
            setPhoneNumber(phoneNumber);
            setCallerLoc(callerLoc);
            setPhoneOperator(phoneOperator);
        }

        public void setPhoneNumber(String phoneNumber) {
            setPhoneNumberFormat(PhoneNumberFormatter.phoneNumberFormat(phoneNumber));
            this.phoneNumber = phoneNumber;
        }

        public String getCallerLoc() {
            return callerLoc;
        }

        public void setCallerLoc(String callerLoc) {
            this.callerLoc = callerLoc;
        }

        public String getPhoneOperator() {
            return phoneOperator;
        }

        public void setPhoneOperator(String phoneOperator) {
            this.phoneOperator = phoneOperator;
        }

        public String getPhoneNumberFormat() {
            return phoneNumberFormat;
        }

        private void setPhoneNumberFormat(String phoneNumberFormat) {
            this.phoneNumberFormat = phoneNumberFormat;
        }
    }

    public CallLogDetailRecyclerViewAdapter(Context context,
        List<PhoneNumberItemModel> phoneNumberList, List<CallLogModelDBFlow> callLogList) {

        mContext = context;
        mCallLogList = callLogList;
        mPhoneNumberList = phoneNumberList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        /**
         * 电话号码item
         */
        if (viewType == VIEW_TYPE_PHONE_ITEM) {
            final View view = LayoutInflater.from(mContext)
                .inflate(R.layout.phone_number_item, parent, false);

            final PhoneNumberItemViewHolder holder = new PhoneNumberItemViewHolder(view);

            //拨打电话
            setPhoneNumberItemListener(view, holder);

            return holder;
        }

        /**
         * 字符item
         */
        if (viewType == VIEW_TYPE_STRING) {
            final View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recent_call_string, parent, false);

            final StringViewHolder holder = new StringViewHolder(view);

            //显示通话记录总数量
            setStringItemListener(view, holder);

            return holder;
        }

        /**
         * 通话记录item
         */
        final View view = LayoutInflater.from(mContext)
            .inflate(R.layout.calllog_detail_item, parent, false);

        final DetailItemViewHolder holder = new DetailItemViewHolder(view);

        //拨打电话
        setCallLogItemListener(view, holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        /**
         * 电话号码item
         */
        if (position < mPhoneNumberList.size()) {

            PhoneNumberItemModel phoneNumberItem = mPhoneNumberList.get(position);

            ((PhoneNumberItemViewHolder) holder).phoneNumberTV
                .setText(phoneNumberItem.getPhoneNumberFormat());

            ((PhoneNumberItemViewHolder) holder).callerLocTV
                .setText(phoneNumberItem.getCallerLoc());

            ((PhoneNumberItemViewHolder) holder).phoneOperatorTV
                .setText(phoneNumberItem.getPhoneOperator());

            if (position == 0) {
                ((PhoneNumberItemViewHolder) holder).callImage
                    .setImageResource(R.drawable.phone_image);
                ((PhoneNumberItemViewHolder) holder).callImage
                    .setColorFilter(MyApplication.getThemeColorPrimary());
            }

            return;
        }

        /**
         * 字符item
         */
        if (position == mPhoneNumberList.size() ) {
            ((StringViewHolder) holder).blankView.setTextColor(MyApplication.getThemeColorPrimary());
            return;
        }

        /**
         * 通话记录item
         */
        //除去电话列表的长度和一个字符项
        int mPosition = position - mPhoneNumberList.size() - COUNT_STRING_ITEM;

        CallLogModelDBFlow callLogItem = mCallLogList.get(mPosition);

        ((DetailItemViewHolder) holder).phoneNumberTV
            .setText(PhoneNumberFormatter.phoneNumberFormat(callLogItem.getPhoneNumber()));

        ((DetailItemViewHolder) holder).callDateTV
            .setText(CallDateFormatter.format(callLogItem.getDateInMilliseconds()));

        String durationString;
        boolean isMissed = (callLogItem.getCallType() == CallLog.Calls.MISSED_TYPE) ||
            ("0".equals(callLogItem.getDuration()) && callLogItem.getCallType() == CallLog.Calls.INCOMING_TYPE);
        if (isMissed) {
            //未接电话
            ((DetailItemViewHolder) holder).callTypeImage.setImageResource(R.drawable.ic_call_missed);
            ((DetailItemViewHolder) holder).callDurationTV.setText(CALL_MISSED_STRING);
        } else if (callLogItem.getCallType() == CallLog.Calls.OUTGOING_TYPE) {
            //拨出电话
            if ("0".equals(callLogItem.getDuration())) {
                ((DetailItemViewHolder) holder).callTypeImage.setImageResource(R.drawable.ic_call_failed);
                ((DetailItemViewHolder) holder).callDurationTV.setText(CALL_MADE_FAILED_STRING);
            } else {
                ((DetailItemViewHolder) holder).callTypeImage.setImageResource(R.drawable.ic_call_made);
                durationString = String.format(MyApplication.getContext().getString(R.string.callDuration),
                    CALL_MADE_OUT, CallDurationFormatter.format(callLogItem.getDuration()));
                ((DetailItemViewHolder) holder).callDurationTV.setText(durationString);
            }
        } else {
            //接入电话
            ((DetailItemViewHolder) holder).callTypeImage.setImageResource(R.drawable.ic_call_received);

            durationString = String.format(MyApplication.getContext().getString(R.string.callDuration),
                CALL_MADE_IN, CallDurationFormatter.format(callLogItem.getDuration()));
            ((DetailItemViewHolder) holder).callDurationTV.setText(durationString);
        }

        //设置电话标志的颜色
        ImageView callImageView = (ImageView) ((DetailItemViewHolder) holder).callLogItemView
            .findViewById(R.id.callImageView);
        callImageView.setColorFilter(MyApplication.getThemeColorPrimary());

    }

    /**
     * 电话号码条目数量 + 字符条目数量 + 通话记录条目数量
     * @return
     */
    @Override
    public int getItemCount() {
        return mPhoneNumberList.size() + mCallLogList.size() + COUNT_STRING_ITEM;
    }

    /**
     * 根据位置返回item类型
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position < mPhoneNumberList.size()) {
            return VIEW_TYPE_PHONE_ITEM;
        }

        if (position == mPhoneNumberList.size()) {
            return VIEW_TYPE_STRING;
        }

        return VIEW_TYPE_CALL_ITEM;
    }

    public void setPhoneNumberList(List<PhoneNumberItemModel> mPhoneNumberList) {
        this.mPhoneNumberList = mPhoneNumberList;
    }

    public void setCallLogList(List<CallLogModelDBFlow> mCallLogList) {
        this.mCallLogList = mCallLogList;
    }

    /**
     * 为电话项设置监听
     * @param view
     * @param holder
     */
    private void setPhoneNumberItemListener(final View view, final PhoneNumberItemViewHolder holder) {
        //拨打电话
        holder.callLogItemView.findViewById(R.id.item_linear_layout).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PermissionUtil.requestPermissions(mContext,
                        PermissionUtil.REQUEST_CODE, new String[]{Manifest.permission.CALL_PHONE},
                        new PermissionUtil.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted() {

                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + holder.phoneNumberTV.getText()
                                    .toString().replace(PhoneNumberFormatter.DELIMITER, "")));
                                mContext.startActivity(intent);
                            }

                            /**
                             * 见PermissionUtils类的“说明一”
                             */
                            @Override
                            public void onPermissionDenied() {
                                Snackbar.make(view, "您拒绝了权限申请，功能无法使用",
                                    Snackbar.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        );

        //发送短信
        holder.callLogItemView.findViewById(R.id.sms_image).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermissionUtil.requestPermissions(mContext,
                        PermissionUtil.REQUEST_CODE, new String[]{Manifest.permission.CALL_PHONE},
                        new PermissionUtil.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted() {

                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("smsto:" + holder.phoneNumberTV.getText()
                                    .toString().replace(PhoneNumberFormatter.DELIMITER, "")));
                                mContext.startActivity(intent);
                            }

                            /**
                             * 见PermissionUtils类的“说明一”
                             */
                            @Override
                            public void onPermissionDenied() {
                                Snackbar.make(view, "您拒绝了权限申请，功能无法使用",
                                    Snackbar.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        );
    }

    /**
     * 为通话记录项设置监听
     * @param view
     * @param holder
     */
    private void setCallLogItemListener(final View view, final DetailItemViewHolder holder) {
        //拨打电话
        holder.callLogItemView.findViewById(R.id.layout_item).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PermissionUtil.requestPermissions(mContext,
                        PermissionUtil.REQUEST_CODE, new String[]{Manifest.permission.CALL_PHONE},
                        new PermissionUtil.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted() {

                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + holder.phoneNumberTV.getText()
                                    .toString().replace(PhoneNumberFormatter.DELIMITER, "")));
                                mContext.startActivity(intent);
                            }

                            /**
                             * 见PermissionUtils类的“说明一”
                             */
                            @Override
                            public void onPermissionDenied() {
                                Snackbar.make(view, "您拒绝了权限申请，功能无法使用",
                                    Snackbar.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        );
    }

    /**
     * 为字符项设置监听
     * @param view
     * @param holder
     */
    private void setStringItemListener(final View view, final StringViewHolder holder) {
        holder.blankView.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(view, "通话记录总数为：" + mCallLogList.size(),
                        Snackbar.LENGTH_SHORT).show();
                }
            }
        );
    }

    /**
     * 内部类，对应布局文件 phone_number_item.xml，即通话列表每个单项
     */
    private static class PhoneNumberItemViewHolder extends RecyclerView.ViewHolder {
        View callLogItemView;

        TextView phoneNumberTV;      //电话号码
        TextView callerLocTV;        //归属地
        TextView phoneOperatorTV;    //运营商
        ImageView callImage;         //电话标志图片

        public PhoneNumberItemViewHolder(View view) {
            super(view);
            callLogItemView = view;

            phoneNumberTV = (TextView) view.findViewById(R.id.phone_number);
            callerLocTV = (TextView) view.findViewById(R.id.caller_loc);
            phoneOperatorTV = (TextView) view.findViewById(R.id.phone_operator);

            callImage = (ImageView) view.findViewById(R.id.call_image);
        }
    }

    /**
     * 内部类，对应布局文件 calllog_detail_item.xml，即电话号码列表每个单项
     */
    private static class DetailItemViewHolder extends RecyclerView.ViewHolder {
        View callLogItemView;

        TextView callDateTV;         //通话发生时间
        ImageView callTypeImage;     //通话类型对应的图片
        TextView phoneNumberTV;      //电话号码
        TextView callDurationTV;     //通话时长

        public DetailItemViewHolder(View view) {
            super(view);
            callLogItemView = view;

            callDateTV = (TextView) view.findViewById(R.id.call_date);
            callTypeImage = (ImageView) view.findViewById(R.id.call_type_image);
            phoneNumberTV = (TextView) view.findViewById(R.id.phone_number);
            callDurationTV = (TextView) view.findViewById(R.id.call_duration);
        }
    }

    /**
     * 内部类，对应布局文件 recent_call_string.xml，用于在电话列表和通话记录列表之间
     * 添加字符串。
     */
    private static class StringViewHolder extends RecyclerView.ViewHolder {
        TextView blankView;

        public StringViewHolder(View view) {
            super(view);

            blankView = (TextView) view.findViewById(R.id.recent_call_tv);
        }
    }
}