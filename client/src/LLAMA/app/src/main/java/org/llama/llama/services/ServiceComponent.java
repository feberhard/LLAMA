package org.llama.llama.services;

import org.llama.llama.AppContextModule;
import org.llama.llama.MainActivity;
import org.llama.llama.MyFirebaseInstanceIDService;
import org.llama.llama.auth.SignInActivity;
import org.llama.llama.chat.ChatActivity;
import org.llama.llama.chat.chatinfo.ChatInfoActivity;
import org.llama.llama.map.MapsActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Felix on 21.11.2016.
 */

@Singleton
@Component(modules = {AppContextModule.class, ServiceModule.class})
public interface ServiceComponent {
    void inject(SignInActivity signInActivity);
    void inject(MainActivity mainActivity);
    void inject(MyFirebaseInstanceIDService myFirebaseInstanceIDService);
    void inject(ChatActivity chatActivity);
    void inject(ChatInfoActivity chatInfoActivity);
    void inject(MapsActivity mapsActivity);
}
