package webfreak.si.daysofmylife2;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener
{
    Thread t;
    WebView w;
    public ListView mListView;
    public InterstitialAd mInterstitialAd;
    String webviewData;
    String celebrity;
    String celebrity_url_ready;
    public static List<Celebrity> androidList;
    final int toleranceInterval = (int)TimeUnit.HOURS.toSeconds(2);
    //final int periodicity = (int)TimeUnit.HOURS.toSeconds(12);
    final int periodicity = (int)TimeUnit.HOURS.toSeconds(3);

    private List<HashMap<String, String>> mAndroidMapList = new ArrayList<>();
    private static final String KEY_NAME = "name";
    private static final String KEY_DAYSALIVE = "daysalive";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("My data"));
        tabLayout.addTab(tabLayout.newTab().setText("Outlived"));
        tabLayout.addTab(tabLayout.newTab().setText("Social"));


        Intent widgetUpdate = new Intent(this, WidgetProvider.class);
        widgetUpdate.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(widgetUpdate);

        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_app_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id_inter));

        mInterstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                Random rr =new Random();
                if(rr.nextInt(100) < 30)
                {
                    mInterstitialAd.show();
                }
            }
        });


        requestNewInterstitial();

        FloatingActionButton myFab = (FloatingActionButton)  findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent intent = new Intent(getBaseContext(), PersonEditor.class);
                //startActivity(intent);
            }
        });
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mListView = (ListView) findViewById(R.id.outlived_list);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
        t = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    while (!isInterrupted())
                    {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    TextView seconds = (TextView) findViewById(R.id.secondsAliveValue);
                                    int second = parseInt(seconds.getText().toString().replace(".", ""));
                                    second = second + 1;
                                    seconds.setText(String.format(Locale.GERMANY, "%,d", second));

                                }
                                catch (Exception ex)
                                {
                                    return;
                                }
                            }
                        });
                    }
                }
                catch (InterruptedException e)
                {
                }
            }
        };

        t.start();
        Intent intent = getIntent();
        String share = intent.getAction();

    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.banner_device_id))
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Toast.makeText(this, mAndroidMapList.get(i).get(KEY_NAME),Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Nothing here yet.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause()
    {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(1 == Utils.getPrefInt("REFRESH_MAIN",getApplicationContext()))
        {
            Utils.putPrefInt("REFRESH_MAIN", 0, getApplicationContext());
            finish();
            startActivity(getIntent());
        }
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }
    public void loadListView() {

        ListAdapter adapter = new SimpleAdapter(MainActivity.this, mAndroidMapList, R.layout.list_item, new String[] {  KEY_NAME, KEY_DAYSALIVE }, new int[] { R.id.name, R.id.api })
        {
            @Override
            public View getView (int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView) v.findViewById(R.id.api);
                int green_color = ContextCompat.getColor(getApplicationContext(), R.color.cool_green);
                if(tv.getText().toString().startsWith("Outlived"))
                {
                    tv.setTextColor(green_color);
                }
                else
                {
                    tv.setTextColor(Color.RED);
                }

                return v;
            }
        };
        final ListView mListView = (ListView) findViewById(R.id.outlived_list);
        if(mListView !=null)
        {
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Object o = mListView.getItemAtPosition(position);
                    celebrity = ((HashMap) o).get("name").toString().trim();
                    celebrity_url_ready = celebrity.replace(" ","+").replace("(","").replace(")","").replace("'","");
                    AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
                    d.setTitle(celebrity);
                    w = new WebView(getApplicationContext());
                    w.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                    InputStream is = null;
                    try
                    {
                        is = getAssets().open("celebrity_info.html");
                        int size = is.available();

                        byte[] buffer = new byte[size];
                        is.read(buffer);
                        is.close();
                        String str = new String(buffer);
                        webviewData = str;
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    w.loadData(webviewData,"text/html; charset=UTF-8", null);
                    d.setView(w).show();
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try
                            {
                                Document doc;
                                Document doc2;
                                try
                                {
                                    doc = Jsoup.connect("http://www.bing.com/images/search?q=" + celebrity_url_ready).get();
                                    doc2 = Jsoup.connect("https://www.bing.com/search?q=" + celebrity_url_ready).get();
                                    InputStream is = getAssets().open("celebrity_info.html");
                                    int size = is.available();

                                    byte[] buffer = new byte[size];
                                    is.read(buffer);
                                    is.close();
                                    Elements pngs = doc.select("img[src^=http]");
                                    Elements description = doc2.select("div.b_snippet");
                                    Elements links = doc2.select("li.b_algo");
                                    Element link = links.select("h2 a").get(0);

                                    Element image = null;
                                    Element desc = null;
                                    String more = "";
                                    String str = new String(buffer);
                                    if(pngs.size() > 0)
                                    {
                                        image = pngs.get(0);
                                        String imgUrl = image.attr("src");
                                        str = str.replace("loader.png",imgUrl);
                                    }
                                    if(description.size() > 0)
                                    {
                                        desc = description.get(0);
                                        String celebDesc = desc.html();
                                        str += celebDesc;
                                    }
                                    if(link.attr("href").length() > 0)
                                    {
                                        more = link.attr("href");
                                        str = str.replace(" ..."," ...<a href=\""+more+"\"> more</a>");
                                    }
                                    webviewData = str;

                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                w.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        w.loadData(webviewData,"text/html; charset=UTF-8", null);
                                    }
                                });

                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                        }
                    });

                    thread.start();

                }
            });
        }
    }
}
