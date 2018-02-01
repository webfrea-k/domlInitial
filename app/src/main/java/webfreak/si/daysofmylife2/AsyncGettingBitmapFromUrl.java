package webfreak.si.daysofmylife2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by simon.hocevar on 20.03.2017.
 */

public class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap>
{
    private Activity activity;
    ImageView custom_image;
    Button share;
    Boolean enable_button = false;
    public AsyncGettingBitmapFromUrl(Activity activity){
        this.activity = activity;
    }
    @Override
    protected Bitmap doInBackground(String... params) {

        if(params[0] == "local")
        {
            return BitmapFactory.decodeResource(activity.getResources(), R.drawable.loading_image);
        }
        else
        {
            enable_button = true;
            Bitmap bitmap = Utils.getBitmapFromURL(activity.getString(R.string.background_image_url));
            String daily_qoute = Utils.getPref("DAILY_QUOTE",activity);
            if(daily_qoute.length()<1)
            {
                daily_qoute = "Today is a great day. I started using Days of my life!";
            }
            Bitmap bitmap_withtext = Utils.drawMultilineTextToBitmap(activity,bitmap, daily_qoute);
            try
            {
                File cachePath = new File(activity.getCacheDir(), "images");
                cachePath.mkdirs(); // don't forget to make the directory
                FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                bitmap_withtext.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap_withtext;
        }

    }
    /*@Override
    protected void onPreExecute()
    {super.onPreExecute();
    }*/
    @Override
    protected void onPostExecute(Bitmap bitmap)
    {

        custom_image = (ImageView) activity.findViewById(R.id.customimage);
        share = (Button) activity.findViewById(R.id.share);
        if(custom_image != null)
        {
            custom_image.setImageBitmap(bitmap);
        }
        if(share != null && enable_button == true)
        {
            share.setEnabled(true);
        }
    }
}
