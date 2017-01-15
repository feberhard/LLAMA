package org.llama.llama.services;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Felix on 21.11.2016.
 */
@Module
public class ServiceModule {

    public ServiceModule() {

    }

    @Provides
    @Singleton
    static IUserService providesIUserService() {
        return new UserService();
    }

    @Provides
    @Singleton
    static IChatService providesIChatService() {
        return new ChatService();
    }

    @Provides
    @Singleton
    static ILanguageService providesILanguageService() {
        return new LanguageService();
    }

    @Provides
    @Singleton
    static ICountryService providesICountryService(){
        return new CountryService();
    }
}
