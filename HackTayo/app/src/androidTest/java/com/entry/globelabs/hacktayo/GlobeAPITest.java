package com.entry.globelabs.hacktayo;

import android.content.Intent;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.*;

/**
 * Created by JettRobin on 7/24/2016.
 */
@RunWith(AndroidJUnit4.class)
public class GlobeAPITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    SmsManager smsManager = SmsManager.getDefault();

    String destinationNum = "21581666";

    @Test
    public void testIdealParams() {
        String message = "PHPIN/PHSBL/2016-10-31/14:00/1/Jett Andres";
        smsManager.sendTextMessage(destinationNum, null, message, null, null);
    }


    public void testIncompleteParams() {
        String message = "PHPIN/PHSBL/2016-10-30/";
        smsManager.sendTextMessage(destinationNum, null, message, null, null);
    }


    public void testUnorderedParams() {
        String message = "PHPIN/3/testing unordered message/PHSBL/14:00/2016-10-30/";
        smsManager.sendTextMessage(destinationNum, null, message, null, null);
    }


    public void testSubscribeUnsubscribe() {
        String destinationNum = "21580375";

        String message = "STOP";
        smsManager.sendTextMessage(destinationNum, null, message, null, null);

        message = "INFO";
        smsManager.sendTextMessage(destinationNum, null, message, null, null);

        message = "YES";
        smsManager.sendTextMessage(destinationNum, null, message, null, null);
    }

    public void testSpamSMS(){
        String message = "testing annoying spam text from hackathon";

        for(int x = 0; x <= 5; x++){
            smsManager.sendTextMessage("222", null, message, null, null);
        }
    }

}