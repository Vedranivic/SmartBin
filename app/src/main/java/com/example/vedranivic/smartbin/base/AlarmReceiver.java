package com.example.vedranivic.smartbin.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import com.example.vedranivic.smartbin.main.MainActivity;
import com.example.vedranivic.smartbin.R;
import com.example.vedranivic.smartbin.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import androidx.core.app.NotificationCompat;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver{

    private final static String TAG = AlarmReceiver.class.getSimpleName();

    public final static int reqCode = 101;
    public final static int reqCodeEvent = 202;
    public final static int REMINDER_TYPE = 0;
    public final static int EVENT_TYPE = 1;
    public final static String SCHEDULE_FOR_COLLECTION_ACTION = "Collect";
    public final static String DISMISS_ACTION = "Dismiss";

    private int collectionNumber = 0;
    private Double wasteAmount = 0.0;
    private Boolean scheduledForCollection = false;
    private String binSize;
    private Double percentage;
    private int currentMonth;

    public final static String CHANNEL_REMINDER = "REMINDER";
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getIntExtra("TYPE", REMINDER_TYPE) == REMINDER_TYPE){
            showNotification(context);
        }

        else if(intent.getIntExtra("TYPE",REMINDER_TYPE) == EVENT_TYPE){
            updateStatistics(context);
        }

        if(intent.getAction()!=null) {
            if (intent.getAction().equals(SCHEDULE_FOR_COLLECTION_ACTION)) {
                scheduleForCollection(context);
            } else if (intent.getAction().equals(DISMISS_ACTION)) {
                dismissNotification(context);
            }
        }
    }

    private void dismissNotification(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(reqCode);
    }

    private void scheduleForCollection(Context context) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String userID = context.getSharedPreferences("SMARTBIN", MODE_PRIVATE).getString("USER_ID", "");
        if(!userID.equals("")) {
            databaseReference.child(userID).child("scheduledForCollection").setValue(true);
        }
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(reqCode);
    }

    public void showNotification(Context context) {

        PendingIntent acceptNotificationIntent = PendingIntent.getBroadcast(
                context,
                reqCode,
                new Intent(context, AlarmReceiver.class)
                        .setAction(SCHEDULE_FOR_COLLECTION_ACTION)
                        .putExtra("COLLECTION", true)
                , PendingIntent.FLAG_UPDATE_CURRENT
        );

        PendingIntent openAppIntent = PendingIntent.getActivity(
                context,
                reqCode,
                new Intent(context, MainActivity.class)
                , PendingIntent.FLAG_UPDATE_CURRENT
        );

        PendingIntent dismissNotificationIntent = PendingIntent.getBroadcast(
                context,
                reqCode,
                new Intent(context, AlarmReceiver.class)
                        .putExtra("COLLECTION", false)
                        .setAction(DISMISS_ACTION)
                , PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_REMINDER)
                .setSmallIcon(R.drawable.ic_mybin_24dp)
                .setContentIntent(openAppIntent)
                .setContentTitle("Reminder")
                .setContentText("You should take your bin out for waste collection!")
                .addAction(R.drawable.ic_mybin_24dp, "SCHEDULE FOR COLLECTION", acceptNotificationIntent)
                .addAction(R.drawable.ic_mybin_24dp, "DISMISS", dismissNotificationIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setCategory(NotificationCompat.CATEGORY_ALARM);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(reqCode, mBuilder.build());
    }

    private void updateStatistics(Context context){
        String userID = context.getSharedPreferences("SMARTBIN", MODE_PRIVATE).getString("USER_ID", "");
        if(!userID.equals("")) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if(dataSnapshot.child("collectionNumber").exists()){
                        collectionNumber = user.getCollectionNumber();
                        wasteAmount = user.getWasteAmount();
                    }
                    if(dataSnapshot.child("scheduledForCollection").exists()){
                        scheduledForCollection = user.getScheduledForCollection();
                    }
                    binSize = user.getBinSize();
                    currentMonth = user.getCurrentMonth();
                    percentage = user.getPercentage();

                    if(scheduledForCollection){
                        databaseReference.child(userID).child("collectionNumber").setValue(collectionNumber+1);
                        databaseReference.child(userID).child("wasteAmount").setValue(wasteAmount + (percentage/100)*Integer.parseInt(binSize.split(" ")[0])/1000);
                        Log.e(TAG,"PARSE: " + String.valueOf(Integer.parseInt(binSize.split(" ")[0])));
                        databaseReference.child(userID).child("scheduledForCollection").setValue(false);

                    }

                    // on next Month onset
                    int month = Calendar.getInstance().get(Calendar.MONTH);
                    if( month > currentMonth){
                        databaseReference.child(userID).child("lastCollectionNumber").setValue(collectionNumber);
                        databaseReference.child(userID).child("lastWasteAmount").setValue(wasteAmount);
                        databaseReference.child(userID).child("collectionNumber").setValue(0);
                        databaseReference.child(userID).child("wasteAmount").setValue(0.0);
                        databaseReference.child(userID).child("currentMonth").setValue(month);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG,databaseError.getMessage());
                }
            });

        }
    }
}
