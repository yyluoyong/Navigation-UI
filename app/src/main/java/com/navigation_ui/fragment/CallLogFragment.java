package com.navigation_ui.fragment;

import android.content.Context;
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

public class CallLogFragment extends Fragment implements FragmentUpdatable, Observer {

    private View mView;
    private RecyclerView mRecyclerView;
    private CallLogRecyclerViewAdapter mRecyclerViewAdapter;

    private List<CallLogItemModel> callLogItemModelList = new ArrayList<>();

    private String contactsName;

    private FragmentUpdateListener mUpdateListener;

    private boolean isNeedUpdatea = false;

    @Override
    public void onAttach(Context context) {
        LogUtil.d("Adapter", "onAttach");

        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initCallLogs();

        LogUtil.d("Adapter", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_call_log, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerViewAdapter = new CallLogRecyclerViewAdapter(callLogItemModelList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        LogUtil.d("Adapter", "onCreateView");
        UpdateDataObservable.getInstance().addObserver(this);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        LogUtil.d("Adapter", "onActivityCreated");

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        LogUtil.d("Adapter", "onStart");
        super.onStart();
    }

    @Override
    public void update(Observable o, Object arg) {

        boolean updateFlag = (boolean) arg;

        if (updateFlag) {
            updateData();
        }
    }

    @Override
    public void updateData() {
        contactsName = "李四";

        callLogItemModelList.clear();

        for (int i = 0; i < 30; i++) {
            CallLogItemModel callLogItemModel = new CallLogItemModel();
            callLogItemModel.setContactsName("李四");
            callLogItemModel.setPhoneNumber("13012341234");
            callLogItemModel.setDateInStr("2012年6月12日 12:12:12");
            callLogItemModel.setCallCounts(5);
            callLogItemModel.setDuration("12");
            callLogItemModel.setCallType(1);
            callLogItemModel.setCallerLoc("北京市");

            callLogItemModelList.add(callLogItemModel);
        }

        mRecyclerViewAdapter.setCallLogList(callLogItemModelList);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {

        LogUtil.d("Adapter", "onResume --->");

//        if (mUpdateListener != null) {
//            LogUtil.d("Adapter", "onResume mUpdateListener");
//            if (mUpdateListener.isNeedUpdate()) {
//                updateData();
//                LogUtil.d("Adapter", "onResume update");
//            }
//        }



        super.onResume();

        LogUtil.d("Adapter", "onResume ====");
    }


    @Override
    public void onDestroyView() {
        LogUtil.d("Adapter", "onDestroyView");
        UpdateDataObservable.getInstance().deleteObserver(this);
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        LogUtil.d("Adapter", "onPause");
        super.onPause();

    }

    @Override
    public boolean isNeedUpdate() {
        return isNeedUpdatea;
    }


    private void initCallLogs() {

        contactsName = "张三";

        for (int i = 0; i < 50000; i++) {
            CallLogItemModel callLogItemModel = new CallLogItemModel();
            callLogItemModel.setContactsName("张三");
            callLogItemModel.setPhoneNumber("13012341234");
            callLogItemModel.setDateInStr("2012年6月12日 12:12:12");
            callLogItemModel.setCallCounts(5);
            callLogItemModel.setDuration("12");
            callLogItemModel.setCallType(1);
            callLogItemModel.setCallerLoc("北京市");

            callLogItemModelList.add(callLogItemModel);
        }
    }

    @Override
    public String toString() {
        return contactsName;
    }

    public void setNeedUpdate(boolean needUpdate) {
        isNeedUpdatea = needUpdate;
    }

    public void addFragmentUpdateListener(FragmentUpdateListener listener) {
        mUpdateListener = listener;
    }
}
