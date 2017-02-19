package com.navigation_ui.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.navigation_ui.R;
import com.navigation_ui.model.CallLogItemModel;

import java.util.List;

/**
 * Created by Yong on 2017/2/11.
 */

public class CallLogRecyclerViewAdapter extends RecyclerView.Adapter<CallLogRecyclerViewAdapter.ViewHolder> {

    private List<CallLogItemModel> mCallLogList;

    /**
     * 内部类，对应布局文件 calllog_item.xml
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        View callLogItemView;

        TextView contactsNameTV;  //联系人
        TextView phoneNumberTV;   //电话号码
        TextView callDateTV;      //通话发生时间
        TextView callCountsTV;    //通话次数
//        TextView durationTV;
//        TextView callTypeTV;
        ImageView callTypeImage;  //通话类型对应的图片
        TextView callerLocTV;     //归属地

        public ViewHolder(View view) {
            super(view);
            callLogItemView = view;

            contactsNameTV = (TextView) view.findViewById(R.id.contacts_name);
            phoneNumberTV  = (TextView) view.findViewById(R.id.phone_number);
            callDateTV     = (TextView) view.findViewById(R.id.call_date);
            callCountsTV   = (TextView) view.findViewById(R.id.call_counts);
//            durationTV     = (TextView) view.findViewById(R.id.duration);
//            callTypeTV     = (TextView) view.findViewById(R.id.call_type);
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

        ViewHolder holder = new ViewHolder(view);

        //undo:为holder的组件添加监听事件

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CallLogItemModel callLogItem = mCallLogList.get(position);

        holder.contactsNameTV.setText(callLogItem.getContactsName());
        holder.phoneNumberTV.setText(callLogItem.getPhoneNumber());
        holder.callDateTV.setText(callLogItem.getDateInStr());
        holder.callCountsTV.setText("("+callLogItem.getCallCounts()+")");
//        holder.durationTV.setText(callLogItem.getDuration());
//        holder.callTypeTV.setText(""+callLogItem.getCallType());
        holder.callTypeImage.setImageResource(R.mipmap.ic_call_missed_grey600_18dp);
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
