/*
 * =| ADAPTIVE RUNTIME PLATFORM |=======================================================================================
 *
 * (C) Copyright 2013-2014 Carlos Lozano Diez t/a Adaptive.me <http://adaptive.me>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Original author:
 *
 *     * Carlos Lozano Diez
 *                 <http://github.com/carloslozano>
 *                 <http://twitter.com/adaptivecoder>
 *                 <mailto:carlos@adaptive.me>
 *
 * Contributors:
 *
 *     * Francisco Javier Martin Bueno
 *             <https://github.com/kechis>
 *             <mailto:kechis@gmail.com>
 *
 * =====================================================================================================================
 */

package me.adaptive.arp.impl.pim;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.util.Log;
import me.adaptive.arp.api.*;
import me.adaptive.arp.impl.ServiceLocator;

import java.util.*;


public class ContactImpl implements IContact {

    private static final Map<Integer, ContactPhone.PhoneType> PHONE_TYPES_MAP = new HashMap<Integer, ContactPhone.PhoneType>();
    private static final Map<Integer, ContactAddress.AddressType> ADDRESS_TYPES_MAP = new HashMap<Integer, ContactAddress.AddressType>();

    static {
        // Phone
        PHONE_TYPES_MAP.put(Phone.TYPE_FAX_HOME, ContactPhone.PhoneType.HomeFax);
        PHONE_TYPES_MAP.put(Phone.TYPE_MOBILE, ContactPhone.PhoneType.Mobile);
        PHONE_TYPES_MAP.put(Phone.TYPE_WORK, ContactPhone.PhoneType.Work);
        PHONE_TYPES_MAP.put(Phone.TYPE_FAX_WORK, ContactPhone.PhoneType.WorkFax);
        PHONE_TYPES_MAP.put(Phone.TYPE_COMPANY_MAIN, ContactPhone.PhoneType.Work);
        PHONE_TYPES_MAP.put(Phone.TYPE_WORK_MOBILE, ContactPhone.PhoneType.Work);
        PHONE_TYPES_MAP.put(Phone.TYPE_CUSTOM, ContactPhone.PhoneType.Other);

        // Address
        ADDRESS_TYPES_MAP.put(StructuredPostal.TYPE_HOME,
                ContactAddress.AddressType.Home);
        ADDRESS_TYPES_MAP.put(StructuredPostal.TYPE_WORK, ContactAddress.AddressType.Work);
        ADDRESS_TYPES_MAP.put(StructuredPostal.TYPE_OTHER,
                ContactAddress.AddressType.Other);

    }

    @Override
    public void searchContacts(String term, IContactResultCallback callback) {
        Uri uri = null;
        Context context = ServiceLocator.getMainActivity().getApplicationContext();
        ContentResolver cr = context.getContentResolver();
        Cursor contactsCursor = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";


        if (term != null) {
            Log.d("ARP TEST", "ARP: term " + term);
            //LOGGER_CONTACTS.logInfo("ListContacts", "Start listing contacts... queryText " + query.toString());
            String selection = null;
            String[] selectionValues = null;

            //LOGGER_CONTACTS.logInfo("Value", term);

            selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
            /*switch (query.getCondition()) {
                case Equals:
                    selectionValues = new String[]{term};
                    break;
                case StartsWith:
                    selectionValues = new String[]{term + "%"};
                    break;
                case EndsWith:
                    selectionValues = new String[]{"%" + term};
                    break;
                case Contains:*/
            selectionValues = new String[]{"%" + term + "%"};
                    /*break;
            }*/


            uri = ContactsContract.Contacts.CONTENT_URI;
            //LOGGER_CONTACTS.logDebug("Contacts selection", selection + ", selection values: " + (selectionValues != null ? selectionValues.toString() : "null"));

            Date start = new Date();
            contactsCursor = cr.query(uri, new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME}, selection, selectionValues, sortOrder);
            //LOGGER_CONTACTS.logDebug("Time lapsed (main query A)", (new Date().getTime() - start.getTime()) + " ms");

        } else {
            Log.d("ARP TEST", "ARP: term null");
            //LOGGER_CONTACTS.logInfo("ListContacts", "Start listing contacts... without query");
            Date start = new Date();
            uri = ContactsContract.Contacts.CONTENT_URI;
            contactsCursor = cr.query(uri, new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME}, null, null, sortOrder);
            //LOGGER_CONTACTS.logDebug("Time lapsed (main query B)", (new Date().getTime() - start.getTime()) + " ms");

        }

        List<Contact> contactList = new ArrayList<Contact>();
        Log.d("ARP TEST", "ARP: init try");
        try {
            // iterate cursor to
            contactsCursor.moveToFirst();
            int excludedContacts = 0;
            Date startLoop = new Date();
            Map<String, Contact> contactsIDs = new HashMap<String, Contact>();
            String contactsIDsString = "";
            while (!contactsCursor.isAfterLast()) {


                Long id = contactsCursor.getLong(0);
                String displayName = contactsCursor.getString(1);
                Log.d("ARP TEST", "ARP: displayName = " + displayName);
                Contact contact = new Contact(id.toString());
                ContactPersonalInfo pI = new ContactPersonalInfo();
                pI.setName(displayName);
                contact.setPersonalInfo(pI);

                contactsIDs.put("" + id, contact);
                contactsIDsString += (contactsIDsString.length() > 0 ? "," : "") + "'" + id + "'";
                contactsCursor.moveToNext();
            }
            //LOGGER_CONTACTS.logDebug("Time lapsed (looping contacts)", (new Date().getTime() - startLoop.getTime()) + " ms");

            // querying the content resolver for all contacts

            String selectionMultiple = ContactsContract.RawContactsEntity.CONTACT_ID + " IN (" + contactsIDsString + ") ";
            Date startRawQuery = new Date();
            Cursor raws = cr.query(ContactsContract.RawContactsEntity.CONTENT_URI, null,
                    selectionMultiple, null, null);
            //LOGGER_CONTACTS.logDebug("Time lapsed (query raw data for all contacts matched)", (new Date().getTime() - startRawQuery.getTime()) + " ms");

            Date startLoopingRawData = new Date();
            try {
                raws.moveToFirst();

                // it seems that there is a time saving by querying the indexes before the cursor loop
                int columnIndex_CONTACTID = raws.getColumnIndex(ContactsContract.RawContactsEntity.CONTACT_ID);
                int columnIndex_MIMETYPE = raws.getColumnIndex(ContactsContract.Data.MIMETYPE);
                int columnIndex_PHONE_NUMBER = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int columnIndex_IS_PRIMARY = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY);
                int columnIndex_PHONE_TYPE = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                int columnIndex_EMAIL_ADDRESS = raws.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1);
                int columnIndex_DISPLAY_NAME = raws.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME);
                int columnIndex_GROUP_MEMBERSHIP = raws.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.DATA1);

                while (!raws.isAfterLast()) {

                    String contactId = raws.getString(columnIndex_CONTACTID);

                    String firstname = null, group = null, lastname = null, name = null;
                    List<ContactEmail> emailList = new ArrayList<ContactEmail>();
                    List<ContactPhone> phoneList = new ArrayList<ContactPhone>();
                    Contact contact = null;

                    if (contactsIDs != null && contactsIDs.containsKey(contactId)) {
                        contact = contactsIDs.get(contactId);
                        // asList returns unmodifiable lists, so we need to create new lists from them
                        if (contact.getContactEmails() != null)
                            emailList.addAll(Arrays.asList(contact.getContactEmails()));
                        if (contact.getContactPhones() != null)
                            phoneList.addAll(Arrays.asList(contact.getContactPhones()));
                    }

                    String type = raws.getString(columnIndex_MIMETYPE);
                    if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(type)) {
                        // structured name
                        firstname = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                        lastname = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                        //displayName = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                    } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(type)) {
                        // it's a phone, gets all properties and add in
                        // the phone list
                        ContactPhone phone = new ContactPhone();
                        String number = raws.getString(columnIndex_PHONE_NUMBER);
                        phone.setPhone(number);
                        int primary = raws.getInt(columnIndex_IS_PRIMARY);
                        //phone.setIsPrimary(primary != 0);
                        int phonetype = raws.getInt(columnIndex_PHONE_TYPE);
                        phone.setPhoneType(getNumberType(phonetype));
                        phoneList.add(phone);
                    } else if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(type)) {
                        // it's an Email address, gets all properties
                        // and add in the email list
                        ContactEmail email = new ContactEmail();
                        String emailaddress = raws.getString(columnIndex_EMAIL_ADDRESS);
                        email.setEmail(emailaddress);
                        //email.setCommonName(raws.getString(columnIndex_DISPLAY_NAME));
                        emailList.add(email);
                    } else if (ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
                            .equals(type)) {
                        group = raws.getString(columnIndex_GROUP_MEMBERSHIP);
                    }
                    raws.moveToNext();

                    // filling the bean
                    if (contact != null) {
                        if (emailList.size() > 0)
                            contact.setContactEmails(emailList.toArray(new ContactEmail[emailList.size()]));
                        if (phoneList.size() > 0)
                            contact.setContactPhones(phoneList.toArray(new ContactPhone[phoneList.size()]));
                        if (firstname != null) contact.getPersonalInfo().setName(firstname);
                        if (lastname != null) contact.getPersonalInfo().setLastName(lastname);
                        if (name != null) contact.getPersonalInfo().setName(name);

                        // replace contact in the hashmap
                        contactsIDs.put(contactId, contact);
                    }
                }

            } finally {
                if (raws != null) {
                    raws.close();
                }
            }

            //LOGGER_CONTACTS.logDebug("Time lapsed (end loop raw data)", (new Date().getTime() - startLoopingRawData.getTime()) + " ms");

            /*if (query != null
                    && query.getColumn().equals(ContactQueryColumn.Phone)
                    && query.getCondition().equals(ContactQueryCondition.Available)) {
                //LOGGER_CONTACTS.logDebug("ListContacts", "Checking contacts to exclude due to condition (phone available)...");
                for (ContactLite contact : contactsIDs.values()) {
                    boolean addContactToFilteredList = true;
                    if (contact.getPhones() == null || contact.getPhones().length <= 0) {
                        addContactToFilteredList = false; // do not include contacts without phones available
                        excludedContacts++;
                    }

                    if (addContactToFilteredList) contactList.add(contact);
                }

                if (excludedContacts > 0)
                    //LOGGER_CONTACTS.logDebug("ListContacts", "Excluded contacts due to condition (phone available): " + excludedContacts);
            } else {
                // convert hashmap to list directly
                contactList = new ArrayList<Contact>(contactsIDs.values());
            }*/
            contactList = new ArrayList<Contact>(contactsIDs.values());
        } catch (Exception ex) {
            callback.onError(IContactResultCallback.Error.NoPermission);
        } finally {
            if (contactsCursor != null) {
                contactsCursor.close();
            }
        }

        Log.d("ARP TEST", "ARP: contactList has " + contactList.size() + " Contacts");
        Contact[] result = contactList.toArray(new Contact[contactList.size()]);
        for (int i = 0; i < contactList.size(); i++) result[i] = contactList.get(i);
        Log.d("ARP TEST", "ARP: result has " + result.length + " Contacts");
        callback.onResult(result);


    }

    private static ContactPhone.PhoneType getNumberType(int phoneType) {
        return PHONE_TYPES_MAP.get(phoneType) == null ? ContactPhone.PhoneType.Other
                : PHONE_TYPES_MAP.get(phoneType);
    }

    @Override
    public void searchContacts(String term, IContactResultCallback callback, Filter... filter) {

    }

    @Override
    public void getContact(ContactUid contact, IContactResultCallback callback) {

    }

    @Override
    public void getContactPhoto(ContactUid contact, IContactPhotoResultCallback callback) {

    }

    @Override
    public boolean setContactPhoto(ContactUid contact, byte[] pngImage) {
        return false;
    }

    @Override
    public void getContacts(IContactResultCallback callback) {

    }

    @Override
    public void getContacts(IContactResultCallback callback, FieldGroup... fields) {

    }

    @Override
    public void getContacts(IContactResultCallback callback, FieldGroup[] fields, Filter... filter) {

    }
}
