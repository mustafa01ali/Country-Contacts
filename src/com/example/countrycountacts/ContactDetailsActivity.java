/**
 * 
 */
package com.example.countrycountacts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;

/**
 * @author mustafa.ali
 *
 */
public class ContactDetailsActivity extends Activity {
    
    private ArrayList<String> mPhoneNumbers;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_details);
        
        displayDetails();
    }
    
    private void displayDetails(){
        Bundle b = getIntent().getExtras();
        Contact contact =
            b.getParcelable("contact");
     
        TextView nameTextView = (TextView) findViewById(R.id.name);
        nameTextView.setText(contact.getName());
        
        ListView lv = (ListView) findViewById(R.id.numbers_list);
        mPhoneNumbers = contact.getPhoneNumbers();
        //System.out.println("Numbers: " + mPhoneNumbers.size());
        
        lv.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, mPhoneNumbers.toArray()));
        //lv.setAdapter(new ArrayAdapter<Object>(this, R.layout.phone_field, R.id.number, phoneNumbers.toArray()));
        
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                startActivity(new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + mPhoneNumbers.get(position))));
            }
        });
    }
}
