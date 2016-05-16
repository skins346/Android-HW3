package com.example.user.androidhw3;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;
/**
 Program to send message
 Author: Kim Young Song.
 E-mail Address: infall346@gmail.com.
 Programming homework #3
 Last Changed: May 16, 2016
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView browser = (WebView) findViewById(R.id.webView1);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        // if the html file is in the app's memory space use:
        browser.loadUrl("file:///android_asset/hw3.html");
    }

    public static class JavaScriptInterface {
        Context mContext;
        String number;
        static String result = "";
        static String message = "";

        /**
         * Instantiate the interface and set the context
         */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public String receiveData(String data) {
            number = data;   //receive from html
            result =  result + number;   //append all data from html
            return number;
        }

        @JavascriptInterface
        public void receiveMessage(String data) {
            message = data;   //receive from html
         }

        @JavascriptInterface
        public void executeSMS() {
            sendSMS();    //send message
        }
//////////////////////////////////////////////////From here , used open source
        @JavascriptInterface   //send message
        public void sendSMS(){

            String smsNum = JavaScriptInterface.result;
            String smsText = JavaScriptInterface.message;

            if (smsNum.length()>0 && smsText.length()>0){
                sendSMS(smsNum, smsText);
            }else{
                Toast.makeText(mContext, "모두 입력해 주세요", Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void sendSMS(String smsNumber, String smsText){
            PendingIntent sentIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_SENT_ACTION"), 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_DELIVERED_ACTION"), 0);

            /**
             * SMS가 발송될때 실행
             * When the SMS massage has been sent
             */
            mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            // 전송 성공
                            Toast.makeText(mContext, "전송 완료", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            // 전송 실패
                            Toast.makeText(mContext, "전송 실패", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            // 서비스 지역 아님
                            Toast.makeText(mContext, "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            // 무선 꺼짐
                            Toast.makeText(mContext, "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            // PDU 실패
                            Toast.makeText(mContext, "PDU Null", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter("SMS_SENT_ACTION"));

            /**
             * SMS가 도착했을때 실행
             * When the SMS massage has been delivered
             */
            mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            // 도착 완료
                            Toast.makeText(mContext, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            // 도착 안됨
                            Toast.makeText(mContext, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter("SMS_DELIVERED_ACTION"));

            SmsManager mSmsManager = SmsManager.getDefault();
            mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);

        }
    }
}
