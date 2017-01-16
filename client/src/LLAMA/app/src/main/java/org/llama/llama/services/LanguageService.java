package org.llama.llama.services;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.llama.llama.MyApp;
import org.llama.llama.model.Language;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 11.01.2017.
 */

public class LanguageService implements ILanguageService {
    private static final String TAG = "LanguageService";
    private static Map<String, Language> langs = new LinkedHashMap<>();

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public synchronized Promise getLanguages() {
        final Deferred deferred = new DeferredObject();
        final Promise promise = deferred.promise();

        if (langs.isEmpty()) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference ref = database.getReference().child("languages");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Language l = child.getValue(Language.class);
                        l.setId(child.getKey());
                        langs.put(child.getKey(), l);
                    }
                    langs = sortByValue(langs);
                    deferred.resolve(langs);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    deferred.reject(null);
                }
            });
        } else {
            deferred.resolve(langs);
        }

        return promise;
    }

    @Override
    public synchronized Promise getLanguage(String langId) {
        final Deferred deferred = new DeferredObject();
        final Promise promise = deferred.promise();

        if (langs.isEmpty()) {
            getLanguages();
        }

        deferred.resolve(langs.get(langId));
        return promise;
    }

    @Override
    public boolean loadFlag(View view, int imageViewId, String langId) {
        ImageView imageView = (ImageView) view.findViewById(imageViewId);

        // read a flag from the assets folder
        SVG svg = null;
        try {
            svg = SVG.getFromAsset(MyApp.getAppContext().getAssets(), "flags/" + langId + ".svg");
        } catch (SVGParseException | IOException e) {
            return false;
        }

        // create a canvas to draw onto
        if (svg.getDocumentWidth() != -1) {
            Bitmap bitmap = Bitmap.createBitmap((int) Math.ceil(svg.getDocumentWidth()),
                    (int) Math.ceil(svg.getDocumentHeight()),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // clear background to white
            canvas.drawRGB(255, 255, 255);

            // render the flag onto our canvas
            svg.renderToCanvas(canvas);

            // draw flag on imageView
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            imageView.setImageBitmap(bitmap);
        }

        return true;
    }
}
