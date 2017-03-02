package com.navigation_ui.adapter;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.navigation_ui.R;
import com.navigation_ui.activity.CallLogDetailActivity;
import com.navigation_ui.model.CallLogItemModel;
import com.navigation_ui.utils.MaterialDesignColor;
import com.navigation_ui.utils.PermissionUtil;
import com.navigation_ui.utils.PhoneNumberFormatter;

import java.util.List;
import java.util.Random;

/**
 * Created by Yong on 2017/2/11.
 */

public class CallLogRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "CallLogRecyclerViewAdapter";

    private List<CallLogItemModel> mCallLogList;

    //对应空白item类型
    private static final int VIEW_TYPE_BLANK = 0;
    //对应通话记录item类型
    private static final int VIEW_TYPE_ITEM = 1;

    //空白item数目
    private static final int COUNT_BLACK_ITEM = 1;

    private Context mContext;

    public CallLogRecyclerViewAdapter(Context context, List<CallLogItemModel> callLogList) {
        mContext = context;
        mCallLogList = callLogList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        /**
         * 空白item
         */
        if (viewType == VIEW_TYPE_BLANK) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.blank_view, parent, false);
            return new BlankViewHolder(view);
        }

        /**
         * 通话记录item
         */
        final View view = LayoutInflater.from(mContext).inflate(R.layout.main_calllog_item, parent, false);

        final CallLogItemViewHolder holder = new CallLogItemViewHolder(view);

        //展示通话详情界面
        holder.callLogItemView.findViewById(R.id.more_info).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDetailActivity(holder.contactsNameTV.getText().toString());
                }
            }
        );

        //拨打条目的电话。
        holder.callLogItemView.findViewById(R.id.calllog_detail_linearlayout).setOnClickListener(
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

        //展示通话详情界面
        holder.contactsImage.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDetailActivity(holder.contactsNameTV.getText().toString());
                }
            }
        );

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //开始出的空白
        if (position == 0) {
            return;
        }

        //除去空白项
        int mPosition = position - COUNT_BLACK_ITEM;

        CallLogItemModel callLogItem = mCallLogList.get(mPosition);

        //联系人为空，数据库将其设置成了电话号码。
        if (callLogItem.getPhoneNumber().equals(callLogItem.getContactsName())) {
            ((CallLogItemViewHolder) holder).contactsNameTV.setText(callLogItem.getPhoneNumberFormat());
        } else {
            ((CallLogItemViewHolder) holder).contactsNameTV.setText(callLogItem.getContactsName());
        }


        ((CallLogItemViewHolder) holder).phoneNumberTV.setText(callLogItem.getPhoneNumberFormat());

        ((CallLogItemViewHolder) holder).callDateTV.setText(callLogItem.getDateFormat());

        ((CallLogItemViewHolder) holder).callCountsTV.setText("("+callLogItem.getCallCounts()+")");

        if (callLogItem.getCallType() == CallLog.Calls.INCOMING_TYPE) {
            ((CallLogItemViewHolder) holder).callTypeImage
                .setImageResource(R.drawable.ic_call_made);
        } else if (callLogItem.getCallType() == CallLog.Calls.OUTGOING_TYPE) {
            ((CallLogItemViewHolder) holder).callTypeImage
                .setImageResource(R.drawable.ic_call_received);
        } else {
            ((CallLogItemViewHolder) holder).callTypeImage
                .setImageResource(R.drawable.ic_call_missed);
        }

        ((CallLogItemViewHolder) holder).callerLocTV.setText(callLogItem.getCallerLoc());
        ((CallLogItemViewHolder) holder).operatorTV.setText(callLogItem.getOperator());

        setContactsImage(((CallLogItemViewHolder) holder), mPosition);
    }


    /**
     * 设置联系人头像：背景色和字符
     * @param holder
     * @param position
     */
    private void setContactsImage(CallLogItemViewHolder holder, int position) {

        char firstChar = mCallLogList.get(position).toString().charAt(0);

        holder.contactsImage.setImageResource(R.mipmap.ic_person);

        return;

        //数字不显示字符，其余显示第一个字符
//        if (firstChar >= '0' && firstChar <= '9') {
//            holder.contactsImage.setImageResource(R.mipmap.ic_person);
//        } else {
//            holder.contactsImageText.setText(String.valueOf(firstChar));
//
//            String phoneNumberStr = holder.phoneNumberTV.getText().toString()
//                .replace(PhoneNumberFormatter.DELIMITER, "");
//
//            //截取电话号码后两位，通过计算余数来设定头像
//            int cutLength = 2;
//            int headImageIndex;
//
//            if (phoneNumberStr != null &&  phoneNumberStr.length() >= cutLength) {
//                int phoneNumberLastTwo = Integer.parseInt(phoneNumberStr
//                    .substring(phoneNumberStr.length() - cutLength));
//                headImageIndex = phoneNumberLastTwo % MaterialDesignColor.MDColorsDeep.length;
//            } else {
//                Random random = new Random();
//                headImageIndex = random.nextInt(MaterialDesignColor.MDColorsDeep.length);
//            }
//
//            ((GradientDrawable) holder.contactsImage.getDrawable())
//                .setColor(MaterialDesignColor.MDColorsDeep[headImageIndex]);
//        }
    }

    /**
     * 一个空白栏 + 通话记录条目数量
     * @return
     */
    @Override
    public int getItemCount() {
        return mCallLogList.size() + COUNT_BLACK_ITEM;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_BLANK;
        }

        return VIEW_TYPE_ITEM;
    }

    public void setCallLogList(List<CallLogItemModel> mCallLogList) {
        this.mCallLogList = mCallLogList;
    }

    /**
     * 打开通话详情页面的Activity
     * @param contactsName
     */
    private void startDetailActivity(@NonNull String contactsName) {
        Intent intent = new Intent(mContext, CallLogDetailActivity.class);
        intent.putExtra(CallLogDetailActivity.CONTACTS_NAME, contactsName);
        intent.putExtra(CallLogDetailActivity.CALL_TYPE, CallLogDetailActivity.CALL_ALL);
        mContext.startActivity(intent);
    }

    /**
     * 内部类，对应布局文件 blank_view.xml，用于在整个列表之前添加一个小空白，
     * 不然列表第一项与顶部距离太短。
     */
    private static class BlankViewHolder extends RecyclerView.ViewHolder {
        View blankView;

        public BlankViewHolder(View view) {
            super(view);

            blankView = (View) view.findViewById(R.id.blank_view);
        }
    }

    /**
     * 内部类，对应布局文件 main_calllog_item.xml，即通话列表每个单项
     */
    private static class CallLogItemViewHolder extends RecyclerView.ViewHolder {
        View callLogItemView;

        ImageView contactsImage;     //联系人头像
        TextView contactsImageText;  //联系人头像上的文字
        TextView contactsNameTV;     //联系人
        TextView phoneNumberTV;      //电话号码
        TextView callDateTV;         //通话发生时间
        TextView callCountsTV;       //通话次数
        ImageView callTypeImage;     //通话类型对应的图片
        TextView callerLocTV;        //归属地
        TextView operatorTV;         //运营商

        public CallLogItemViewHolder(View view) {
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
            operatorTV = (TextView) view.findViewById(R.id.operator);
        }
    }

}
