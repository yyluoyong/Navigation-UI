package com.navigation_ui.adapter;


import android.graphics.drawable.GradientDrawable;
import android.provider.CallLog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.navigation_ui.R;
import com.navigation_ui.model.CallLogItemModel;
import com.navigation_ui.tools.CallDateFormatter;
import com.navigation_ui.tools.LogUtil;
import com.navigation_ui.tools.MaterialDesignColor;
import com.navigation_ui.tools.MyApplication;
import com.navigation_ui.tools.PhoneNumberFormatter;

import java.util.List;
import java.util.Random;

/**
 * Created by Yong on 2017/2/11.
 */

public class CallLogRecyclerViewAdapter extends RecyclerView.Adapter<CallLogRecyclerViewAdapter.ViewHolder> {

    private String TAG = "CallLogRecyclerViewAdapter";

    private List<CallLogItemModel> mCallLogList;

    /**
     * 内部类，对应布局文件 calllog_item.xml，即通话列表每个单项
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        View callLogItemView;

        ImageView contactsImage;     //联系人头像
        TextView contactsImageText;  //联系人头像上的文字
        TextView contactsNameTV;     //联系人
        TextView phoneNumberTV;      //电话号码
        TextView callDateTV;         //通话发生时间
        TextView callCountsTV;       //通话次数
        ImageView callTypeImage;     //通话类型对应的图片
        TextView callerLocTV;        //归属地

        public ViewHolder(View view) {
            super(view);
            callLogItemView = view;

            contactsImage = (ImageView) view.findViewById(R.id.contacts_image);
            contactsImageText = (TextView) view.findViewById(R.id.contacts_image_text);
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
            holder.callTypeImage.setImageResource(R.drawable.ic_call_made);
        } else if (callLogItem.getCallType() == CallLog.Calls.OUTGOING_TYPE) {
            holder.callTypeImage.setImageResource(R.drawable.ic_call_received);
        } else {
//            LogUtil.d(TAG, "设置");
            holder.callTypeImage.setImageResource(R.drawable.ic_missed);
        }

        holder.callerLocTV.setText(callLogItem.getCallerLoc());

        setContactsImage(holder, position);
    }


    /**
     * 设置联系人头像：背景色和字符
     * @param holder
     * @param position
     */
    private void setContactsImage(ViewHolder holder, int position) {

        char firstChar = mCallLogList.get(position).toString().charAt(0);

        //数字不显示字符，其余显示第一个字符
        if (firstChar >= '0' && firstChar <= '9') {
            holder.contactsImage.setImageResource(R.mipmap.ic_person);
        } else {
            holder.contactsImageText.setText(String.valueOf(firstChar));

            //undo：颜色根据联系人通话信息确定
            Random random = new Random();
            ((GradientDrawable) holder.contactsImage.getDrawable())
                .setColor(MaterialDesignColor
                    .MDColorsDeep[random.nextInt(MaterialDesignColor.MDColorsDeep.length)]);
        }


    }

    @Override
    public int getItemCount() {
        return mCallLogList.size();
    }

    public void setCallLogList(List<CallLogItemModel> mCallLogList) {
        this.mCallLogList = mCallLogList;
    }
}
