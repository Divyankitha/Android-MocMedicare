package com.manage.hospital.hmapp.Extras.broadcast_receiver;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.manage.hospital.hmapp.R;
import com.manage.hospital.hmapp.ui.patient.AlarmActivity;

import java.util.Random;


public class RingtonePlayingService extends Service {

    MediaPlayer media_song;
    int startId;
    boolean isRunning;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);


        String state = intent.getExtras().getString("extra");
        Integer alarm_sound_choice = intent.getExtras().getInt("sound_choice");

        Log.e("Ringtone extra is ", state);
        Log.e("Alarm choice is ", alarm_sound_choice.toString());


        NotificationManager notify_manager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        Intent intent_alarm_activity = new Intent(this.getApplicationContext(), AlarmActivity.class);
        PendingIntent pending_intent_alarm_activity = PendingIntent.getActivity(this, 0,
                intent_alarm_activity, 0);

        Notification notification_popup = new Notification.Builder(this)
                .setContentTitle("Reminder Alert!")
                .setContentText("You have a reminder set")
                .setSmallIcon(R.drawable.ic_action_call)
                .setContentIntent(pending_intent_alarm_activity)
                .setAutoCancel(true)
                .build();

        assert state != null;
        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                Log.e("Start ID is ", state);
                break;
            default:
                startId = 0;
                break;
        }

        if (!this.isRunning && startId == 1) {
            Log.e("there is no music, ", "and you want start");

            this.isRunning = true;
            this.startId = 0;

            notify_manager.notify(0, notification_popup);

            if (alarm_sound_choice == 0) {

                int minimum_number = 1;
                int maximum_number = 13;

                Random random_number = new Random();
                int alarm_number = random_number.nextInt(maximum_number + minimum_number);
                Log.e("random number is " , String.valueOf(alarm_number));


                if (alarm_number == 1) {
                    media_song = MediaPlayer.create(this, R.raw.alarm1);
                    media_song.start();
                }
                else if (alarm_number == 2) {

                    media_song = MediaPlayer.create(this, R.raw.alarm2);
                    media_song.start();
                }
                else {
                    media_song = MediaPlayer.create(this, R.raw.alarm3);
                    media_song.start();
                }


            }
            else if (alarm_sound_choice == 1) {
                media_song = MediaPlayer.create(this, R.raw.alarm1);

                media_song.start();
            }
            else if (alarm_sound_choice == 2) {
                media_song = MediaPlayer.create(this, R.raw.alarm2);
                media_song.start();
            }
            else {
                media_song = MediaPlayer.create(this, R.raw.alarm3);
                media_song.start();
            }
        }

        else if (this.isRunning && startId == 0) {
            Log.e("there is music, ", "and you want end");

            media_song.stop();
            media_song.reset();

            this.isRunning = false;
            this.startId = 0;
        }

        else if (!this.isRunning && startId == 0) {
            Log.e("there is no music, ", "and you want end");

            this.isRunning = false;
            this.startId = 0;

        }


        else if (this.isRunning && startId == 1) {
            Log.e("there is music, ", "and you want start");

            this.isRunning = true;
            this.startId = 1;

        }

        else {
            Log.e("else ", "somehow you reached this");

        }



        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("on Destroy called", "ta da");

        super.onDestroy();
        this.isRunning = false;
    }



}

