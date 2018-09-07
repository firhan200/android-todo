package com.learning.firhan.todo;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.learning.firhan.todo.Constant.SettingNames;
import com.learning.firhan.todo.Helpers.SettingDatabaseHelper;
import com.learning.firhan.todo.Models.Setting;

public class PinActivity extends AppCompatActivity {

    static final String DEFAULT_PIN = ".";
    static final String HIDE_PIN = "*";
    int currentPosition;
    String pin,firstPin,confirmPin, pin1Text,pin2Text,pin3Text,pin4Text;
    boolean isSetPin, isSetEnablePin, isConfirmPin;
    Button button0,button1,button2,button3,button4,button5,button6,button7,button8,button9,buttonDel;
    TextView pin1,pin2,pin3,pin4,pinLabel;
    SettingDatabaseHelper settingDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        //init db
        settingDatabaseHelper = new SettingDatabaseHelper(this);

        //init id
        pin1 = (TextView)findViewById(R.id.pin1);
        pin2 = (TextView)findViewById(R.id.pin2);
        pin3 = (TextView)findViewById(R.id.pin3);
        pin4 = (TextView)findViewById(R.id.pin4);
        pinLabel = (TextView)findViewById(R.id.pin_label);

        button0 = (Button)findViewById(R.id.button0);
        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);
        button3 = (Button)findViewById(R.id.button3);
        button4 = (Button)findViewById(R.id.button4);
        button5 = (Button)findViewById(R.id.button5);
        button6 = (Button)findViewById(R.id.button6);
        button7 = (Button)findViewById(R.id.button7);
        button8 = (Button)findViewById(R.id.button8);
        button9 = (Button)findViewById(R.id.button9);
        buttonDel = (Button)findViewById(R.id.buttonDel);

        //reset pin
        resetPin();

        //get intent
        isSetEnablePin = getIntent().getBooleanExtra("setEnablePin", false);
        isSetPin = getIntent().getBooleanExtra("setPin", false);
        if(isSetPin){
            isConfirmPin = false;
            pinLabel.setVisibility(View.VISIBLE);
            pinLabel.setText("Insert your 4 digit PIN.");
        }else{
            //there is no intent extra for setPin
            //check is locked/not
            checkPinEnabled();
        }

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTapped("0");
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTapped("1");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTapped("2");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTapped("3");
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTapped("4");
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTapped("5");
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTapped("6");
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTapped("7");
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTapped("8");
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTapped("9");
            }
        });

        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete current pin label
                if(currentPosition==2){
                    pin1.setText(DEFAULT_PIN);
                    pin1Text = "";
                }else if(currentPosition==3){
                    pin2.setText(DEFAULT_PIN);
                    pin2Text = "";
                }else if(currentPosition==4){
                    pin3.setText(DEFAULT_PIN);
                    pin3Text = "";
                }else if(currentPosition==5){
                    pin4.setText(DEFAULT_PIN);
                    pin4Text = "";
                }

                //decrease current position
                if(currentPosition > 1){
                    currentPosition--;
                }
            }
        });
    }

    private void buttonTapped(String number){
        if(currentPosition==1){
            pin1.setText(HIDE_PIN);
            pin1Text = number;
        }else if(currentPosition==2){
            pin2.setText(HIDE_PIN);
            pin2Text = number;
        }else if(currentPosition==3){
            pin3.setText(HIDE_PIN);
            pin3Text = number;
        }else if(currentPosition==4){
            pin4.setText(HIDE_PIN);
            pin4Text = number;
        }

        //position only between 1 to 4
        if(currentPosition >= 1 && currentPosition <=4){
            pin = pin1Text + pin2Text + pin3Text + pin4Text;
            currentPosition++;
        }

        //check if pin already 4 digits
        if(currentPosition==5){
            if(isSetPin){
                if(!isConfirmPin){
                    setToConfirmPin();
                }else{
                    validatingConfirmPin();
                }
            }else{
                validatingPin();
            }
        }
    }

    private void resetPin(){
        currentPosition = 1;
        pin = "";
        pin1Text = "";
        pin2Text = "";
        pin3Text = "";
        pin4Text = "";
        pin1.setText(DEFAULT_PIN);
        pin2.setText(DEFAULT_PIN);
        pin3.setText(DEFAULT_PIN);
        pin4.setText(DEFAULT_PIN);
    }

    private void setToConfirmPin(){
        isConfirmPin = true;
        firstPin = pin;
        resetPin();
        pinLabel.setText("Confirm Your PIN.");
    }

    private void validatingConfirmPin(){
        confirmPin = pin;
        if(firstPin.equals(confirmPin)){
            //pin equals with confirm pin
            //save pin to sqlite
            Setting pinObj = settingDatabaseHelper.getSettingByTitle(SettingNames.PIN_CODE, true);
            settingDatabaseHelper.updateData(SettingNames.PIN_CODE, confirmPin);

            //check if is enabled pin has been set
            Setting enabledPinObj = settingDatabaseHelper.getSettingByTitle(SettingNames.ENABLED_PIN, true);
            //enabled pin has been set before
            if(isSetEnablePin){
                //set pin to enabled
                settingDatabaseHelper.updateData(SettingNames.ENABLED_PIN, "True");
            }

            Toast.makeText(getApplicationContext(), "Successfuly Set PIN", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            //confirm pin incorrect
            Snackbar snackbar = Snackbar.make(pinLabel, "Confirm PIN Incorret!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Try Again", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetPin();
                }
            });
            snackbar.show();
        }
    }

    private void validatingPin(){
        //get pin from sqlite
        Setting pinObj = settingDatabaseHelper.getSettingByTitle(SettingNames.PIN_CODE, false);
        if(pinObj!=null){
            if(pin.equals(pinObj.getValue())){
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainActivityIntent);
            }else{
                //reset
                resetPin();

                Snackbar.make(pinLabel, "Incorrect PIN!", Snackbar.LENGTH_SHORT).show();
            }
        }else{
            Snackbar.make(pinLabel, "PIN has not been set", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void checkPinEnabled(){
        boolean isLock = true;

        //check intent
        //get setting
        try{
            Setting enablePinObj = settingDatabaseHelper.getSettingByTitle(SettingNames.ENABLED_PIN, false);
            if(enablePinObj!=null){
                //app has enabled pin obj
                if(enablePinObj.getValue().equals("True")){
                    //app is locked
                    isLock = true;
                }else{
                    isLock = false;
                }
            }else{
                isLock = false;
            }
        }catch (Exception ex){
            isLock = false;
            System.out.println(ex.getMessage());
        }


        if(!isLock){
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        }
    }
}
