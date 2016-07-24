package com.entry.globelabs.hacktayo.Model;

/**
 * Created by JettRobin on 7/24/2016.
 */
public class UserConsent {

    private String accessToken;
    private String subscriberNumber;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSubscriberNumber() {
        return subscriberNumber;
    }

    public void setSubscriberNumber(String subscriberNumber) {
        this.subscriberNumber = subscriberNumber;
    }
}
