package com.navigation_ui.adapter;

import android.content.Context;
import android.provider.CallLog;
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
 * Created by Yong on 2017/2/23.
 */

public class DetailRecyclerViewAdapter extends
    RecyclerView.Adapter<DetailRecyclerViewAdapter.ViewHolder> {

    private String TAG = "CallLogRecyclerViewAdapter";

    private List<CallLogItemModel> mCallLogList;
    /**
     * 内部类，对应布局文件 calllog_item.xml，即通话列表每个单项
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        View callLogItemView;

        TextView callDateTV;         //通话发生时间
        ImageView callTypeImage;     //通话类型对应的图片
        TextView phoneNumberTV;      //电话号码
        TextView callDurationTV;     //通话时长

        public ViewHolder(View view) {
            super(view);
            callLogItemView = view;

            callDateTV = (TextView) view.findViewById(R.id.call_date);
            callTypeImage = (ImageView) view.findViewById(R.id.call_type_image);
            phoneNumberTV = (TextView) view.findViewById(R.id.phone_number);
            callDurationTV = (TextView) view.findViewById(R.id.call_duration);
        }
    }

    public DetailRecyclerViewAdapter(List<CallLogItemModel> callLogList) {
        mCallLogList = callLogList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context mContext = parent.getContext();

        View view = LayoutInflater.from(mContext).inflate(R.layout.calllog_detail_item, parent, false);

        final ViewHolder holder = new ViewHolder(view);

        //undo：添加监听

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CallLogItemModel callLogItem = mCallLogList.get(position);

        holder.phoneNumberTV.setText(PhoneNumberFormatter
            .phoneNumberFormat(callLogItem.getPhoneNumber()));

        holder.callDateTV.setText(CallDateFormatter
            .format(callLogItem.getDateInMilliseconds()));

        if (callLogItem.getCallType() == CallLog.Calls.INCOMING_TYPE) {
            holder.callTypeImage.setImageResource(R.drawable.ic_call_made);
        } else if (callLogItem.getCallType() == CallLog.Calls.OUTGOING_TYPE) {
            holder.callTypeImage.setImageResource(R.drawable.ic_call_received);
        } else {
//            LogUtil.d(TAG, "设置");
            holder.callTypeImage.setImageResource(R.drawable.ic_call_missed);
        }

        holder.callDurationTV.setText("呼入2分");

    }

    @Override
    public int getItemCount() {
        return mCallLogList.size();
    }

    public void setCallLogList(List<CallLogItemModel> mCallLogList) {
        this.mCallLogList = mCallLogList;
    }
}