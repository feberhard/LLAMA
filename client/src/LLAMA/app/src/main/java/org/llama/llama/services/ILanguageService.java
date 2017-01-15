package org.llama.llama.services;

import android.app.Activity;
import android.view.View;

import org.jdeferred.Promise;

/**
 * Created by Daniel on 11.01.2017.
 */
public interface ILanguageService {
    Promise getLanguages();
    Promise getLanguage(String langId);
    boolean loadFlag(View view, int imageViewId, String langId);
}
