package webfreak.si.daysofmylife2;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by simon.hocevar on 18.03.2017.
 */

public class LoadJSONTaskQuote extends AsyncTask<String, Void, ResponseQuote> {
    Gson gson;
    ResponseQuote response;
    public LoadJSONTaskQuote(Listener listener) {

        mListener = listener;
    }

    public interface Listener {

        void onLoaded(List<Quote> androidList);

        void onError();
    }

    private Listener mListener;

    @Override
    protected ResponseQuote doInBackground(String... strings)
    {
        try
        {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            gson = new Gson();
            StorageReference gsReference = storage.getReferenceFromUrl("gs://admob-app-id-3010130871.appspot.com/daysofmylifequotes.json");

            final long ONE_MEGABYTE = 1024 * 1024;
            gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    try
                    {
                        String str = new String(bytes, "UTF-8");
                        Gson gson = new Gson();

                        response = gson.fromJson(str, ResponseQuote.class);
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
            while (response == null)
            {
                Thread.sleep(50);
            }
            return response;
        }

        catch (Exception e)
        {
            Log.e("ERROR",e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(ResponseQuote response) {

        if (response != null) {

            mListener.onLoaded(response.getQuote());

        } else {

            mListener.onError();
        }
    }
}
