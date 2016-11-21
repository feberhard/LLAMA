package org.llama.llama;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Felix on 21.11.2016.
 */

@Module //a module could also include other modules
public class AppContextModule {
    private final Application application;

    public AppContextModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Application providesApplication() {
        return this.application;
    }
}
