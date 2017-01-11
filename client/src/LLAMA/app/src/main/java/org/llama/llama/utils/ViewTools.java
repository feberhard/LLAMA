package org.llama.llama.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by Felix on 11.1.2017.
 */

//http://stackoverflow.com/a/29823008/6364886
public class ViewTools {
    /**
     * @param root usually Activity.getWindow().getDecorView() or your custom Toolbar
     */
    public static
    @Nullable
    View findActionBarTitle(@NonNull View root) {
        return findActionBarItem(root, "action_bar_title", "mTitleTextView");
    }

    /**
     * @param root usually Activity.getWindow().getDecorView() or your custom Toolbar
     */
    public static
    @Nullable
    View findActionBarSubTitle(@NonNull View root) {
        return findActionBarItem(root, "action_bar_subtitle", "mSubtitleTextView");
    }

    private static
    @Nullable
    View findActionBarItem(@NonNull View root,
                           @NonNull String resourceName, @NonNull String toolbarFieldName) {
        View result = findViewSupportOrAndroid(root, resourceName);

        if (result == null) {
            View actionBar = findViewSupportOrAndroid(root, "action_bar");
            if (actionBar != null) {
                result = reflectiveRead(actionBar, toolbarFieldName);
            }
        }
        if (result == null && root.getClass().getName().endsWith("widget.Toolbar")) {
            result = reflectiveRead(root, toolbarFieldName);
        }
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private static
    @Nullable
    View findViewSupportOrAndroid(@NonNull View root, @NonNull String resourceName) {
        Context context = root.getContext();
        View result = null;
        if (result == null) {
            int supportID = context.getResources().getIdentifier(resourceName, "id", context.getPackageName());
            result = root.findViewById(supportID);
        }
        if (result == null) {
            int androidID = context.getResources().getIdentifier(resourceName, "id", "android");
            result = root.findViewById(androidID);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> /*@Nullable*/ T reflectiveRead(@NonNull Object object, @NonNull String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception ex) {
            Log.w("HACK", "Cannot read " + fieldName + " in " + object, ex);
        }
        return null;
    }
}
