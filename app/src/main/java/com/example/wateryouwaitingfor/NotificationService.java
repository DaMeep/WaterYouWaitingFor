package com.example.wateryouwaitingfor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    public static String NOTIFICATION_CHANNEL_ID = "10001"; // Notification Channel ID
    private final static String default_notification_channel_id = "default" ; // Default Channel ID
    private Timer timer; // Timer Object
    private TimerTask timerTask; // Timer Task Object

    private final Handler handler = new Handler(Looper.myLooper()) ; // Handler for making Notifications

    @Override
    public IBinder onBind (Intent arg0) {
        return null;
    }
    @Override
    public int onStartCommand (Intent intent , int flags , int startId) {
        super .onStartCommand(intent , flags , startId) ;
        startTimer() ;
        return START_STICKY ;
    }
    @Override
    public void onCreate () {
    }
    @Override
    public void onDestroy () {
        stopTimerTask() ;
        super .onDestroy() ;
    }

    /**
     * Starts the timer for the Notification Reminders
     */
    public void startTimer () {
        timer = new Timer() ;
        initializeTimerTask() ;
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        int notificationInterval = sharedPreferences.getInt("notificationInterval", 0) + 1;
        Log.e("NOTIFICATION INTERVAL", String.valueOf(notificationInterval));
        timer .schedule( timerTask , notificationInterval * 900  * 1000L , notificationInterval * 900  * 1000L ) ;
    }
    /**
     * Stops the timer for the Notification Reminders
     */
    public void stopTimerTask () {
        if ( timer != null ) {
            timer .cancel() ;
            timer = null;
        }
    }
    /**
     * Begins the TimerTask needed for regular notifications
     */
    public void initializeTimerTask () {
        timerTask = new TimerTask() {
            public void run () {
                handler .post   ( new Runnable() {
                    public void run () {
                        createNotification() ;
                    }
                }) ;
            }
        } ;
    }
    /**
     * Creates the Notification Channel and the
     * Notification message
     */
    private void createNotification () {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext() , default_notification_channel_id ) ;
        mBuilder.setContentTitle( "WaterYouWaitingFor" ) ;
        mBuilder.setContentText( "It's time to drink!" ) ;
        mBuilder.setTicker( "Drink Time" ) ;
        mBuilder.setSmallIcon(R.drawable.baseline_water_drop_24 ) ;
        mBuilder.setAutoCancel( true ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
    }
}
