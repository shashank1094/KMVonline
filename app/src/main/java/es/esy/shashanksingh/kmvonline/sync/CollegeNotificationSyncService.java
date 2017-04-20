package es.esy.shashanksingh.kmvonline.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by shashank on 15-Feb-17.
 */

public class CollegeNotificationSyncService extends Service{
    private static final Object sSyncAdapterLock = new Object();
    private static CollegeNotificationSyncAdapter mCollegeNotificationSyncAdapter = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mCollegeNotificationSyncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onCreate() {
        Log.d("CollegeNfcatonSycSrvc", "onCreate-CollegeNotificationSyncService");
        synchronized (sSyncAdapterLock) {
            if (mCollegeNotificationSyncAdapter == null) {
                mCollegeNotificationSyncAdapter = new CollegeNotificationSyncAdapter(getApplicationContext(), true);
            }
        }
    }
}
