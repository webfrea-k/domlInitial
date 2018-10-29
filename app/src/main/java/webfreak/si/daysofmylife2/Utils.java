package webfreak.si.daysofmylife2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;

/**
 * Created by simon.hocevar on 17.03.2017.
 */

public class Utils
{
    public static void putPref(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putPrefInt(String key, int value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public static void putPrefLong(String key, long value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }
    public static void putPrefFloat(String key, float value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key,value);
        editor.commit();
    }
    public static void putPrefDouble(String key, double value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key,Double.doubleToRawLongBits(value));
        editor.commit();
    }
    public static double getPrefDouble(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Double.longBitsToDouble(preferences.getLong(key, 0));
    }
    public static float getPrefFloat(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getFloat(key, 0);
    }
    public static String getPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }
    public static int getPrefInt(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, 0);
    }
    public static long getPrefLong(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(key,0);
    }
    public static Bitmap combineBitmaps(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    public static Bitmap drawTextToBitmap(Context gContext, Bitmap gResId, String gText) {


        Bitmap bitmap = gResId;
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        // text size in pixels
        paint.setTextSize((int) (45 * scale));
        // text shadow

        paint.setShadowLayer(1f, 10f, 10f, Color.BLACK);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2;

        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }

    public static Bitmap drawMultilineTextToBitmap(Context gContext, Bitmap gResId, String gText) {
        if(gText.contains("You have just"))
        {
            gText = gText.replace("You have just","I have just");
        }
        // prepare canvas
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = gResId;

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        Canvas canvasSignature = new Canvas(bitmap);
        Canvas canvasSignature2 = new Canvas(bitmap);
        // new antialiased Paint
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TextPaint signature = new TextPaint(Paint.ANTI_ALIAS_FLAG);// text color - #3D3D3D
        TextPaint signature2 = new TextPaint(Paint.ANTI_ALIAS_FLAG);// text color - #3D3D3D
        paint.setColor(Color.WHITE);
        signature.setColor(Color.WHITE);
        signature2.setColor(Color.WHITE);
        // text size in pixels

        paint.setTextSize((int) (45 * scale));
        signature.setTextSize((int) (25 * scale));
        signature2.setTextSize((int) (23 * scale));
        // text shadow
        paint.setShadowLayer(3f, 8f, 8f, Color.BLACK);
        signature.setShadowLayer(3f, 8f, 8f, Color.BLACK);
        signature2.setShadowLayer(3f, 8f, 8f, Color.BLACK);
        // set text width to canvas width minus 16dp padding
        int textWidth = canvas.getWidth() - (int) (16 * scale);

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(gText, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        StaticLayout signatureLayout = new StaticLayout("@DaysOfMyLife", signature, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        StaticLayout signatureLayout2 = new StaticLayout("goo.gl/PTBn6D", signature, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // get height of multiline text
        int textHeight = textLayout.getHeight();

        // get position of text's top left corner
        float x = (bitmap.getWidth() - textWidth)/2;
        float y = (bitmap.getHeight() - textHeight)/2;

        // draw text to the Canvas center
        canvas.save();
        canvasSignature.save();
        canvasSignature2.save();
        canvas.translate(x, y);
        canvasSignature.translate(20, 900);
        int width = bitmap.getWidth();
        canvasSignature2.translate(width-500, 900);
        textLayout.draw(canvas);
        signatureLayout.draw(canvasSignature);
        signatureLayout2.draw(canvasSignature2);
        canvas.restore();
        canvasSignature.restore();
        canvasSignature2.restore();

        return bitmap;
    }

    public static void showNotification(Context context, String user, String celebrity)
    {
        Intent intentAction = new Intent(context, MainActivity.class);
        intentAction.setAction(Intent.ACTION_MEDIA_SHARED);
        String textToNotification = "";
        if(user.length()>0)
        {
            textToNotification = user + " has just outlived "+celebrity;
        }
        else
        {
            textToNotification = "You have just outlived "+celebrity;
        }
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intentAction, 0);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_notification) // notification icon
                .setContentTitle(textToNotification) // title for notification
                .setContentText("Congratulations!") // message for notification
                .addAction(R.mipmap.ic_share, "SHARE", pIntent)
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,intent,Intent.FILL_IN_ACTION);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
    public static String getNextPersonToOutliveSelf(List<Celebrity> androidList, Context ctx)
    {
        long user_birthday_long = Utils.getPrefLong("USER_BIRTHDAY",ctx);
        DateTime current_date = new DateTime();
        current_date = current_date.withHourOfDay(0).withMinuteOfHour(1);
        long millisalive = current_date.getMillis() - user_birthday_long;
        int daysAlive = (int) TimeUnit.MILLISECONDS.toDays(millisalive);
        boolean whosNext = true;

        for (Celebrity celebrity : androidList)
        {
            int days_difference = daysAlive - parseInt(celebrity.getDaysAlive());
            String outlived_diff = String.valueOf(days_difference);

            if (outlived_diff.startsWith("-"))
            {
                int numberOfDaysDifference = parseInt(outlived_diff.substring(1));
                if (numberOfDaysDifference > 365)
                {
                    double years = (double) numberOfDaysDifference / (double) 365;
                    if (whosNext && user_birthday_long > 0)//Get the first celebrity to outlive
                    {
                        Utils.putPref("WHOS_NEXT", celebrity.getName().trim() + ", " + String.format(Locale.US, "%.2f", years) + " years more.", ctx);
                    }
                }
                else
                {
                    if (whosNext && user_birthday_long > 0)//Get the first celebrity to outlive
                    {
                        if (outlived_diff.substring(1) == "1")
                        {
                            Utils.putPref("WHOS_NEXT", celebrity.getName().trim() + ", " + outlived_diff.substring(1) + " day more.", ctx);
                        } else
                        {
                            Utils.putPref("WHOS_NEXT", celebrity.getName().trim() + ", " + outlived_diff.substring(1) + " days more.", ctx);
                        }
                    }
                }
                whosNext = false;
            } else if (outlived_diff.startsWith("0") && user_birthday_long > 0)
            {
                Utils.putPref("WHOS_NEXT", "You have just outlived " + celebrity.getName().trim(), ctx);
                whosNext = false;
            }

        }
        return Utils.getPref("WHOS_NEXT", ctx);
    }

    public static void requestNewInterstitial(InterstitialAd mInterstitialAd, Context ctx) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(ctx.getString(R.string.banner_device_id))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
