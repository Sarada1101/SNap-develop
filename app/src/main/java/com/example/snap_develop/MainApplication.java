package com.example.snap_develop;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.Objects;

import timber.log.Timber;

public class MainApplication extends Application {

    public final static int TIMELINE_POS = 0;
    public final static int MAP_POS = 1;
    public final static int USER_POS = 2;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new MyDebugTree());
        }
    }


    // VectorDrawableをBitmapに変換
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = Objects.requireNonNull(ContextCompat.getDrawable(context, drawableId));

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
