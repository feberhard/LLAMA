package org.llama.llama.services;

import android.app.Activity;

/**
 * Created by Daniel on 11.01.2017.
 */
public interface IFlagService {
    boolean loadFlag(Activity activity, int imageViewId, String langId);
}
