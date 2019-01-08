package com.rayanehsabz.choobid;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.LogLevel;

/**
 * Created by sabz3 on 01/23/2018.
 */

public class GlobalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        String appToken = "3svf2tilsfuo";
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(this, appToken, environment);
        config.setLogLevel(LogLevel.VERBOSE);
        Adjust.onCreate(config);

        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
    }


    private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
