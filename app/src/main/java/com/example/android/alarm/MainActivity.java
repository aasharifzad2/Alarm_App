package com.example.android.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;


import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    Context context;
    ToggleButton button;
    TextView textView;
    PendingIntent pending_intent;


    TextToSpeech t1;
    Button b1;
    Button b2;
    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;

        ReadRSS rssReader = new ReadRSS();
        rssReader.run();

        b1=(Button) findViewById(R.id.button);
        b2=(Button) findViewById(R.id.s_button);

        // Initialize alarm manager

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Initialize time picker

        alarm_timepicker = (TimePicker) findViewById(R.id.timePicker3);

        // Initialize calendar intent

        final Calendar calendar = Calendar.getInstance();

        // Initialize text view

        textView = (TextView) findViewById(R.id.textView);

        // create alarm receiver intent

        final Intent my_intent= new Intent(this.context, Alarm_Receiver.class);

        // Initialize buttons

        button = (ToggleButton) findViewById(R.id.toggleB);

        // initialize speech



        t1 = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS) {
                    result = t1.setLanguage(Locale.CANADA);
                } else {
                    Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),ReadRSS.news,Toast.LENGTH_SHORT).show();
                t1.speak(ReadRSS.news, TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t1 != null) {
                    t1.stop();
                    t1.shutdown();
                }
            }
        });


        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Calendar now = Calendar.getInstance();

                // Setting calendar to the time picked on time picker

                int hour;
                int min;

                int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
                    hour = alarm_timepicker.getHour();
                    min = alarm_timepicker.getMinute();
                } else {
                    hour = alarm_timepicker.getCurrentHour();
                    min = alarm_timepicker.getCurrentMinute();
                }

                String min_string = Integer.toString(min);
                String hour_string = Integer.toString(hour);

                if (hour > 12) {
                    hour_string = Integer.toString(hour - 12);
                }

                if (min < 10) {
                    min_string = "0" + Integer.toString(min);
                }

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);

                //if (calendar.before(now))
                  //  calendar.add(Calendar.DAY_OF_MONTH, 1);


                if (isChecked) {

                    // put an extra string in my_intent
                    // tells the clock that yoe set the alarm on

                    my_intent.putExtra("extra", "alarm on");


                    // create pending intent that delays intent until alarm time

                    pending_intent = PendingIntent.getBroadcast(MainActivity.this, 0,
                            my_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // set alarm manager

                    alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            pending_intent);


                    set_text("Alarm set to: " + hour_string + ":" + min_string);

                } else {

                    set_text("Alarm is off!" );

                    // Cancel the alarm

                    alarm_manager.cancel(pending_intent);

                    // put extra string into my_intent
                    // tells the clock you turned the alarm off

                    my_intent.putExtra("extra", "alarm off");

                    // Cancel the ringtone

                    sendBroadcast(my_intent);

                }
            }
        });
        }

    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    private void set_text(String output) {
        textView.setText(output);

    }

}