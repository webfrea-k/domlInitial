package webfreak.si.daysofmylife2;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by simon.hocevar on 26.03.2017.
 */

public class DailyJob extends JobService implements LoadJSONTask.Listener{
    @Override
    public boolean onStartJob(JobParameters job) {
        // Do some work here
        long user_birthday_long = Utils.getPrefLong("USER_BIRTHDAY",getApplicationContext());
        if(user_birthday_long>0)
        {
            DateTime dt = new DateTime();
            if(Utils.getPrefInt("JOB_UPDATE",getApplicationContext()) != dt.getDayOfMonth())
            {
                new LoadJSONTask(this).execute();
                Utils.putPrefInt("JOB_UPDATE",dt.getDayOfMonth(), getApplicationContext());
            }
        }
        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }

    @Override
    public void onLoaded(List<Celebrity> androidList)
    {
        long user_birthday_long = Utils.getPrefLong("USER_BIRTHDAY",getApplicationContext());
        DateTime current_date = new DateTime();
        current_date = current_date.withHourOfDay(0).withMinuteOfHour(1);
        long millisalive = current_date.getMillis() - user_birthday_long;
        int daysAlive = (int) TimeUnit.MILLISECONDS.toDays(millisalive);
        ArrayList<String> list = new ArrayList<String>();
        TinyDB db = new TinyDB(getApplicationContext());
        if(db.getListString("PEOPLES").size() > 0)
        {
            list = db.getListString("PEOPLES");
        }
        list.add("You");
        boolean user_outlived_al_least_someone = false;
        boolean friend_outlived_at_least_someone = false;


        for (Celebrity celebrity : androidList)
        {
            int celebrity_days_alive =Integer.parseInt(celebrity.getDaysAlive());
            for(String item : list)
            {
                if(item.equals("You"))
                {
                    if(daysAlive == celebrity_days_alive)
                    {
                        Utils.showNotification(getApplicationContext(),"You",celebrity.getName().trim());
                        Utils.putPref("WHOS_NEXT", "You have just outlived "+celebrity.getName().trim(), getApplicationContext());
                        user_outlived_al_least_someone  = true;
                    }
                }
                else
                {
                    long other_user_birthday_long = Utils.getPrefLong(item+"_BIRTHDAY_MILLIS", getApplicationContext());
                    long other_millisalive = current_date.getMillis() - other_user_birthday_long;
                    int other_daysAlive = (int) TimeUnit.MILLISECONDS.toDays(other_millisalive);
                    if(other_daysAlive == celebrity_days_alive)
                    {
                        Utils.showNotification(getApplicationContext(),item,celebrity.getName().trim());
                        Utils.putPref("WHOS_NEXT_"+item, item+" has just outlived "+celebrity.getName().trim(), getApplicationContext());
                    }
                }
            }
        }
    }


    @Override
    public void onError()
    {

    }
}


//Utils.showNotification(getApplicationContext(),"SIMA","Michael Jackson");