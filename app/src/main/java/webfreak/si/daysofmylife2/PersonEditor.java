package webfreak.si.daysofmylife2;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import static webfreak.si.daysofmylife2.R.id.txtItemPerson;

/**
 * Created by simon.hocevar on 21.03.2017.
 */

public class PersonEditor extends PreferenceActivity
{
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Spinner spinner;
    TextView birthdayText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_editor);
        Utils.putPrefInt("REFRESH_MAIN", 1, getApplicationContext());
        /** Defining the ArrayAdapter to set items to Spinner Widget */
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        birthdayText = (TextView)findViewById(R.id.textView);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TinyDB db = new TinyDB(getApplicationContext());
        if(db.getListString("PEOPLES").size() > 0)
        {
            list = db.getListString("PEOPLES");
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter.notifyDataSetChanged();
            spinner.setAdapter(adapter);

        }

        /** Defining click event listener for the button */
        View.OnClickListener addListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText txtItem = (EditText) findViewById(txtItemPerson);
                list.add(txtItem.getText().toString());
                txtItem.setText("");
                adapter.notifyDataSetChanged();
            }
        };
        /** Defining click event listener for the button */
        View.OnClickListener saveListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(spinner.getCount()>0)
                {
                    TinyDB db = new TinyDB(getApplicationContext());
                    db.putListString("PEOPLES",list);
                    String userKey = spinner.getSelectedItem().toString()+"_BIRTHDAY_MILLIS";
                    EditText birthdayDate = (EditText)findViewById(R.id.datetext);
                    try
                    {
                        DateTimeFormatter format = DateTimeFormat.forPattern("d.M.yyyy");
                        DateTime dateTime = DateTime.parse(birthdayDate.getText().toString(), format);
                        Utils.putPrefLong(userKey, dateTime.getMillis(), getApplicationContext());
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Saved.", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                    catch (Exception ex)
                    {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Incorrect date format. Use d.m.yyyy", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Add at least one friend.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }


            }
        };
        /** Defining click event listener for the button */
        View.OnClickListener deleteListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText birthdayDate = (EditText)findViewById(R.id.datetext);
                try
                {
                    birthdayDate.setText("");
                    String selected = spinner.getSelectedItem().toString();
                    list.remove(selected);
                    adapter.notifyDataSetChanged();
                    TinyDB db = new TinyDB(getApplicationContext());
                    db.putListString("PEOPLES",list);

                }
                catch (Exception ex)
                {

                }
            }
        };

        /** Getting a reference to button object of the resource activity_main */
        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        Button btnSave = (Button) findViewById(R.id.savePerson);
        Button btnDelete= (Button) findViewById(R.id.deletePerson);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //String newText = spinner.getSelectedItem().toString() +"'s birthday:";
                //birthdayText.setText(newText);
                String userKey = spinner.getSelectedItem().toString()+"_BIRTHDAY_MILLIS";
                long millis = Utils.getPrefLong(userKey,getApplicationContext());
                EditText birthdayDate = (EditText)findViewById(R.id.datetext);
                if(millis>0)
                {
                    DateTime dt = new DateTime(millis);
                    birthdayDate.setText(dt.toString("d.M.yyyy"));
                }
                else
                {
                    birthdayDate.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        /** Setting click listener for the button */
        btnAdd.setOnClickListener(addListener);
        btnSave.setOnClickListener(saveListener);
        btnDelete.setOnClickListener(deleteListener);

    }
}