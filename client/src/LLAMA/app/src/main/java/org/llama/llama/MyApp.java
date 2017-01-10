package org.llama.llama;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import org.llama.llama.services.DaggerServiceComponent;
import org.llama.llama.services.ServiceComponent;
import org.llama.llama.services.ServiceModule;

/**
 * Created by Felix on 21.11.2016.
 */

public class MyApp extends MultiDexApplication {
    private ServiceComponent mServiceComponent;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();

//        mServiceComponent = DaggerServiceComponent.builder()
//                .appContextModule(new AppContextModule(this))
//                .serviceModule(new ServiceModule())
//                .build();
        // when the modules don't have parameters create is enough
        mServiceComponent = DaggerServiceComponent.create();
    }

    public ServiceComponent getServiceComponent(){
        return mServiceComponent;
    }

    public static Context getAppContext(){
        return MyApp.context;
    }
}
