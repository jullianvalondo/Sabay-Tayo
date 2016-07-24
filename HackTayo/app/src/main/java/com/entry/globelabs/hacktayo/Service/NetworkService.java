package com.entry.globelabs.hacktayo.Service;

import retrofit2.Retrofit;

/**
 * Created by JettRobin on 7/24/2016.
 */
public class NetworkService {

    private static final NetworkService uniqueInstance = new NetworkService();

    private final String SHORT_CODE = "21580375";
    private final String APP_ID = "qMMGCL5RKpu4Rik7zacRaxuGzMAMCM75";
    private final String APP_SECRET = "a115c12fb38ab10c1f8dab50def07d166fbeb165461f083eeb767d9e88755b17";
    private final String BASE_URL = "https://developer.globelabs.com.ph/";

    private Retrofit retrofit;
    private GlobeSMSApi globeService;

    private NetworkService(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        globeService = retrofit.create(GlobeSMSApi.class);
    }

    public NetworkService getUniqueInstance(){
        return uniqueInstance;
    }

    public void testGlobeServiec(){}
}
