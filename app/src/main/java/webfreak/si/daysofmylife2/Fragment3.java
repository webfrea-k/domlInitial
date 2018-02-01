package webfreak.si.daysofmylife2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;

/**
 * Created by simon.hocevar on 15.03.2017.
 */

public class Fragment3 extends Fragment {
    View rootView;
    ImageView custom_image;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_3, container, false);
        custom_image = (ImageView) rootView.findViewById(R.id.customimage);

        new AsyncGettingBitmapFromUrl(getActivity()).execute("local");
        if(custom_image.getDrawable() == null)
        {
            new AsyncGettingBitmapFromUrl(getActivity()).execute("web");
        }
        return inflater.inflate(R.layout.fragment_3, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = (Button) view.findViewById(R.id.share);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                File imagePath = new File(getContext().getCacheDir(), "images");
                File newFile = new File(imagePath, "image.png");
                Uri contentUri = FileProvider.getUriForFile(getContext(), "webfreak.si.daysofmylife2.fileprovider", newFile);

                if (contentUri != null) {

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, getContext().getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                }

            }
        });
    }
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);

        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.banner_device_id))
                .build();
        AdView mAdView = (AdView) getView().findViewById(R.id.adView);
        mAdView.loadAd(request);
    }
}

