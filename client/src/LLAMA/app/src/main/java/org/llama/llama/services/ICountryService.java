package org.llama.llama.services;

import android.app.Activity;

import org.jdeferred.Promise;

/**
 * Created by Daniel on 12.01.2017.
 */
public interface ICountryService {
    Promise getCountries();
    Promise getCountry(String countryId);
}
