package es.esy.shashanksingh.kmvonline.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by shashank on 15-Feb-17.
 */

public class CollegeNotificationAuthenticatorService extends Service  {
    // Instance field that stores the authenticator object
    private CollegeNotificationAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new CollegeNotificationAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
