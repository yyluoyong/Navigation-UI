package com.navigation_ui.adapter;


import android.provider.CallLog;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.navigation_ui.R;
import com.navigation_ui.model.CallLogItemModel;
import com.navigation_ui.tools.CallDateFormatter;
import com.navigation_ui.tools.PhoneNumberFormatter;

import java.util.List;

/**
 * Created by Yong on 2017/2/11.
 */

public class CallLogRecyclerViewAdapter extends RecyclerView.Adapter<CallLogRecyclerViewAdapter.ViewHolder> {

    private List<CallLogItemModel> mCallLogList;

    /**
     * 内部类，对应布局文件 calllog_item.xml，即通话列表每个单项
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        View callLogItemView;

        ImageView contactsImage;  //联系人头像
        TextView contactsNameTV;  //联系人
        TextView phoneNumberTV;   //电话号码
        TextView callDateTV;      //通话发生时间
        TextView callCountsTV;    //通话次数
        ImageView callTypeImage;  //通话类型对应的图片
        TextView callerLocTV;     //归属地

        public ViewHolder(View view) {
            super(view);
            callLogItemView = view;

            contactsImage  = (ImageView) view.findViewById(R.id.contacts_image);
            contactsNameTV = (TextView) view.findViewById(R.id.contacts_name);
            phoneNumberTV  = (TextView) view.findViewById(R.id.phone_number);
            callDateTV     = (TextView) view.findViewById(R.id.call_date);
            callCountsTV   = (TextView) view.findViewById(R.id.call_counts);
            callTypeImage  = (ImageView) view.findViewById(R.id.call_type_image);
            callerLocTV    = (TextView) view.findViewById(R.id.caller_loc);
        }
    }

    public CallLogRecyclerViewAdapter(List<CallLogItemModel> callLogList) {
        mCallLogList = callLogList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calllog_item, parent, false);

        final ViewHolder holder = new ViewHolder(view);

        //undo:为单项上更多信息添加监听，计划是展示与该联系人历史通话详细信息。
        holder.callLogItemView.findViewById(R.id.more_info).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "点击了Info按钮，功能待完善.", Snackbar.LENGTH_SHORT).show();
                }
            }
        );

        //undo：为单项添加监听，计划是拨打条目的电话。
        holder.callLogItemView.findViewById(R.id.calllog_detail_linearlayout).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "拨打电话："+holder.phoneNumberTV.getText()
                        .toString().replace(" ", ""),
                        Snackbar.LENGTH_SHORT).show();
                }
            }
        );

        holder.contactsImage.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "点击了联系人头像，功能待完善.", Snackbar.LENGTH_SHORT).show();
                }
            }
        );

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CallLogItemModel callLogItem = mCallLogList.get(position);

        holder.contactsNameTV.setText(callLogItem.getContactsName());

        holder.phoneNumberTV.setText(PhoneNumberFormatter
            .phoneNumberFormat(callLogItem.getPhoneNumber()));

        holder.callDateTV.setText(CallDateFormatter
            .format(callLogItem.getDateInMilliseconds()));

        holder.callCountsTV.setText("("+callLogItem.getCallCounts()+")");

        if (callLogItem.getCallType() == CallLog.Calls.INCOMING_TYPE) {
            holder.callTypeImage.setImageResource(R.mipmap.ic_call_made_grey600_18dp);
        } else if (callLogItem.getCallType() == CallLog.Calls.OUTGOING_TYPE) {
            holder.callTypeImage.setImageResource(R.mipmap.ic_call_received_grey600_18dp);
        } else {
            holder.callTypeImage.setImageResource(R.mipmap.ic_call_missed_grey600_18dp);
        }

        holder.callerLocTV.setText(callLogItem.getCallerLoc());
    }

    @Override
    public int getItemCount() {
        return mCallLogList.size();
    }

    public void setCallLogList(List<CallLogItemModel> mCallLogList) {
        this.mCallLogList = mCallLogList;
    }
}
