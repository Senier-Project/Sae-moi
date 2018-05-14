package com.three_eung.saemoi;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

/**
 * Created by CH on 2018-02-18.
 */

public class InitApp extends Application {
    private static InitApp self;
    public static FirebaseAuth sAuth;
    public static FirebaseUser sUser;
    public static FirebaseDatabase sDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        self = this;
        KakaoSDK.init(new KakaoAdapter() {
            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return self;
                    }
                };
            }
        });

        sAuth = FirebaseAuth.getInstance();
        sUser = sAuth.getCurrentUser();
        sDatabase = FirebaseDatabase.getInstance();


    }

    @Override
    public void onTerminate() {
        sAuth = null;
        sUser = null;
        sDatabase = null;

        super.onTerminate();
    }
}
