package com.example.cspingpong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.expansionpanel.ExpansionHeader;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int GAMES_PER_HOUR = 4;
    private static final int MIN_HOUR_PICK = 0;
    private static final int MAX_HOUR_PICK = 23;

    private Server server;
    private ExpansionHeader[] slotHeaders = new ExpansionHeader[GAMES_PER_HOUR];
    //    private ExpansionLayout[] slotExpansions = new ExpansionLayout[GAMES_PER_HOUR];
    private TextView[] headerTexts = new TextView[GAMES_PER_HOUR];
    private String[] slotIntervalsSuffix = new String[GAMES_PER_HOUR];

    private NumberPicker hourPicker;

    private NameDialog nameDialog;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        server = new Server();

        connectViewsToXML();

        setHourPickerValues();

        // todo more elegant
        setSlotIntervalStrings();

        setHourPickerListener();

        updateHeaderColors();

        FragmentManager fm = getSupportFragmentManager();
        nameDialog = NameDialog.newInstance("Some Title");
        nameDialog.show(fm, "fragment_edit_name");
    }


    /**
     * time picker on value changed listener
     */
    private void setHourPickerListener() {

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int pickedHour = hourPicker.getValue();

                for (int i = 0; i < GAMES_PER_HOUR; i++) {
                    headerTexts[i].setText(pickedHour + slotIntervalsSuffix[i]);
                }

                updateHeaderColors();
            }
        });
    }


    private void setSlotIntervalStrings() {
        slotIntervalsSuffix[0] = ":00";
        slotIntervalsSuffix[1] = ":15";
        slotIntervalsSuffix[2] = ":30";
        slotIntervalsSuffix[3] = ":45";
    }

    /**
     * Connect between Objects and XML representation of them
     */
    private void connectViewsToXML() {
        hourPicker = findViewById(R.id.hour_picker);

        slotHeaders[0] = findViewById(R.id.slot_header_1);
        slotHeaders[1] = findViewById(R.id.slot_header_2);
        slotHeaders[2] = findViewById(R.id.slot_header_3);
        slotHeaders[3] = findViewById(R.id.slot_header_4);

        headerTexts[0] = findViewById(R.id.header_text1);
        headerTexts[1] = findViewById(R.id.header_text2);
        headerTexts[2] = findViewById(R.id.header_text3);
        headerTexts[3] = findViewById(R.id.header_text4);

    }

    private void updateHeaderColors() {
        ArrayList<Game> games = server.get_hour_agenda(22122019, hourPicker.getValue());

        for (int i = 0; i < 4; i++) {
            switch (games.get(i).empty_slots()) {
                case 0:
                    slotHeaders[i].setBackgroundTintList(
                            ContextCompat.getColorStateList(getApplicationContext(), R.color.gray));
                    slotHeaders[i].setClickable(false);
                    break;
                case 1:
                    slotHeaders[i].setBackgroundTintList(
                            ContextCompat.getColorStateList(getApplicationContext(), R.color.apple));
                    break;
                case 2:
                    slotHeaders[i].setBackgroundTintList(
                            ContextCompat.getColorStateList(getApplicationContext(), R.color.white));
                    break;
            }
        }
    }

    private void setHourPickerValues() {
        // TODO generate automatically?
        final String[] arrayString = new String[]{
                "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00",
                "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
                "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"};
        hourPicker.setMinValue(MIN_HOUR_PICK);
        hourPicker.setMaxValue(MAX_HOUR_PICK);

        hourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                // TODO Auto-generated method stub
                return arrayString[value];
            }
        });
    }

    public void chooseGame(View slotButton) {
        int time = hourPicker.getValue();
        String sTime = "";

        switch (slotButton.getId()) {
            case R.id.join_button_1:
                sTime = hourPicker.getValue() + slotIntervalsSuffix[0];
                break;
            case R.id.join_button_2:
                time += 15;
                sTime = hourPicker.getValue() + slotIntervalsSuffix[1];
                break;
            case R.id.join_button_3:
                time += 30;
                sTime = hourPicker.getValue() + slotIntervalsSuffix[2];
                break;
            case R.id.join_button_4:
                time += 45;
                sTime = hourPicker.getValue() + slotIntervalsSuffix[3];
                break;
        }
        server.addPlayer(22122019, time, username);
        updateHeaderColors();

        String message = "Hi " + username + ", You chose to play in " + 22122019 + " at " + sTime;
        Toast gameInfo = Toast.makeText(this, message, Toast.LENGTH_LONG);
        gameInfo.show();
    }

    public void confirmName(View view) {
        // todo - do something if name is empty
        EditText tx = nameDialog.mEditText;
        username = tx.getText().toString();

        nameDialog.dismiss();

        Toast toast = Toast.makeText(getApplicationContext(), "Hello " + username, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        toast.show();
    }
}
