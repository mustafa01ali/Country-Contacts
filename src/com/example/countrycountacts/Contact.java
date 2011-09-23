/**
 * 
 */
package com.example.countrycountacts;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @author mustafa.ali
 *
 */
public class Contact implements Parcelable{

    private String name;
    private ArrayList<String> phoneNumbers; 

    /**
     * Default Constructor
     */
    public Contact(){
        name = "";
        phoneNumbers = new ArrayList<String>();
    }
    
    /**
     * Constructor
     * @param name  Contact name
     */
    public Contact(String name){
        this.setName(name);
        phoneNumbers = new ArrayList<String>();
    }

    /**
     *
     * Constructor to use when re-constructing object
     * from a parcel
     *
     * @param in a parcel from which to read this object
     */
    public Contact(Parcel in) {
        this();
        name = in.readString();
        in.readStringList(phoneNumbers);
    }

    /**
     * Adds a phone number to the contact
     * @param number    phone number
     */
    public void addPhoneNumber(String number){
        this.phoneNumbers.add(number);
    }

    /**
     * 
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the object to the parcel to be passed between activities
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(phoneNumbers);
    }

    /**
     * Setter for name
     * @param name Contact name
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * Getter for name field
     * @return Contact name
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Getter for phone numbers
     * @return Contact's phone numbers
     */
    public ArrayList<String> getPhoneNumbers(){
        return this.phoneNumbers;
    }
    
    /**
     * Checks if the contact has any phone numbers
     * @return true if the contacts has any phone numbers, false otherwise
     */
    public boolean hasPhoneNumbers(){
        if (phoneNumbers.size() > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * CREATOR to re-instantiate the object from the bundle
     */
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

        public Contact createFromParcel(Parcel source) {
           return new Contact(source);
        }

        public Contact[] newArray(int size) {
           return new Contact[size];
        }

     };

}
