package com.navigation_ui.fragment;

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
import com.navigation_ui.adapter.MainViewPagerAdapter;
import com.navigation_ui.model.CallLogItemModel;

import java.util.ArrayList;
import java.util.List;

public class CallLogFragment extends Fragment {

    private View mView;
    private RecyclerView mRecyclerView;

    private List<CallLogItemModel> callLogItemModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_call_log, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        initCallLogs();

        CallLogRecyclerViewAdapter recyclerViewAdapter = new CallLogRecyclerViewAdapter(callLogItemModelList);
        mRecyclerView.setAdapter(recyclerViewAdapter);

        return mView;
    }


    private void initCallLogs() {

        for (int i = 0; i < 30; i++) {
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


}
