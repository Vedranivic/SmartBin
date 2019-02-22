package com.example.vedranivic.smartbin.main;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.vedranivic.smartbin.R;
import com.example.vedranivic.smartbin.base.AlarmReceiver;
import com.example.vedranivic.smartbin.base.Constants;
import com.example.vedranivic.smartbin.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.antonious.materialdaypicker.MaterialDayPicker;
import ca.antonious.materialdaypicker.SelectionMode;
import ca.antonious.materialdaypicker.SelectionState;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/*----------------------------------------
Fragment in the main screen for displaying
and setting user’s prefferences (such as
collection arrival time, reminders, bin
size etc.) The data is saved online to
Firebase database
----------------------------------------*/
public class SettingsFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    @BindView(R.id.spBinSize)
    MaterialSpinner spBinSize;
    @BindView(R.id.dayPicker)
    MaterialDayPicker dayPicker;
    @BindView(R.id.tvTimeDay)
    TextView tvTimeDay;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.spReminder)
    MaterialSpinner spReminder;
    @BindView(R.id.btSaveSettings)
    Button bSave;

    private Boolean binSizeChosen = false;
    private Boolean dayPicked = false;
    private Boolean timePicked = false;
    private Boolean reminderOptionChosen = false;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        // setting day picking mode (only one day at time selected)
        dayPicker.setSelectionMode(new SelectionMode() {
            @Override
            public SelectionState getSelectionStateAfterSelecting(SelectionState lastSelectionState, MaterialDayPicker.Weekday dayToSelect) {
                SelectionState state = lastSelectionState.withDaySelected(dayToSelect);
                for(MaterialDayPicker.Weekday day : lastSelectionState.getSelectedDays()){
                    if(day != dayToSelect){
                        state = state.withDayDeselected(day);
                    }
                }
                dayPicked = true;
                return state;
            }

            @Override
            public SelectionState getSelectionStateAfterDeselecting(SelectionState lastSelectionState, MaterialDayPicker.Weekday dayToDeselect) {
                return lastSelectionState;
            }
        });

        // setting listeners for spinners
        spReminder.setOnItemSelectedListener((view1, position, id, item) -> reminderOptionChosen = true);
        spBinSize.setOnItemSelectedListener((view1, position, id, item) -> binSizeChosen = true);

        // retrieving userID from Shared Prefferences
        if(getActivity()!=null) {
            userID = getActivity().getSharedPreferences("SMARTBIN", MODE_PRIVATE).getString("USER_ID", "");
        }

        // if already setup, fetch current settings from Firebase
        if(!getActivity().getSharedPreferences("SMARTBIN", MODE_PRIVATE).getBoolean("INFO_NOT_SET_UP", false)){
            spBinSize.setItems(getResources().getStringArray(R.array.binSizes));
            spReminder.setItems(getResources().getStringArray(R.array.reminderOptions));
            fetchCurrentSettings();
        }
        else {
            // change display for first setup
            tvCancel.setVisibility(View.GONE);
            BottomNavigationView navigation = getActivity().findViewById(R.id.bottomNavigation);
            navigation.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    // retrieving last saved settings from Firebase Database
    private void fetchCurrentSettings() {
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tvTimeDay.setText(user.getCollectionTime());
                dayPicker.selectDay(MaterialDayPicker.Weekday.valueOf(user.getCollectionDay()));
                for(int i = 0; i < spBinSize.getItems().size(); i++){
                    if(spBinSize.getItems().get(i).equals(user.getBinSize())){
                        spBinSize.setSelectedIndex(i);
                    }
                }
                for(int i = 0; i < spReminder.getItems().size(); i++){
                    if(spReminder.getItems().get(i).equals(user.getReminderOption())){
                        spReminder.setSelectedIndex(i);
                    }
                }
                binSizeChosen = true;
                dayPicked = true;
                timePicked = true;
                reminderOptionChosen = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,databaseError.getMessage());
            }
        });
    }


    // Choosing collection time (Time picker Dialog opens)
    @OnClick(R.id.tvTimeDay)
    public void chooseCollectionTime(){
        TimePickerDialog timePicker = new TimePickerDialog(
                getContext(),
                this,
                timePicked ? Integer.parseInt(tvTimeDay.getText().toString().substring(0,2)) : 8,
                timePicked ? Integer.parseInt(tvTimeDay.getText().toString().substring(3,5)) : 0,
                true);
        timePicker.show();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        tvTimeDay.setText(String.format("%02d:%02d", hourOfDay, minute));
        timePicked = true;
    }


    @OnClick(R.id.spBinSize)
    public void setSpBinSizeItems(){
        if(!binSizeChosen) {
            spBinSize.setItems(getResources().getStringArray(R.array.binSizes));
            binSizeChosen = true;
        }
    }

    @OnClick(R.id.spReminder)
    public void setSpReminderItems(){
        if(!reminderOptionChosen) {
            spReminder.setItems(getResources().getStringArray(R.array.reminderOptions));
            reminderOptionChosen = true;
        }
    }

    // Saving the setting (to the Firebase) and starting alarm reminder
    @SuppressLint("ApplySharedPref")
    @OnClick(R.id.btSaveSettings)
    public void saveSettings(){
        if(Constants.IsNetworkConnected(getActivity())) {
            if (!binSizeChosen) {
                toastMessage("Please select approximate bin size");
            } else if (!dayPicked) {
                toastMessage("Please select the day of the week when your waste usually gets collected");
            } else if (!timePicked) {
                toastMessage("Please select the time of the day when your waste usually gets collected");
            } else if (!reminderOptionChosen) {
                toastMessage("Please select a reminder option");
            } else {
                // if everything validated, write to database
                databaseReference.child(userID).child("binSize").setValue(spBinSize.getText());
                databaseReference.child(userID).child("collectionDay").setValue(
                        dayPicker.getSelectedDays().get(0).toString()
                );
                databaseReference.child(userID).child("collectionTime").setValue(tvTimeDay.getText());
                databaseReference.child(userID).child("reminderOption").setValue(spReminder.getText());

                databaseReference.child(userID).child("currentMonth").setValue(Calendar.getInstance().get(Calendar.MONTH));

                // setting the reminder
                setReminder();
                // flag for setting set_up
                getActivity().getSharedPreferences("SMARTBIN", MODE_PRIVATE).edit()
                        .putBoolean("INFO_NOT_SET_UP", false)
                        .commit();
                toastMessage("Settings saved");
                BottomNavigationView navigation = getActivity().findViewById(R.id.bottomNavigation);
                navigation.setVisibility(View.VISIBLE);
                navigation.setSelectedItemId(R.id.navigation_mybin);
            }
        }
    }

    // Setting reminder based on user preferences (before collection of waste happens)
    private void setReminder() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayPicker.getSelectedDays().get(0).ordinal()+1);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tvTimeDay.getText().toString().substring(0,2)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(tvTimeDay.getText().toString().substring(3,5)));
        calendar.set(Calendar.SECOND,0);
        long reminderOffset = 0;

        // Choosing how much time before to remind the user base on selected option
        switch(spReminder.getSelectedIndex()){
            case 0: reminderOffset = Constants.ONE_DAY;
            case 1: reminderOffset = Constants.ONE_HOUR;
            case 2: reminderOffset = Constants.HALF_HOUR;
            case 3: reminderOffset = Constants.MINUTES_15;
            case 4: reminderOffset = Constants.MINUTES_2;
        }

        // The reminder alarm intent that will fire on reminding time
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                getActivity(),
                AlarmReceiver.reqCode,
                new Intent(getContext(),
                        AlarmReceiver.class).putExtra("TYPE",AlarmReceiver.REMINDER_TYPE),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // The collection event intent that will fire on collection time
        PendingIntent eventIntent = PendingIntent.getBroadcast(
                getActivity(),
                AlarmReceiver.reqCodeEvent,
                new Intent(getContext(),
                        AlarmReceiver.class).putExtra("TYPE",AlarmReceiver.EVENT_TYPE),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Adding intents to the alarm manager
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis()-reminderOffset,
                AlarmManager.INTERVAL_DAY*7,alarmIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY*7,eventIntent);
    }

    @OnClick(R.id.tvCancel)
    public void cancel(){
        BottomNavigationView navigation = getActivity().findViewById(R.id.bottomNavigation);
        navigation.setSelectedItemId(R.id.navigation_mybin);
    }

    private void toastMessage(String message) {
        Toast.makeText(getContext(), message,Toast.LENGTH_LONG).show();
    }

}
