package com.tutorials.reminderappsamsung.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tutorials.reminderappsamsung.MainActivity;
import com.tutorials.reminderappsamsung.R;
import com.tutorials.reminderappsamsung.data.database.ReminderDatabase;
import com.tutorials.reminderappsamsung.data.model.Reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class UpdateReminderItem extends AppCompatActivity {

    TextView titleUpdate;

    CheckBox important;
    EditText title, location, note;
    TimePicker timePicker;
    DatePicker datePicker;
    Button date, time;


    RelativeLayout relativeLayoutTime, outside;

    LinearLayout datetimeButton;

    TextView cancel, save, timeTV;

    boolean relativeLayoutTimeChecked = false;


    private Calendar calendar = Calendar.getInstance();

    private Reminder mReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);

        initData();

        mReminder = (Reminder) Objects.requireNonNull(getIntent().getExtras()).get("object_reminder_item");

        setupDateTimePickerUpdate(mReminder.getDate(), mReminder.getTime());
        setupTitleLocationNote(mReminder.getTitle(), mReminder.getLocation(), mReminder.getDescription());
        //DateTimeChangedListenerUpdate();

        ElementInReminderOnClick();
    }

    private void setupTitleLocationNote(String strtitle, String strlocation, String strdescription) {
        title.setText(strtitle);
        location.setText(strlocation);
        note.setText(strdescription);
    }

    private void ElementInReminderOnClick() {

        setupDateTimePicker();

        relativeLayoutTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardEdittext(v);
                if(relativeLayoutTimeChecked){
                    timeTV.setText(null);
                    relativeLayoutTimeChecked = false;
                    //set time
                    time.setTextColor(0xFF000000);
                    timePicker.setVisibility(View.GONE);
                    //set date
                    date.setTextColor(0xFF000000);
                    datePicker.setVisibility(View.GONE);
                    datetimeButton.setVisibility(View.GONE);
                }else {
                    timeTV.setText(R.string.textviewTime);
                    timeTV.setTypeface(null, Typeface.BOLD);
                    relativeLayoutTimeChecked = true;
                    datetimeButton.setVisibility(View.VISIBLE);
                    //set time
                    time.setTextColor(0xFF5BBCFF);
                    timePicker.setVisibility(View.VISIBLE);
                    //set date
                    date.setTextColor(0xFF000000);
                    datePicker.setVisibility(View.GONE);
                    date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(timePicker.getVisibility() == View.VISIBLE){
                                timePicker.setVisibility(View.GONE);
                                time.setTextColor(0xFF000000);
                            }
                            date.setTextColor(0xFF5BBCFF);
                            datePicker.setVisibility(View.VISIBLE);
                        }
                    });
                    time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(datePicker.getVisibility() == View.VISIBLE){
                                datePicker.setVisibility(View.GONE);
                                date.setTextColor(0xFF000000);
                            }
                            time.setTextColor(0xFF5BBCFF);
                            timePicker.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.setVisibility(View.GONE);
                datePicker.setVisibility(View.GONE);
                datetimeButton.setVisibility(View.GONE);
                timeTV.setTypeface(null);
                timeTV.setText(null);
                hideKeyboardEdittext(v);
            }
        });

        TitleNoteLocationFocus();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateReminderItem.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Disable the save button initially
        save.setEnabled(false);

        //co the dung TextUtils.isEmpty(str)
        TextChangedListenerUpdate();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTitle = title.getText().toString();
                if (!strTitle.isEmpty()) {
                    String strNote = note.getText().toString();
                    String strLocation = location.getText().toString();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(calendar.getTime());

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String formattedTime = timeFormat.format(calendar.getTime());
                    //Log.v("TAGY+TITLE", strTitle + " " + strTitle.length() + " " + formattedDate + " " + formattedTime);
                    Intent intent = new Intent(UpdateReminderItem.this, MainActivity.class);

                    mReminder.setDate(formattedDate);
                    mReminder.setTime(formattedTime);
                    mReminder.setTitle(strTitle);
                    mReminder.setDescription(strNote);
                    mReminder.setLocation(strLocation);
                    ReminderDatabase.getInstance(UpdateReminderItem.this).getReminderDAO().updateReminderItem(mReminder);
                    startActivity(intent);
                }
            }
        });

        important.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(important.isChecked()){
                    important.setBackgroundTintList(ColorStateList.valueOf(R.color.yellow));
                }else {
                    important.setBackgroundTintList(ColorStateList.valueOf(R.color.black));
                }
            }
        });

    }

    private void TitleNoteLocationFocus() {
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.GONE);
                    datetimeButton.setVisibility(View.GONE);
                    timeTV.setTypeface(null);
                    timeTV.setText(null);
                }
            }
        });
        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.GONE);
                    datetimeButton.setVisibility(View.GONE);
                    timeTV.setTypeface(null);
                    timeTV.setText(null);
                }
            }
        });
        note.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.GONE);
                    datetimeButton.setVisibility(View.GONE);
                    timeTV.setTypeface(null);
                    timeTV.setText(null);
                }
            }
        });
    }

    private void DateTimeChangedListenerUpdate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = timeFormat.format(calendar.getTime());

        if(!formattedDate.equals(mReminder.getDate()) || !formattedTime.equals(mReminder.getTime())){
            save.setEnabled(true);
            save.setText(R.string.save);
        }else {
            save.setEnabled(false);
            save.setText(null);
        }
    }

    private void TextChangedListenerUpdate() {
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Update strTitle whenever the text in the EditText changes
                String strTitle = s.toString();
                // Enable the save button if strTitle is not empty, otherwise disable it
                save.setEnabled(strTitle.isEmpty());
                save.setEnabled(!strTitle.isEmpty());
                if(!strTitle.isEmpty()){
                    save.setText(R.string.save);
                }
            }
        });

        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strLocation = s.toString();
                // Enable the save button if strTitle is not empty, otherwise disable it
                save.setEnabled(strLocation.isEmpty());
                save.setEnabled(!strLocation.isEmpty());
                if(!strLocation.isEmpty()){
                    save.setText(R.string.save);
                }
            }
        });

        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strNote = s.toString();
                // Enable the save button if strTitle is not empty, otherwise disable it
                save.setEnabled(strNote.isEmpty());
                save.setEnabled(!strNote.isEmpty());
                if(!strNote.isEmpty()){
                    save.setText(R.string.save);
                }
            }
        });
    }

    private void hideKeyboardEdittext(View v) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (NullPointerException message){
            message.printStackTrace();
        }
    }

    private void setupDateTimePickerUpdate(String dateString, String timeString) {

        // Phân tích chuỗi ngày thành các thành phần
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.getDefault());
        try {
            calendar.setTime(dateFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Thiết lập DatePicker từ ngày được phân tích
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);

        // Phân tích chuỗi thời gian thành các thành phần
        String[] timeParts = timeString.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Thiết lập TimePicker từ thời gian được phân tích
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            // Cho các phiên bản Android cũ hơn
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }
    }

    private void initData() {
        titleUpdate = findViewById(R.id.title);
        titleUpdate.setText("Update");

        datetimeButton = findViewById(R.id.dateTimeButton);
        important = findViewById(R.id.important);
        title = findViewById(R.id.edittextTitle);
        location = findViewById(R.id.editLocation);
        note = findViewById(R.id.editNote);

        timePicker = findViewById(R.id.timepicker);
        datePicker = findViewById(R.id.datepicker);

        date = findViewById(R.id.btnDate);
        time = findViewById(R.id.btnTime);

        timePicker.setVisibility(View.GONE);
        datePicker.setVisibility(View.GONE);
        datetimeButton.setVisibility(View.GONE);


        outside = findViewById(R.id.outside);
        relativeLayoutTime = findViewById(R.id.time);

        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        timeTV = findViewById(R.id.timeTV);
    }

    private void setupDateTimePicker() {
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                    }
                });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }
        });
    }
}