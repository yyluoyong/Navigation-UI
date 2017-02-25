package com.navigation_ui.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.navigation_ui.R;
import com.navigation_ui.model.CallLogItemModel;
import com.navigation_ui.tools.CallDateFormatter;
import com.navigation_ui.tools.LogUtil;
import com.navigation_ui.tools.PermissionUtils;
import com.navigation_ui.tools.PhoneNumberFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yong on 2017/2/23.
 */

public class CallLogDetailRecyclerViewAdapter extends
    RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "CallLogRecyclerViewAdapter";

    private Context mContext;

    //对应电话号码类型
    private final static int VIEW_TYPE_PHONE_ITEM = 0;
    //对应中间分隔的字符类型
    private final static int VIEW_TYPE_STRING = 1;
    //对应通话记录类型
    private final static int VIEW_TYPE_CALL_ITEM = 2;

    //电话号码列表
    private List<String> mPhoneNumberList;
    //通话记录列表
    private List<CallLogItemModel> mCallLogList;

    private final static int COUNT_STRING_ITEM = 1;

    public CallLogDetailRecyclerViewAdapter(Context context, List<CallLogItemModel> callLogList) {

        mContext = context;

        mCallLogList = callLogList;

        mPhoneNumberList = new ArrayList<String>();
        mPhoneNumberList.add("0816 5516 634");
        mPhoneNumberList.add("155 5566 7788");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //电话号码item
        if (viewType == VIEW_TYPE_PHONE_ITEM) {
            View view = LayoutInflater.from(mContext)
                .inflate(R.layout.phone_number_item, parent, false);

            final PhoneNumberItemViewHolder holder = new PhoneNumberItemViewHolder(view);

            return holder;
        }

        //字符item
        if (viewType == VIEW_TYPE_STRING) {
            View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recent_call_string, parent, false);

            final StringViewHolder holder = new StringViewHolder(view);

            return holder;
        }

        //通话记录item
        View view = LayoutInflater.from(mContext)
            .inflate(R.layout.calllog_detail_item, parent, false);

        final DetailItemViewHolder holder = new DetailItemViewHolder(view);

        holder.callLogItemView.findViewById(R.id.layout_item).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    MakeCallActivity.makeCall(MyApplication.getContext(), "1567639928");
                    PermissionUtils.requestPermissions(mContext,
                        PermissionUtils.REQUEST_CODE, new String[]{Manifest.permission.CALL_PHONE},
                        new PermissionUtils.OnPermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:10000"));
                                mContext.startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(mContext, "你绝拒了权限申请！",
                                    Toast.LENGTH_LONG).show();
                                LogUtil.d(TAG, "权限申请失败!");
                            }
                        });
                }
            }
        );

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //电话列表项
        if (position < mPhoneNumberList.size()) {
            ((PhoneNumberItemViewHolder) holder).phoneNumberTV
                .setText(mPhoneNumberList.get(position));
            ((PhoneNumberItemViewHolder) holder).callerLocTV.setText("四川绵阳");
            ((PhoneNumberItemViewHolder) holder).phoneOperatorTV.setText("移动");

            if (position == 0) {
                ((PhoneNumberItemViewHolder) holder).callImage
                    .setImageResource(R.drawable.phone_image);
            }

            return;
        }

        //字符项
        if (position == mPhoneNumberList.size() ) {
            return;
        }

        //通话记录项
        //除去电话列表的长度和一个字符项
        int mPosition = position - mPhoneNumberList.size() - COUNT_STRING_ITEM;

        CallLogItemModel callLogItem = mCallLogList.get(mPosition);

        ((DetailItemViewHolder) holder).phoneNumberTV.setText(PhoneNumberFormatter
            .phoneNumberFormat(callLogItem.getPhoneNumber()));

        ((DetailItemViewHolder) holder).callDateTV.setText(CallDateFormatter
            .format(callLogItem.getDateInMilliseconds()));

        if (callLogItem.getCallType() == CallLog.Calls.INCOMING_TYPE) {
            ((DetailItemViewHolder) holder).callTypeImage
                .setImageResource(R.drawable.ic_call_made);
        } else if (callLogItem.getCallType() == CallLog.Calls.OUTGOING_TYPE) {
            ((DetailItemViewHolder) holder).callTypeImage
                .setImageResource(R.drawable.ic_call_received);
        } else {
            ((DetailItemViewHolder) holder).callTypeImage
                .setImageResource(R.drawable.ic_call_missed);
        }

        ((DetailItemViewHolder) holder).callDurationTV.setText("呼入2分");
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

    public void setCallLogList(List<CallLogItemModel> mCallLogList) {
        this.mCallLogList = mCallLogList;
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
        View blankView;

        public StringViewHolder(View view) {
            super(view);

            blankView = (View) view.findViewById(R.id.recent_call_tv);
        }
    }
}