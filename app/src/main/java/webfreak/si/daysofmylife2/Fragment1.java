package webfreak.si.daysofmylife2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static webfreak.si.daysofmylife2.R.id.card_view_spinner;

/**
 * Created by simon.hocevar on 15.03.2017.
 */

public class Fragment1 extends Fragment implements LoadJSONTask.Listener{
    View rootView;
    TextView seconds;
    WebView w;
    String webviewData;
    String celebrity;
    public static List<Celebrity> androidList;
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    TextView nextToOutlive;
    String celebrity_url_ready;
    DateTime formal_date = new DateTime();
    boolean allTaskFinished = false;
    boolean shouldRestartApp= false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_1, container, false);
        final EditText edt = (EditText) rootView.findViewById(R.id.datetext);
        seconds = (TextView)rootView.findViewById(R.id.secondsAliveValue);
        final TextView birthdayText = (TextView)rootView.findViewById(R.id.textView);
        final TextView minutes = (TextView)rootView.findViewById(R.id.minutesAliveValue);
        final TextView hours = (TextView)rootView.findViewById(R.id.hoursAliveValue);
        final TextView days = (TextView)rootView.findViewById(R.id.daysAliveValue);
        final TextView months = (TextView)rootView.findViewById(R.id.monthsAliveValue);
        final TextView years = (TextView)rootView.findViewById(R.id.yearsAliveValue);
        long user_birthday_long = Utils.getPrefLong("USER_BIRTHDAY",getContext());
        final DateTime user_birthday = new DateTime(user_birthday_long);
        final DateTime current_date = new DateTime().withHourOfDay(0).withMinuteOfHour(1);
        final Spinner friendsSpinner = (Spinner) rootView.findViewById(R.id.spinnerFriends);
        final CardView friendsCard = (CardView) rootView.findViewById(card_view_spinner);
        nextToOutlive = (TextView) rootView.findViewById(R.id.nexttoOutliveText);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
        friendsSpinner.setAdapter(adapter);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TinyDB db = new TinyDB(getContext());
        nextToOutlive.setText(Utils.getPref("WHOS_NEXT",getContext()));
        if(db.getListString("PEOPLES").size() > 0)
        {
            friendsCard.setVisibility(View.VISIBLE);
            list = db.getListString("PEOPLES");
            list.add(0,"You");
            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter.notifyDataSetChanged();
            friendsSpinner.setAdapter(adapter);

        }
        friendsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0)
                {
                    EditText birthdayDate = (EditText) rootView.findViewById(R.id.datetext);
                    String newText = friendsSpinner.getSelectedItem().toString() + "r birthday";
                    birthdayText.setText(newText);
                    birthdayDate.setEnabled(true);
                    birthdayDate.setTextColor(Color.BLACK);
                    long user_birthday_long = Utils.getPrefLong("USER_BIRTHDAY",getContext());
                    DateTime dt = new DateTime(user_birthday_long);
                    birthdayDate.setText(dt.toString("d.M.yyyy"));
                    long diff = current_date.getMillis()- dt.getMillis();

                    seconds.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toSeconds(diff))));
                    minutes.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toMinutes(diff))));
                    hours.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toHours(diff))));
                    days.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toDays(diff))));
                    months.setText(String.format(Locale.GERMANY,"%,d", Months.monthsBetween(dt,current_date).getMonths()));
                    years.setText(String.format(Locale.GERMANY,"%,d",Math.abs(Years.yearsBetween(dt,current_date).getYears())));
                    if(Utils.getPref("WHOS_NEXT",getContext()).length()>0)
                    {
                        TextView nextToOutlive = (TextView) rootView.findViewById(R.id.nexttoOutliveText);
                        nextToOutlive.setText(Utils.getPref("WHOS_NEXT",getContext()));

                    }
                }
                else
                {
                    String newText = friendsSpinner.getSelectedItem().toString() + "'s birthday";
                    birthdayText.setText(newText);
                    String userKey = friendsSpinner.getSelectedItem().toString() + "_BIRTHDAY_MILLIS";
                    long millis = Utils.getPrefLong(userKey, getContext());
                    EditText birthdayDate = (EditText) rootView.findViewById(R.id.datetext);
                    if (millis > 0)
                    {
                        long diff = current_date.getMillis() - millis;
                        seconds.setText(String.format(Locale.GERMANY, "%,d", Math.abs((int) TimeUnit.MILLISECONDS.toSeconds(diff))));
                        DateTime dt = new DateTime(millis);
                        birthdayDate.setText(dt.toString("d.M.yyyy"));
                        birthdayDate.setEnabled(false);
                        birthdayDate.setTextColor(Color.GRAY);
                        seconds.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toSeconds(diff))));
                        minutes.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toMinutes(diff))));
                        hours.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toHours(diff))));
                        days.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toDays(diff))));
                        months.setText(String.format(Locale.GERMANY,"%,d", Months.monthsBetween(dt,current_date).getMonths()));
                        years.setText(String.format(Locale.GERMANY,"%,d",Math.abs(Years.yearsBetween(dt,current_date).getYears())));
                    } else
                    {
                        birthdayDate.setText("");
                    }
                    if(Utils.getPref("WHOS_NEXT_"+friendsSpinner.getSelectedItem().toString(),getContext()).length()>0)
                    {
                        TextView nextToOutlive = (TextView) rootView.findViewById(R.id.nexttoOutliveText);
                        nextToOutlive.setText(Utils.getPref("WHOS_NEXT_"+friendsSpinner.getSelectedItem().toString(),getContext()));

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        if(user_birthday_long > 0)
        {
            edt.setText(user_birthday.toString("d.M.yyyy"));
            formal_date = user_birthday;
            long diff = current_date.getMillis() - user_birthday.getMillis();
            DateTime birthday = new DateTime(user_birthday.getMillis());
            seconds.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toSeconds(diff))));
            minutes.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toMinutes(diff))));
            hours.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toHours(diff))));
            days.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toDays(diff))));
            months.setText(String.format(Locale.GERMANY,"%,d", Months.monthsBetween(birthday,current_date).getMonths()));
            years.setText(String.format(Locale.GERMANY,"%,d",Math.abs(Years.yearsBetween(birthday,current_date).getYears())));
        }
        edt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                final DatePickerDialog mDatePicker=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
                {
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
                {
                    // TODO Auto-generated method stub
                    DateTime dt = new DateTime();
                    new LoadJSONTask(Fragment1.this).execute();
                    dt = dt.withDate(selectedyear,selectedmonth+1,selectedday);
                    Utils.putPrefLong("USER_BIRTHDAY", dt.getMillis(), getContext());
                    edt.setText(dt.toString("d.M.yyyy"));
                    long diff = current_date.getMillis()- dt.getMillis();
                    seconds.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toSeconds(diff))));
                    minutes.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toMinutes(diff))));
                    hours.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toHours(diff))));
                    days.setText(String.format(Locale.GERMANY,"%,d",Math.abs((int) TimeUnit.MILLISECONDS.toDays(diff))));
                    months.setText(String.format(Locale.GERMANY,"%,d", Math.abs((Months.monthsBetween(dt, current_date).getMonths()))));
                    years.setText(String.format(Locale.GERMANY,"%,d",Math.abs(Years.yearsBetween(dt, current_date).getYears())));
                    if(androidList != null && androidList.size()>0)
                    {
                        Utils.getNextPersonToOutliveSelf(androidList, getContext());
                        nextToOutlive.setText(Utils.getPref("WHOS_NEXT",getContext()));

                    }
                    if(allTaskFinished)
                    {
                        getActivity().finish();
                        getActivity().overridePendingTransition(0, 0);
                        startActivity(getActivity().getIntent());
                        getActivity().overridePendingTransition(0, 0);
                    }
                    if(!allTaskFinished)
                    {
                        shouldRestartApp = true;
                    }
                }
            },formal_date.getYear(), formal_date.getMonthOfYear()-1, formal_date.getDayOfMonth());
                mDatePicker.setTitle("Set your birthday");
                mDatePicker.show();
            }

        });

        nextToOutlive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String completeText = nextToOutlive.getText().toString();
                String next ="";
                if(completeText.contains("just outlived"))
                {
                    completeText = completeText.replace(" just outlived ","/");
                    next = completeText.split("/")[1].trim();
                }
                else
                {
                    next = nextToOutlive.getText().toString().split(",")[0].trim();
                }

                celebrity = next.trim();
                celebrity_url_ready = next.replace(" ","+").replace("(","").replace(")","").replace("'","");
                AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
                d.setTitle(celebrity);
                w = new WebView(getContext());
                w.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
                InputStream is = null;
                try
                {
                    is = getActivity().getAssets().open("celebrity_info.html");
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
                                InputStream is = getActivity().getAssets().open("celebrity_info.html");
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
        return rootView;
    }

    @Override
    public void onLoaded(List<Celebrity> andridListWeb) {
        androidList = andridListWeb;
        Utils.getNextPersonToOutliveSelf(androidList, getContext());
        nextToOutlive.setText(Utils.getPref("WHOS_NEXT",getContext()));
        Utils.getNextPersonsToOutliveOthers(androidList,getContext());
        allTaskFinished = true;
        if(shouldRestartApp)
        {
            getActivity().finish();
            getActivity().overridePendingTransition(0, 0);
            startActivity(getActivity().getIntent());
            getActivity().overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onError()
    {

    }
}