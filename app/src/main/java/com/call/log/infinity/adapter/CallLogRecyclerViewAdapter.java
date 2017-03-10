package com.call.log.infinity.adapter;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.call.log.infinity.MyApplication;
import com.call.log.infinity.R;
import com.call.log.infinity.activity.CallLogDetailActivity;
import com.call.log.infinity.constant.ViewPagerPosition;
import com.call.log.infinity.model.CallLogItemModel;
import com.call.log.infinity.utils.MaterialDesignColor;
import com.call.log.infinity.utils.PermissionUtil;

import java.util.List;
import java.util.Random;

/**
 * Created by Yong on 2017/2/11.
 */

public class CallLogRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "CallLogRecyclerViewAdapter";

    private Context mContext;
    private List<CallLogItemModel> mCallLogList;
    //在ViewPager中的位置
    private int mPosition;

    //对应空白item类型
    private static final int VIEW_TYPE_BLANK = 0;
    //对应通话记录item类型
    private static final int VIEW_TYPE_ITEM = 1;

    //空白item数目
    private static final int COUNT_BLACK_ITEM = 1;


    public CallLogRecyclerViewAdapter(Context context, List<CallLogItemModel> callLogList, int position) {
        mContext = context;
        mCallLogList = callLogList;
        mPosition = position;
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
        holder.callLogItemView.findViewById(R.id.more_info_layout).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDetailActivity(holder.contactsName, holder.phoneNumber);
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
                                intent.setData(Uri.parse("tel:" + holder.phoneNumber));
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
                    startDetailActivity(holder.contactsName, holder.phoneNumber);
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

        ((CallLogItemViewHolder) holder).setPhoneNumber(callLogItem.getPhoneNumber());
        ((CallLogItemViewHolder) holder).setContactsName(callLogItem.getContactsName());

        //设置联系人头像
        setContactsImage(((CallLogItemViewHolder) holder), callLogItem);

        //通话次数信息
        String countsString = "";
        if (callLogItem.getCallCounts() != 1) {
            String callCountsMessage = MyApplication.getContext().getString(R.string.callCountsMessage);
            countsString = String.format(callCountsMessage, callLogItem.getCallCounts());
        }

        //电话号码信息
        String phoneNumberString = "";
        if (callLogItem.getPhoneNumber().equals(callLogItem.getContactsName())) {
            ((CallLogItemViewHolder) holder).contactsNameTV.setText(callLogItem.getPhoneNumberFormat());
        } else {
            ((CallLogItemViewHolder) holder).contactsNameTV.setText(callLogItem.getContactsName());
            phoneNumberString = callLogItem.getPhoneNumberFormat();
        }

        String countsPhoneNumberAreaOperatorText = MyApplication.getContext()
            .getString(R.string.countsPhoneNumberAreaOperatorText);

        String formatString = String.format(countsPhoneNumberAreaOperatorText,
            countsString, phoneNumberString, callLogItem.getCallerLoc(), callLogItem.getOperator());

        ((CallLogItemViewHolder) holder).countsNumberAreaOperator.setText(formatString.trim());

        //设置箭头标志
        boolean isMissed = (callLogItem.getCallType() == CallLog.Calls.MISSED_TYPE) ||
            ("0".equals(callLogItem.getDuration()) && callLogItem.getCallType() == CallLog.Calls.INCOMING_TYPE);
        if (isMissed) {
            //未接电话
            ((CallLogItemViewHolder) holder).callTypeImage.setImageResource(R.drawable.ic_call_missed);
        } else if (callLogItem.getCallType() == CallLog.Calls.OUTGOING_TYPE) {
            //拨出电话
            if ("0".equals(callLogItem.getDuration())) {
                ((CallLogItemViewHolder) holder).callTypeImage.setImageResource(R.drawable.ic_call_failed);
            } else {
                ((CallLogItemViewHolder) holder).callTypeImage.setImageResource(R.drawable.ic_call_made);
            }
        } else {
            //接入电话
            ((CallLogItemViewHolder) holder).callTypeImage
                .setImageResource(R.drawable.ic_call_received);
        }

        //设置通话时间
        ((CallLogItemViewHolder) holder).callDateTV.setText(callLogItem.getDateFormat());

        //符合主题颜色
        ImageView moreInfoImageView = (ImageView) ((CallLogItemViewHolder) holder)
            .callLogItemView.findViewById(R.id.more_info);
        moreInfoImageView.setColorFilter(MyApplication.getThemeColorPrimaryLight());
    }

    /**
     * 设置联系人头像：背景色和字符
     * @param holder
     * @param callLogItemModel
     */
    private void setContactsImage(CallLogItemViewHolder holder, CallLogItemModel callLogItemModel) {

        String name = callLogItemModel.getContactsName();

        char firstChar = name.charAt(0);

        //数字不显示字符，其余显示第一个字符
        if (firstChar >= '0' && firstChar <= '9') {
            holder.contactsImage.setImageResource(R.mipmap.ic_person);
            //这个置空必须有，否则会因为缓存导致混乱？
            holder.contactsImageText.setText("");
        } else {
            holder.contactsImageText.setText(String.valueOf(firstChar));

            String phoneNumberStr = callLogItemModel.getPhoneNumber();

            //截取电话号码后两位，通过计算余数来设定头像
            int cutLength = 2;
            int headImageIndex;

            if (phoneNumberStr != null &&  phoneNumberStr.length() >= cutLength) {
                int phoneNumberLastTwo = Integer.parseInt(phoneNumberStr
                    .substring(phoneNumberStr.length() - cutLength));
                headImageIndex = phoneNumberLastTwo % MaterialDesignColor.MDColorsDeep.length;
            } else {
                Random random = new Random();
                headImageIndex = random.nextInt(MaterialDesignColor.MDColorsDeep.length);
            }

            GradientDrawable circleBackground = new GradientDrawable();
            circleBackground.setShape(GradientDrawable.OVAL);
            circleBackground.setColor(MaterialDesignColor.MDColorsDeep[headImageIndex]);

            holder.contactsImage.setImageDrawable(circleBackground);
        }
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
    private void startDetailActivity(@NonNull String contactsName, @NonNull String phoneNumber) {
        Intent intent = new Intent(mContext, CallLogDetailActivity.class);
        intent.putExtra(CallLogDetailActivity.CONTACTS_NAME, contactsName);
        intent.putExtra(CallLogDetailActivity.PHONE_NUMBER, phoneNumber);
        intent.putExtra(ViewPagerPosition.POSITION, mPosition);
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

            blankView = view.findViewById(R.id.blank_view);
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
        TextView countsNumberAreaOperator; //通话次数、电话号码等信息
        TextView callDateTV;         //通话发生时间
        ImageView callTypeImage;     //通话类型对应的图片

        String contactsName;
        String phoneNumber;

        public CallLogItemViewHolder(View view) {
            super(view);
            callLogItemView = view;

            contactsImage = (ImageView) view.findViewById(R.id.contacts_image);
            contactsImageText = (TextView) view.findViewById(R.id.contacts_image_text);
            contactsNameTV = (TextView) view.findViewById(R.id.contacts_name);
            countsNumberAreaOperator = (TextView) view.findViewById(R.id.counts_number_area_operator);
            callDateTV     = (TextView) view.findViewById(R.id.call_date);
            callTypeImage  = (ImageView) view.findViewById(R.id.call_type_image);
        }

        public void setContactsName(String name) {
            contactsName = name;
        }

        public void setPhoneNumber(String number) {
            phoneNumber = number;
        }
    }

}
