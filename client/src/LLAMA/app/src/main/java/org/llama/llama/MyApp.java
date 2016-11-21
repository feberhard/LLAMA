package org.llama.llama;

import android.app.Application;

import org.llama.llama.services.DaggerServiceComponent;
import org.llama.llama.services.ServiceComponent;
import org.llama.llama.services.ServiceModule;

/**
 * Created by Felix on 21.11.2016.
 */

public class MyApp extends Application {
    private ServiceComponent mServiceComponent;

    @Override
    public void onCreate() {
        super.onCreate();

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
}
