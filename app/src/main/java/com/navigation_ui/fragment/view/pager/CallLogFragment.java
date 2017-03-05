package com.navigation_ui.fragment.view.pager;

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
import com.navigation_ui.R;
import com.navigation_ui.adapter.CallLogRecyclerViewAdapter;
import com.navigation_ui.database.RecentCallLogListUtil;
import com.navigation_ui.model.CallLogItemModel;
import com.navigation_ui.utils.LogUtil;
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

    public static final String POSITION = "POSITION";
    //所有通话
    private static final int POSITION_ALL = 0;
    //来电
    private static final int POSITION_RECEIVED = 1;
    //去电
    private static final int POSITION_MADE = 2;
    //未接
    private static final int POSITION_MISSED = 3;

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

        mRecyclerViewAdapter = new CallLogRecyclerViewAdapter(getContext(), callLogItemModelList);
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

        int mPosition = getArguments().getInt(POSITION, POSITION_ALL);

        //所有通话
        if (mPosition == POSITION_ALL) {
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
        if (mPosition == POSITION_RECEIVED) {
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
        if (mPosition == POSITION_MADE) {
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
        if (mPosition == POSITION_MISSED) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    callLogItemModelList = new RecentCallLogListUtil()
                        .getRecentCallLogItemList(CallLog.Calls.MISSED_TYPE);

                    for( CallLogItemModel model : callLogItemModelList) {
                        LogUtil.d(TAG, model.toString());
                    }

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
        LogUtil.d(TAG, "position " + getArguments().getInt(POSITION, POSITION_ALL));
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
