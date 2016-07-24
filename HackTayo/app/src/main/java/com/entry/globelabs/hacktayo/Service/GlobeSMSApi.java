package com.entry.globelabs.hacktayo.Service;

import com.entry.globelabs.hacktayo.Model.UserConsent;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by JettRobin on 7/24/2016.
 */
public interface GlobeSMSApi {

    @POST("oauth/{accessToken}")
    public Call<UserConsent> getUserConsentAuth(@Path("accessToken") String accessToken, @Query("app_id") String appId, @Query("app_secret") String appSecret, @Query("code") String code);
}
