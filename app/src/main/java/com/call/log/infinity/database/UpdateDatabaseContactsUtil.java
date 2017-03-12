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
