package org.llama.llama.services;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;

/**
 * Created by Daniel on 11.01.2017.
 */

public class FlagService implements IFlagService {
    @Override
    public boolean loadFlag(Activity activity, int imageViewId, String langId) {
        ImageView imageView = (ImageView) activity.findViewById(imageViewId);

        // read a flag from the assets folder
        SVG svg = null;
        try {
            svg = SVG.getFromAsset(activity.getApplicationContext().getAssets(), "flags/" + langId + ".svg");
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
            paint.setColor(Color.BLACK);
            canvas.drawCircle(50, 50, 10, paint);
            imageView.setImageBitmap(bitmap);
        }

        return true;
    }
}
