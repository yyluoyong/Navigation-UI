package com.call.log.infinity.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.call.log.infinity.R;
import com.call.log.infinity.adapter.CallLogRecyclerViewAdapter;
import com.call.log.infinity.constant.ViewPagerPosition;
import com.call.log.infinity.database.RecentCallLogListUtil;
import com.call.log.infinity.model.CallLogItemModel;
import com.call.log.infinity.utils.LogUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CallLogFragment extends Fragment implements Observer {

    static final String TAG = "CallLogFragment";

    private View mView;
    private RecyclerView mRecyclerView;
    private CallLogRecyclerViewAdapter mRecyclerViewAdapter;

    private List<CallLogItemModel> callLogItemModelList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_calllog, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        int mPosition = getArguments().getInt(ViewPagerPosition.POSITION, ViewPagerPosition.POSITION_ALL_TYPE);
        mRecyclerViewAdapter = new CallLogRecyclerViewAdapter(getContext(),
            callLogItemModelList, mPosition);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        //将自己注册为观察者
        UpdateFragmentObservable.getInstance().addObserver(this);

        //初始化数据
        initCallLogs();

        return mView;
    }

    @Override
    public void onDestroyView() {
        LogUtil.d("Adapter", "onDestroyView");

        //解除注册
        UpdateFragmentObservable.getInstance().deleteObserver(this);
        super.onDestroyView();
    }

    /**
     * 观察者模式响应更新方法
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        callLogItemModelList.clear();

        final ProgressDialog pgDialog = createProgressDialog(null, getString(R.string.readDatabaseIng));
        pgDialog.show();

        int mPosition = getArguments().getInt(ViewPagerPosition.POSITION, ViewPagerPosition.POSITION_ALL_TYPE);

        //所有通话
        if (mPosition == ViewPagerPosition.POSITION_ALL_TYPE) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    callLogItemModelList = new RecentCallLogListUtil()
                        .getRecentCallLogItemList(RecentCallLogListUtil.TYPE_ALL);

                    //更新UI页面
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pgDialog.dismiss();
                            mRecyclerViewAdapter.setCallLogList(callLogItemModelList);
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();

            return;
        }

        //来电
        if (mPosition == ViewPagerPosition.POSITION_INCOMING_TYPE) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    callLogItemModelList = new RecentCallLogListUtil()
                        .getRecentCallLogItemList(CallLog.Calls.INCOMING_TYPE);

                    //更新UI页面
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pgDialog.dismiss();
                            mRecyclerViewAdapter.setCallLogList(callLogItemModelList);
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();

            return;
        }

        //去电
        if (mPosition == ViewPagerPosition.POSITION_OUTGOING_TYPE) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    callLogItemModelList = new RecentCallLogListUtil()
                        .getRecentCallLogItemList(CallLog.Calls.OUTGOING_TYPE);

                    //更新UI页面
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pgDialog.dismiss();
                            mRecyclerViewAdapter.setCallLogList(callLogItemModelList);
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();

            return;
        }

        //未接
        if (mPosition == ViewPagerPosition.POSITION_MISSED_TYPE) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    callLogItemModelList = new RecentCallLogListUtil()
                        .getRecentCallLogItemList(CallLog.Calls.MISSED_TYPE);

                    //更新UI页面
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pgDialog.dismiss();
                            mRecyclerViewAdapter.setCallLogList(callLogItemModelList);
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();

            return;
        }
    }

    /**
     * 初始化构造View需要的数据
     */
    private void initCallLogs() {
        //undo:从数据库读取数据
//        LogUtil.d(TAG, "position " + getArguments().getInt(POSITION, POSITION_ALL));
    }

    /**
     * 一个圆圈的进度对话框
     * @param title
     * @param msg
     * @return
     */
    private ProgressDialog createProgressDialog(String title, String msg) {

        ProgressDialog pgDialog = new ProgressDialog(getActivity());

        pgDialog.setTitle(title);
        pgDialog.setMessage(msg);
        pgDialog.setCancelable(false);
        pgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pgDialog.setIndeterminate(false);

        return pgDialog;
    }
}
