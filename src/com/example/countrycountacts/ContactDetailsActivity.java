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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author mustafa.ali
 */
public class ContactDetailsActivity extends Activity {

    private Contact mContact;
    private ArrayList<String> mPhoneNumbers;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_details);

        displayDetails();
    }

    /**
     * Displays the contact's details
     */
    private void displayDetails() {
        Bundle b = getIntent().getExtras();
        mContact =
                b.getParcelable("contact");

        TextView nameTextView = (TextView) findViewById(R.id.name);
        nameTextView.setText(mContact.getName());

        ListView lv = (ListView) findViewById(R.id.numbers_list);
        mPhoneNumbers = mContact.getPhoneNumbers();

        lv.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1,
                mPhoneNumbers.toArray()));
        // lv.setAdapter(new ArrayAdapter<Object>(this, R.layout.phone_field, R.id.number, phoneNumbers.toArray()));

        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                startActivity(new Intent(Intent.ACTION_CALL,
                        Uri.parse("tel:" + mPhoneNumbers.get(position))));
            }
        });
    }

    /**
     * Creates the options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.NONE, 0, "Edit").setIcon(android.R.drawable.ic_menu_edit);
        return true;
    }

    /**
     * Handles the options menu clicks
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getTitle().equals("Edit")) {
            Intent i = new Intent(Intent.ACTION_EDIT);
            i.setData(Uri.parse(ContactsContract.Contacts.CONTENT_LOOKUP_URI + "/"
                    + mContact.getId()));
            startActivity(i);
        }
        return true;
    }
}
