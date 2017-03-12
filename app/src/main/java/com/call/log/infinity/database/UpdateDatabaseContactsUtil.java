package com.call.log.infinity.database;

import android.support.annotation.NonNull;

import com.call.log.infinity.utils.LogUtil;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yong on 2017/3/12.
 */

public class UpdateDatabaseContactsUtil {

    static final String TAG = "UpdateDatabaseContactsUtil";

    public interface OnUpdateDatabaseContactsListener {
        void success();
    }


    public static void updateDatabaseContacts(@NonNull final HashMap<String, String> phoneNumberAndContactsName,
        @NonNull final OnUpdateDatabaseContactsListener listener) {
        DatabaseDefinition database = FlowManager.getDatabase(CallLogDatabase.class);
        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
//                LogUtil.d(TAG, "size: " + phoneNumberAndContactsName.size());
                int i = 1;
                for (Map.Entry<String, String> entry : phoneNumberAndContactsName.entrySet() ) {

                    SQLite.update(CallLogModelDBFlow.class)
                        .set(CallLogModelDBFlow_Table.contactsName.eq(entry.getValue()))
                        .where(CallLogModelDBFlow_Table.phoneNumber.eq(entry.getKey())).async().execute();
//                    i++;
                    LogUtil.d(TAG, "线程ID：" + android.os.Process.myTid() + " i = " + (i++) +  " " + entry.getKey() + " " + entry.getValue());
                }

                SQLite.update(CallLogModelDBFlow.class)
                    .set(CallLogModelDBFlow_Table.contactsName.eq("未知"))
                    .where(CallLogModelDBFlow_Table.phoneNumber.eq("15181637287")).execute();
                LogUtil.d(TAG, "update");
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(Transaction transaction) {
                if (listener != null) {
                    listener.success();
                }
            }
        }).build();
        transaction.execute(); // execute
    }

}
