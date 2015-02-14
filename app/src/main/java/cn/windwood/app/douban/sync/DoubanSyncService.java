package cn.windwood.app.douban.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DoubanSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static DoubanSyncAdapter sDoubanSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("DoubanSyncService", "onCreate - DoubanSyncService");
        synchronized (sSyncAdapterLock) {
            if (sDoubanSyncAdapter == null) {
                sDoubanSyncAdapter = new sDoubanSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sDoubanSyncAdapter.getSyncAdapterBinder();
    }
}
