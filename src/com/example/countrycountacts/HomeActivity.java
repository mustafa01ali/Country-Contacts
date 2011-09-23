/**
 * 
 */
package com.example.countrycountacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Home Activity
 * @author mustafa.ali
 */
public class HomeActivity extends Activity {

    private Spinner mSpinner;
    private ListView mContactsList;
    private String[] mCodes;
    private ArrayList<String> mContactNames;
    private ArrayList<Contact> mContacts;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        init();
    }

    /**
     * Initializes the views, fields
     */
    private void init(){
        mSpinner = (Spinner) findViewById(R.id.spinner1);
        mContactsList = (ListView) findViewById(R.id.listView1);
        
        mContacts = new ArrayList<Contact>();
        mContactNames = new ArrayList<String>();
        
        mCodes = getResources().getStringArray(R.array.codes);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.countries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new CountrySelectedListener());

        mContactsList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Intent intent = new Intent(HomeActivity.this, ContactDetailsActivity.class);
                intent.putExtra("contact", mContacts.get(position));
                startActivity(intent);
            }
        });

    }

    /**
     * Class to provide a callback method that will notify when an item has been selected from the Spinner
     * 
     * @author mustafa.ali
     *
     */
    public class CountrySelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                View view, int pos, long id) {
            
            mContactNames.clear();
            mContacts.clear();
            getContactsByCountryCode(mCodes[pos]);
            setArrayAdapterToListView();
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }

    /**
     * Fetches contacts from the phone DB as per the country code
     * @param code Country + prefix code
     */
    private void getContactsByCountryCode(String code){
        
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    Contact contact = new Contact(name);
                    

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
                            null, 
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
                            new String[]{id}, null);
                    
                    int phoneColumnIndex = pCur.getColumnIndex(Phone.NUMBER);
                    String phoneNumber = "";
                    
                    while (pCur.moveToNext()) {
                        
                        phoneNumber = pCur.getString(phoneColumnIndex);
                        
                        if (phoneNumber.startsWith(code)) {
                            contact.addPhoneNumber(pCur.getString(phoneColumnIndex));    
                        }
                        
                    } 
                    
                    if (contact.hasPhoneNumbers()) {
                        mContactNames.add(name);
                        mContacts.add(contact);                        
                    }
                    
                    pCur.close();
                }
            }
        }
        cur.close();
    }

    /**
     * Sets the cursor adapter to the list view
     * @param c Database cursor
     */
    private void setArrayAdapterToListView(){
        mContactsList.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, mContactNames.toArray()));
        //mContactsList.setAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, new String[] { Contacts.DISPLAY_NAME }, new int[] { android.R.id.text1 }));
    }

}