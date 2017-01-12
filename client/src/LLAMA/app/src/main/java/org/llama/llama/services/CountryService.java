package org.llama.llama.services;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import org.llama.llama.model.Country;
import org.llama.llama.model.Language;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 12.01.2017.
 */

public class CountryService implements ICountryService {
    private static final String TAG = "CountryService";
    private static Map<String, Country> countries = new LinkedHashMap<>();

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
    public synchronized Promise getCountries() {
        final Deferred deferred = new DeferredObject();
        final Promise promise = deferred.promise();

        if (countries.isEmpty()) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference ref = database.getReference().child("countries");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Country c = child.getValue(Country.class);
                        c.setId(child.getKey());
                        countries.put(child.getKey(), c);
                    }
                    countries = sortByValue(countries);
                    deferred.resolve(countries);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    deferred.reject(null);
                }
            });
        } else {
            deferred.resolve(countries);
        }

        return promise;
    }

    @Override
    public synchronized Promise getCountry(String countryId) {
        final Deferred deferred = new DeferredObject();
        final Promise promise = deferred.promise();

        if (countries.isEmpty()) {
            getCountries();
        }

        deferred.resolve(countries.get(countryId));
        return promise;
    }
}
