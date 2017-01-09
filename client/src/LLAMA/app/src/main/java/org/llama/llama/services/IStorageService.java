package org.llama.llama.services;

import android.content.Context;

/**
 * Created by Felix on 13.12.2016.
 */

public interface IStorageService {
    void writeToFile(Context context, String fileName, Object object);

    Object readFromFile(Context context, String fileName);
}
