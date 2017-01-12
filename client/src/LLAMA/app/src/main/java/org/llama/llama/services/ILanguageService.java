package org.llama.llama.services;

import android.app.Activity;

import org.jdeferred.Promise;

/**
 * Created by Daniel on 11.01.2017.
 */
public interface ILanguageService {
    Promise getLanguages();
    Promise getLanguage(String langId);
    boolean loadFlag(Activity activity, int imageViewId, String langId);
}
