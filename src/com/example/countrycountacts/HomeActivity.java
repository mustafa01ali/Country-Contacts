/**
 * Copyright (C) 2011, Mir Mustafa Ali (mustafa01ali@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.example.countrycountacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Home Activity
 * 
 * @author mustafa.ali
 */
public class HomeActivity extends Activity {

    private Spinner mSpinner;
    private ListView mContactsList;
    private String[] mCodes;
    // private ArrayList<String> mContactNames;
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
    private void init() {
        mSpinner = (Spinner) findViewById(R.id.spinner1);
        mContactsList = (ListView) findViewById(R.id.listView1);
        mContactsList.setFastScrollEnabled(true);

        mContacts = new ArrayList<Contact>();
        // mContactNames = new ArrayList<String>();

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
     */
    public class CountrySelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                View view, int pos, long id) {

            // mContactNames.clear();
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
     * 
     * @param code Country + prefix code
     */
    private void getContactsByCountryCode(String code) {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME); // Fetching contacts and applying sort by DISPLAY_NAME

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    Contact contact = new Contact(name);
                    contact.setId(id);
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[] {
                                id
                            }, null);

                    int phoneColumnIndex = pCur.getColumnIndex(Phone.NUMBER);
                    String phoneNumber = "";

                    while (pCur.moveToNext()) {

                        phoneNumber = pCur.getString(phoneColumnIndex);

                        if (phoneNumber.startsWith(code)) {
                            contact.addPhoneNumber(pCur.getString(phoneColumnIndex));
                        }

                    }

                    if (contact.hasPhoneNumbers()) {
                        // mContactNames.add(name);
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
     * 
     * @param c Database cursor
     */
    private void setArrayAdapterToListView() {

        mContactsList.setAdapter(new ContactsListAdaptor(this, mContacts));

        // mContactsList.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, mContactNames.toArray()));

        // mContactsList.setAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, new String[] { Contacts.DISPLAY_NAME }, new int[] { android.R.id.text1 }));
    }

    /**
     * Custom List Adapter for Contacts
     */
    class ContactsListAdaptor extends ArrayAdapter<Contact> implements SectionIndexer {

        HashMap<String, Integer> alphaIndexer;
        String[] sections;

        public ContactsListAdaptor(Context context, ArrayList<Contact> items) {
            super(context, android.R.layout.simple_list_item_1, items);

            alphaIndexer = new HashMap<String, Integer>();
            int size = items.size();

            for (int x = 0; x < size; x++) {
                Contact s = items.get(x);

                // get the first letter of the store
                String ch = s.getName().substring(0, 1);
                // convert to uppercase otherwise lowercase a -z will be sorted after upper A-Z
                ch = ch.toUpperCase();

                // HashMap will prevent duplicates
                alphaIndexer.put(ch, x);
            }

            Set<String> sectionLetters = alphaIndexer.keySet();

            // create a list from the set to sort
            ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);

            Collections.sort(sectionList);

            sections = new String[sectionList.size()];

            sectionList.toArray(sections);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_1, null);
            }
            Contact c = mContacts.get(position);
            if (c != null) {
                TextView name = (TextView) v.findViewById(android.R.id.text1);
                if (name != null) {
                    name.setText(c.getName());
                }
            }
            return v;
        }

        public int getPositionForSection(int section) {
            return alphaIndexer.get(sections[section]);
        }

        public int getSectionForPosition(int position) {
            return 1;
        }

        public Object[] getSections() {
            return sections;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.NONE, 0, "Add New").setIcon(android.R.drawable.ic_input_add);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getTitle().equals("Add New")) {
            Intent i = new Intent(ContactsContract.Intents.Insert.ACTION);
            i.setData(ContactsContract.Contacts.CONTENT_URI);
            startActivity(i);
        }
        return true;
    }
}
