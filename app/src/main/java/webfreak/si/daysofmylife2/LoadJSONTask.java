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

public class LoadJSONTask extends AsyncTask<String, Void, Response> {
    Gson gson;
    Response response;
    public LoadJSONTask(Listener listener) {

        mListener = listener;
    }

    public interface Listener {

        void onLoaded(List<Celebrity> androidList);

        void onError();
    }

    private Listener mListener;

    @Override
    protected Response doInBackground(String... strings)
    {
        try
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            gson = new Gson();
            StorageReference gsReference = storage.getReferenceFromUrl("gs://admob-app-id-3010130871.appspot.com/daysofmylifeoutlived.json");

            final long ONE_MEGABYTE = 1024 * 1024;
            gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    try
                    {
                        String str = new String(bytes, "UTF-8");
                        Gson gson = new Gson();

                         response = gson.fromJson(str, Response.class);
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

        catch (Exception ex)
        {
            Log.e("ERROR",ex.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response response) {

        if (response != null) {

            mListener.onLoaded(response.getCelebrity());

        } else {

            mListener.onError();
        }
    }
}
