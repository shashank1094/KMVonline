package es.esy.shashanksingh.kmvonline;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import es.esy.shashanksingh.kmvonline.sync.CollegeNotificationSyncAdapter;

/**
 * Created by shashank on 16-Feb-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        CollegeNotificationSyncAdapter.syncImmediately(getApplicationContext());
        int d= (int) (remoteMessage.getData().get("dept")).charAt(0);
        String dcrptn=remoteMessage.getData().get("description");
        String msg=remoteMessage.getData().get("message");
        String teahr=remoteMessage.getData().get("teacherName");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
        Boolean shouldShow=sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF,false);
        if(shouldShow) {
            showNotification(msg, dcrptn, d, teahr);
        }
    }
    private int findImageResource(int dept){
        int tempId=0;
        switch (dept) {
            case 0:
                tempId=R.drawable.principal_icon;
                break;
            case 1:
                tempId=R.drawable.admin_icon;
                break;
            case 2:
                tempId=R.drawable.account_icon;
                break;
            case 3:
                tempId=R.drawable.library_icon;
                break;
            case 4:
                tempId=R.drawable.college_icon;
                break;
        }
        return tempId;
    }

    public int generateRandom()
    {
        Random rn = new Random();
        int n = 999999999;
        int i = rn.nextInt() % n;
        return  i;

    }
    private void showNotification(String message,String description,int department,String teacherName) {
        //zxcvbnm
        Log.d("FireBase Notification",message+description+(department-48)+teacherName);
        String officeDepartmentNames[]=new String[6];
        officeDepartmentNames[0]="Principal";
        officeDepartmentNames[1]="Administration";
        officeDepartmentNames[2]="Accounts";
        officeDepartmentNames[3]="Library";
        officeDepartmentNames[4]="Keshav Mahavidyalaya";
        officeDepartmentNames[5]="Department of Computer Science";
        int imageId=findImageResource(department-48);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean shouldShow=sharedPref.getBoolean(teacherName,false);
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME_LOGIN_DETAILS, Context.MODE_PRIVATE);
        String user = sharedPreferences.getString(Config.NAME_SHARED_PREF,"student");
        if(shouldShow){
            shouldShow=user.equals(teacherName);
            if(shouldShow){

            }else {
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setContentTitle(message)
                        .setContentText(teacherName + " - " + officeDepartmentNames[department - 48])
                        .setSmallIcon(imageId)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(description + "\n\n" + teacherName + " - " + officeDepartmentNames[department - 48]));

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = builder.build();
                notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                manager.notify(generateRandom(), notification);
            }
            }

    }
}
