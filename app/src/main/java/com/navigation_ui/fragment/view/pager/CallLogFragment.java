package com.navigation_ui.fragment.view.pager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.navigation_ui.R;
import com.navigation_ui.adapter.CallLogRecyclerViewAdapter;
import com.navigation_ui.model.CallLogItemModel;
import com.navigation_ui.tools.LogUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CallLogFragment extends Fragment implements Observer {

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
        mView = inflater.inflate(R.layout.fragment_call_log, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerViewAdapter = new CallLogRecyclerViewAdapter(callLogItemModelList);
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

        for (int i = 0; i < 30; i++) {
            CallLogItemModel callLogItemModel = new CallLogItemModel();
            callLogItemModel.setContactsName("李四");
            callLogItemModel.setPhoneNumber("13012341234");
            callLogItemModel.setDateInMilliseconds("1485602523885");
            callLogItemModel.setCallCounts(5);
            callLogItemModel.setDuration("12");
            callLogItemModel.setCallType(2);
            callLogItemModel.setCallerLoc("北京市");

            callLogItemModelList.add(callLogItemModel);
        }

        mRecyclerViewAdapter.setCallLogList(callLogItemModelList);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化构造View需要的数据
     */
    private void initCallLogs() {

        final ProgressDialog pgDialog = createProgressDialog(null, "正在读取，请稍后...");
        pgDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //undo：展示读取耗时操作
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < 50000; i++) {
                    CallLogItemModel callLogItemModel = new CallLogItemModel();
                    callLogItemModel.setContactsName("张三");
                    callLogItemModel.setPhoneNumber("13012341234");
                    callLogItemModel.setDateInMilliseconds("1485602523885");
                    callLogItemModel.setCallCounts(5);
                    callLogItemModel.setDuration("12");
                    callLogItemModel.setCallType(1);
                    callLogItemModel.setCallerLoc("四川省绵阳市");

                    callLogItemModelList.add(callLogItemModel);
                }

                //更新UI页面
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerViewAdapter.setCallLogList(callLogItemModelList);
                        mRecyclerViewAdapter.notifyDataSetChanged();

                        pgDialog.dismiss();
                    }
                });
            }
        }).start();
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
