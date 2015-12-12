package com.ferasinfotech.gwreader;

import android.graphics.Typeface;
import android.content.Context;

/**
 * Created by jferas on 12/12/15.
 *
 * Font manager class enabling the inclusion of Font Awesome.
 */
public class FontManager {

    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

}