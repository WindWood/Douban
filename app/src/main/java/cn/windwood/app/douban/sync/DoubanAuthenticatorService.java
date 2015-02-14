package cn.windwood.app.douban.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DoubanAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private DoubanAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new DoubanAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
