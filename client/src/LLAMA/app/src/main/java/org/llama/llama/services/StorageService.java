package org.llama.llama.services;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Felix on 13.12.2016.
 */
public class StorageService {
    public static <T> void writeToFile(Context context, String fileName, T object) {
        // use getCacheDir
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
        } catch (FileNotFoundException fnfe) {

        } catch (IOException ioe) {

        }
    }

    public static <T> T readFromFile(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            T obj = (T) is.readObject();
            is.close();
            fis.close();
            return obj;
        } catch (FileNotFoundException fnfe) {

        } catch (IOException ioe) {

        } catch (ClassNotFoundException cnfe) {

        }
        return null;
    }
}
