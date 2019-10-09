package com.example.fifi.quadappctrl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import io.github.controlwear.virtual.joystick.android.JoystickView;

import static java.lang.Math.abs;


public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    int interval_send = 10;
    int server_and_local_port = 3333;
    DatagramSocket socket_send;
    InetAddress local;
    String text;
    String messageStr="##123123**";
    String ledOrM = "";
    boolean ignoreTouch = false;
    // test only

    int motorSX = 0;

    int motorSpeed = 0;


    private Runnable runnable_send = new Runnable() {
        @Override
        public void run() {
            //update ui
//            TextView textViewM = findViewById(R.id.textView);
//            textViewM.setText(text);

            //start the Runnable again
            handler.postDelayed(this, interval_send);
        }
    };

    class SendThread implements Runnable {
        @Override
        public void run() {
            try {

                socket_send = new DatagramSocket();
                local = InetAddress.getByName("192.168.4.1");
//                int msg_length = messageStr.length();
//                byte[] messageS = messageStr.getBytes();
//                DatagramPacket p1 = new DatagramPacket(messageS, msg_length, local, server_and_local_port);
//                socket_send.send(p1);
                long startTime = 0;

                while(true) {

                    if((System.currentTimeMillis() - startTime) > interval_send){
                        int msg_length = messageStr.length();
                        byte[] messageS = messageStr.getBytes();
                        DatagramPacket p1 = new DatagramPacket(messageS, msg_length, local, server_and_local_port);
                        startTime = System.currentTimeMillis();
                        socket_send.send(p1);
                        messageStr="##123123**";
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveThread implements Runnable {
        @Override
        public void run() {
            try {
                //String text;
                byte[] message = new byte[1500];
                DatagramPacket p = new DatagramPacket(message, message.length);
                DatagramSocket s = new DatagramSocket(server_and_local_port);
                while(true) {
                    s.receive(p);
                    text = new String(message, 0, p.getLength());
                    Log.i("bla", "ArduinoCounter: " + text);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); // Turn screen on if off
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // Keep screen on

        final JoystickView joystick = findViewById(R.id.joystickView4);
        final JoystickView joystick2 = findViewById(R.id.joystickView3);
        joystick.setAutoReCenterButton(true);

        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                // do whatever you want
            }
        });

        joystick.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i("bla", "yyyyyyyy: " + (int)joystick.getY());
                Log.i("bla", "hhhhhhhhh: " + joystick.getHeight());
                Log.i("bla", "wwwwwwww: " + joystick.getWidth());
                Log.i("bla", "xxxxxxxx: " + joystick.getX());

                int yCent = (int)joystick.getY() + joystick.getHeight() / 2;
                int xCent = (int)joystick.getX() + joystick.getWidth() / 2;


                int ypos = (int)motionEvent.getY();
                int xpos = (int)motionEvent.getX();

                if (motionEvent.getAction() == motionEvent.ACTION_DOWN) {
                    if (ypos < 300 || ypos > 500) {
                        ignoreTouch = true;
                        return true;
                    }
                }

                if (motionEvent.getAction() == motionEvent.ACTION_MOVE && ignoreTouch == true) {
                    return true;
                }

                if (motionEvent.getAction() == motionEvent.ACTION_UP && ignoreTouch == true) {
                    ignoreTouch = false;
                    return true;
                }

                return false;
            }
        });


//        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                handler.postDelayed(runnable_send, interval_send);
//                new Thread(new ReceiveThread()).start();
//                new Thread(new SendThread()).start();
//
//            }
//        });


//        final TextView t1 = findViewById(R.id.textView2);
//        final SeekBar sk= findViewById(R.id.mySeekBar3);
//        sk.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                if (abs(motorSX - (int)motionEvent.getX()) > 60) {
//                    Log.i("bla", "dasdsdsdas");
//                    return true;
//                } else {
//                    motorSX = (int)motionEvent.getX();
//
//                    Log.i("bla", "motionEventX: " + motionEvent.getX());
//                    Log.i("bla", "motionEventY: " + motionEvent.getY());
//
//                    return false;
//                }
//            }
//        });
//        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
////                messageStr = "##30**";
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
////                messageStr = "##300**";
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
//                // TODO Auto-generated method stub
//
//                messageStr = "##mot" + Integer.toString(progress) + "**";
//                Log.i("bla", "fadeup: " + messageStr);
//                t1.setText(Integer.toString(progress));
//
//            }
//        });


//        Switch onOffSwitch = findViewById(R.id.switch1);
//        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.v("Switch State=", ""+isChecked);
//                int v = (isChecked) ? 1 : 0;
//
//                messageStr = "##led" + v + "**";
//                Log.i("bla", "ledButton: " + messageStr);
//            }
//
//        });












    }

}
