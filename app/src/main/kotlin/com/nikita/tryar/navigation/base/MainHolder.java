package com.nikita.tryar.navigation.base;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alex on 14.02.15.
 */
public class MainHolder {
    private Context context;
    private static MainHolder instance;
    private int expCount = 300;
    private boolean isInSpeak = false;
    private boolean isInLaunch = false;
    public AssetManager assetManager;
    public BitmapFactory.Options bfOptions;
    private int densityDpi;

    public int getExpCount() {
        return expCount;
    }

    public void setExpCount(int expCount) {
        this.expCount = expCount;
    }

    private MainHolder  ()
    {
    }
    public static MainHolder getInstance()
    {
        if (instance == null)
            instance = new MainHolder();
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
        assetManager = this.context.getResources().getAssets();
        bfOptions = new BitmapFactory.Options();
        bfOptions.inSampleSize = 1;
        bfOptions.inDither = false;                     //Disable Dithering mode
        bfOptions.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        bfOptions.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        bfOptions.inTempStorage = new byte[32 * 1024];
        bfOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        densityDpi = this.context.getResources().getDisplayMetrics().densityDpi;

    }

    public Context getContext() {
        return context;
    }

    public float dpToPixel(float dp){
        return dp * (densityDpi / 160f);
    }

    public Bitmap getSampledBitmapFromAssets(String bitmapFile, int inSampleSize) {
        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(bitmapFile);
            bfOptions.inSampleSize = inSampleSize;
            bitmap = BitmapFactory.decodeStream(istr, null, bfOptions);
        } catch (IOException e) {
            return null;
        }

        return bitmap;
    }

    public interface OnNeedToCloseListener {
        void needToCloseDetailActivity();
    }

    private OnNeedToCloseListener mListener;

    public void setListener(OnNeedToCloseListener listener) {
        mListener = listener;
    }

    public void removeListener(OnNeedToCloseListener listener) {
        mListener = null;
    }

    public void needToClose() {
        if(mListener != null) {
            mListener.needToCloseDetailActivity();
        }
    }

    public boolean getIsInSpeak() {
        return isInSpeak;
    }

    public void setIsInSpeak(boolean isInSpeak) {
        this.isInSpeak = isInSpeak;
    }

    public boolean getIsInLaunch() {
        return isInLaunch;
    }

    public void setIsInLaunch(boolean isInLaunch) {
        this.isInLaunch = isInLaunch;
    }
}
