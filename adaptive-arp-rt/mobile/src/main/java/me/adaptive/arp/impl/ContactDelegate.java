/**
 --| ADAPTIVE RUNTIME PLATFORM |----------------------------------------------------------------------------------------

 (C) Copyright 2013-2015 Carlos Lozano Diez t/a Adaptive.me <http://adaptive.me>.

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 . Unless required by appli-
 -cable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the  License  for the specific language governing
 permissions and limitations under the License.

 Original author:

 * Carlos Lozano Diez
 <http://github.com/carloslozano>
 <http://twitter.com/adaptivecoder>
 <mailto:carlos@adaptive.me>

 Contributors:

 * Ferran Vila Conesa
 <http://github.com/fnva>
 <http://twitter.com/ferran_vila>
 <mailto:ferran.vila.conesa@gmail.com>

 * See source code files for contributors.

 Release:

 * @version v2.0.3

-------------------------------------------| aut inveniam viam aut faciam |--------------------------------------------
 */

package me.adaptive.arp.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.Contact;
import me.adaptive.arp.api.ContactAddress;
import me.adaptive.arp.api.ContactAddressType;
import me.adaptive.arp.api.ContactEmail;
import me.adaptive.arp.api.ContactPersonalInfo;
import me.adaptive.arp.api.ContactPersonalInfoTitle;
import me.adaptive.arp.api.ContactPhone;
import me.adaptive.arp.api.ContactPhoneType;
import me.adaptive.arp.api.ContactSocial;
import me.adaptive.arp.api.ContactUid;
import me.adaptive.arp.api.IContact;
import me.adaptive.arp.api.IContactFieldGroup;
import me.adaptive.arp.api.IContactFilter;
import me.adaptive.arp.api.IContactPhotoResultCallback;
import me.adaptive.arp.api.IContactPhotoResultCallbackError;
import me.adaptive.arp.api.IContactResultCallback;
import me.adaptive.arp.api.IContactResultCallbackError;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Interface for Managing the Contact operations
 * Auto-generated implementation of IContact specification.
 */
public class ContactDelegate extends BasePIMDelegate implements IContact {


    private static final Map<Integer, ContactPhoneType> PHONE_TYPES_MAP = new HashMap<Integer, ContactPhoneType>();
    private static final Map<Integer, ContactAddressType> ADDRESS_TYPES_MAP = new HashMap<Integer, ContactAddressType>();
    static {
        // Phone
        PHONE_TYPES_MAP.put(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME, ContactPhoneType.HomeFax);
        PHONE_TYPES_MAP.put(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, ContactPhoneType.Mobile);
        PHONE_TYPES_MAP.put(ContactsContract.CommonDataKinds.Phone.TYPE_WORK, ContactPhoneType.Work);
        PHONE_TYPES_MAP.put(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK, ContactPhoneType.WorkFax);
        PHONE_TYPES_MAP.put(ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN, ContactPhoneType.Work);
        PHONE_TYPES_MAP.put(ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE, ContactPhoneType.Work);
        PHONE_TYPES_MAP.put(ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM, ContactPhoneType.Other);

        // Address
        ADDRESS_TYPES_MAP.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME,
                ContactAddressType.Home);
        ADDRESS_TYPES_MAP.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK, ContactAddressType.Work);
        ADDRESS_TYPES_MAP.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER,
                ContactAddressType.Other);


    }
    public static String APIService = "contact";
    static LoggingDelegate Logger;

    /**
     * Default Constructor.
     */
    public ContactDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());
    }

    private static ContactPhoneType getNumberType(int phoneType) {
        return PHONE_TYPES_MAP.get(phoneType) == null ? ContactPhoneType.Other
                : PHONE_TYPES_MAP.get(phoneType);
    }

    private static ContactAddressType getAddressType(int value) {

        for (Map.Entry<Integer, ContactAddressType> e : ADDRESS_TYPES_MAP.entrySet()) {
            if (e.getKey().equals(value)) {
                return e.getValue();
            }
        }

        return ContactAddressType.Other;
    }

    /**
     * Get all the details of a contact according to its id
     *
     * @param contact  id to search for
     * @param callback called for return
     * @since ARP1.0
     */
    public void getContact(ContactUid contact, IContactResultCallback callback) {
        Contact contactBean = new Contact();
        contactBean.setContactId(contact.getContactId());

        Logger.log(ILoggingLogLevel.DEBUG, APIService, "getContact: Getting contactBean data for id: " + contact.getContactId());

        String company = null, department = null, firstMame = null, group = null, jobTitle = null, lastName = null, name = null, notes = null, photoBase64Encoded = null, displayName = null;
        List<ContactAddress> addressList = new ArrayList<ContactAddress>();
        List<ContactEmail> emailList = new ArrayList<ContactEmail>();
        List<ContactPhone> phoneList = new ArrayList<ContactPhone>();
        List<String> webSiteList = new ArrayList<String>();

        Context context = AppContextDelegate.getMainActivity().getApplicationContext();
        ContentResolver cr = context.getContentResolver();
        String selectionSingle = ContactsContract.RawContactsEntity.CONTACT_ID + " = " + contact.getContactId();
        Cursor raws = cr.query(ContactsContract.RawContactsEntity.CONTENT_URI, null,
                selectionSingle, null, null);
        try {
            raws.moveToFirst();
            while (!raws.isAfterLast()) {
                String type = raws
                        .getString(raws
                                .getColumnIndex(ContactsContract.Data.MIMETYPE));
                if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(type)) {
                    // structured name

                    displayName = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));

                    Logger.log(ILoggingLogLevel.DEBUG, APIService, "getContact: display name " + displayName);

                } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(type)) {
                    // it's a phone, gets all properties and add in
                    // the phone list
                    ContactPhone phone = new ContactPhone();
                    String number = raws.getString(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phone.setPhone(number);
                    int primary = raws.getInt(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY));
                    //phone.setIsPrimary(primary != 0);
                    int phoneType = raws.getInt(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    phone.setPhoneType(getNumberType(phoneType));
                    phoneList.add(phone);
                } else if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE
                        .equals(type)) {
                    // it's an Address, gets all properties and add
                    // in
                    // the address list
                    ContactAddress address = new ContactAddress();
                    String city = raws.getString(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                    String country = raws.getString(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                    String postcode = raws.getString(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                    String street = raws.getString(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                    // TODO Check about Number, as Android doesn't store
                    // number in a different column. (Is in the street)
                    // (linked with the way contacts are stored by this
                    // class)
                    int typeInt = raws.getInt(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                    ContactAddressType dType = getAddressType(typeInt);
                    address.setType(dType);
                    address.setAddress(street + " - " + postcode + " (" + city.toUpperCase() + ") " + country.toUpperCase());
                    //
                    //TODO: how to get the number?
                    // address.setAddressNumber(number);
                    //
                    addressList.add(address);
                } else if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(type)) {
                    // it's an Email address, gets all properties
                    // and add in the email list
                    ContactEmail email = new ContactEmail();
                    String emailAddress = raws
                            .getString(raws
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
                    email.setEmail(emailAddress);

                    // TODO set first name and last name
                    emailList.add(email);
                } else if (ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE.equals(type)) {
                    if (raws.getInt(raws
                            .getColumnIndex(ContactsContract.RawContactsEntity.IS_PRIMARY)) == 1
                            || company == null || company.equals("")) {
                        company = raws.getString(raws
                                .getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                        department = raws
                                .getString(raws
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Organization.DEPARTMENT));
                        jobTitle = raws.getString(raws
                                .getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                    }
                } else if (ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
                        .equals(type)) {
                    group = raws
                            .getString(raws
                                    .getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.DATA1));
                } else if (ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE.equals(type)) {
                    /*photo = raws.getBlob(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO));
                    photoBase64Encoded = photo != null ? new String(
                            Base64.encode(photo, Base64.DEFAULT))
                            : null;*/
                } else if (ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                        .equals(type)) {
                    webSiteList.add(raws.getString(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                } else if (ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE.equals(type)) {
                    // No need for nickname

                    String nickName = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.NAME));

                    Logger.log(ILoggingLogLevel.DEBUG, APIService, "getContact: nickName: " + nickName);

                } else if (ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE.equals(type)) {
                    if (raws.getInt(raws
                            .getColumnIndex(ContactsContract.RawContactsEntity.IS_PRIMARY)) == 1
                            || notes == null || notes.equals("")) {
                        notes = raws.getString(raws
                                .getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                    }
                }
                raws.moveToNext();
            }

        } finally {
            if (raws != null) {
                raws.close();
            }
        }

        // filling the bean

        contactBean.setContactAddresses(addressList
                .toArray(new ContactAddress[addressList.size()]));
        contactBean.setContactSocials(webSiteList.toArray(new ContactSocial[webSiteList
                .size()]));

        contactBean.setContactEmails(emailList.toArray(new ContactEmail[emailList.size()]));
        contactBean.setContactPhones(phoneList.toArray(new ContactPhone[phoneList.size()]));

        contactBean.setPersonalInfo(new ContactPersonalInfo(displayName, null, lastName, ContactPersonalInfoTitle.Mr));

        callback.onResult(new Contact[]{contactBean});
    }

    /**
     * Get the contact photo
     *
     * @param contact  id to search for
     * @param callback called for return
     * @since ARP1.0
     */
    public void getContactPhoto(ContactUid contact, IContactPhotoResultCallback callback) {
        Context context = AppContextDelegate.getMainActivity().getApplicationContext();
        ContentResolver cr = context.getContentResolver();
        String selectionSingle = ContactsContract.RawContactsEntity.CONTACT_ID + " = " + contact.getContactId();
        Cursor raws = cr.query(ContactsContract.RawContactsEntity.CONTENT_URI, null,
                selectionSingle, null, null);
        byte[] photo = null;
        try {
            raws.moveToFirst();
            while (!raws.isAfterLast()) {
                String type = raws
                        .getString(raws
                                .getColumnIndex(ContactsContract.Data.MIMETYPE));
                if (ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE.equals(type)) {
                    photo = raws.getBlob(raws
                            .getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO));

                    raws.close();
                }
                raws.moveToNext();
            }

        } finally {
            if (raws != null) {
                raws.close();
            }
        }

        if (photo != null) callback.onResult(photo);
        else callback.onError(IContactPhotoResultCallbackError.No_Photo);
    }

    /**
     * Get all contacts
     *
     * @param callback called for return
     * @since ARP1.0
     */
    public void getContacts(IContactResultCallback callback) {
        searchContacts(null, callback);
    }

    /**
     * Get marked fields of all contacts
     *
     * @param callback called for return
     * @param fields   to get for each Contact
     * @since ARP1.0
     */
    public void getContactsForFields(final IContactResultCallback callback, final IContactFieldGroup[] fields) {
        AppContextDelegate.getExecutorService().submit(new Runnable() {
            public void run() {
                Date startSearching = new Date();
                boolean addressRequired = false, mailRequired = false, phoneRequired = false;

                Uri uri = null;
                Context context = AppContextDelegate.getMainActivity().getApplicationContext();
                ContentResolver cr = context.getContentResolver();
                Cursor contactsCursor = null;
                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
                Cursor cur;


                uri = ContactsContract.Contacts.CONTENT_URI;
                contactsCursor = cr.query(uri, new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME}, null, null, sortOrder);


                List<Contact> contactList = new ArrayList<Contact>();
                List<String> webSiteList = new ArrayList<String>();
                try {
                    // iterate cursor to
                    contactsCursor.moveToFirst();
                    int excludedContacts = 0;
                    Map<String, Contact> contactsIDs = new HashMap<String, Contact>();
                    String contactsIDsString = "";
                    while (!contactsCursor.isAfterLast()) {


                        Long id = contactsCursor.getLong(0);
                        String displayName = contactsCursor.getString(1);
                        Contact contact = new Contact(id.toString());
                        ContactPersonalInfo pI = new ContactPersonalInfo();
                        pI.setName(displayName);
                        contact.setPersonalInfo(pI);

                        contactsIDs.put("" + id, contact);
                        contactsIDsString += (contactsIDsString.length() > 0 ? "," : "") + "'" + id + "'";
                        contactsCursor.moveToNext();
                    }


                    // querying the content resolver for all contacts

                    String selectionMultiple = ContactsContract.RawContactsEntity.CONTACT_ID + " IN (" + contactsIDsString + ")";

                    Cursor raws = cr.query(ContactsContract.RawContactsEntity.CONTENT_URI, projectionFromFieldGroup(fields),
                            selectionMultiple, null, null);

                    try {
                        raws.moveToFirst();

                        // it seems that there is a time saving by querying the indexes before the cursor loop
                        int columnIndex_CONTACTID = raws.getColumnIndex(ContactsContract.RawContactsEntity.CONTACT_ID);
                        int columnIndex_MIMETYPE = raws.getColumnIndex(ContactsContract.Data.MIMETYPE);
                        int columnIndex_PHONE_NUMBER = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int columnIndex_IS_PRIMARY = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY);
                        int columnIndex_PHONE_TYPE = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                        int columnIndex_EMAIL_ADDRESS = raws.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1);
                        //int columnIndex_DISPLAY_NAME = raws.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME);
                        int columnIndex_GROUP_MEMBERSHIP = raws.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.DATA1);
                        int columnIndex_FORMATTED_ADDRESS = raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA1);

                        while (!raws.isAfterLast()) {

                            String contactId = raws.getString(columnIndex_CONTACTID);

                            String firstname = null, group = null, lastname = null, name = null, address = null;
                            List<ContactEmail> emailList = new ArrayList<ContactEmail>();
                            List<ContactPhone> phoneList = new ArrayList<ContactPhone>();
                            List<ContactAddress> addressList = new ArrayList<ContactAddress>();
                            Contact contact = null;

                            if (contactsIDs != null && contactsIDs.containsKey(contactId)) {
                                contact = contactsIDs.get(contactId);
                                // asList returns unmodifiable lists, so we need to create new lists from them
                                if (contact.getContactEmails() != null)
                                    emailList.addAll(Arrays.asList(contact.getContactEmails()));
                                if (contact.getContactPhones() != null)
                                    phoneList.addAll(Arrays.asList(contact.getContactPhones()));
                                if (contact.getContactAddresses() != null)
                                    addressList.addAll(Arrays.asList(contact.getContactAddresses()));
                            }

                            String type = raws.getString(columnIndex_MIMETYPE);
                            if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(type)/* && requiredField(fields,FieldGroup.PERSONAL_INFO)*/) {
                                // structured name
                                firstname = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                                lastname = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                                //displayName = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                            } else if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(type)/* && requiredField(fields,FieldGroup.ADDRESSES)*/) {
                                // structured name

                                ContactAddress contactAddress = new ContactAddress();
                                address = raws.getString(columnIndex_FORMATTED_ADDRESS);
                                contactAddress.setAddress(address);
                                //email.setCommonName(raws.getString(columnIndex_DISPLAY_NAME));
                                addressList.add(contactAddress);
                                //displayName = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                            } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE.equals(type)/* && requiredField(fields,FieldGroup.PHONES)*/) {
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
                            } else if (ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE.equals(type)/* && requiredField(fields,FieldGroup.EMAILS)*/) {
                                // it's an Email address, gets all properties
                                // and add in the email list
                                ContactEmail email = new ContactEmail();
                                String emailaddress = raws.getString(columnIndex_EMAIL_ADDRESS);
                                email.setEmail(emailaddress);
                                //email.setCommonName(raws.getString(columnIndex_DISPLAY_NAME));
                                emailList.add(email);
                            } else if (ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE
                                    .equals(type)/* && requiredField(fields,FieldGroup.SOCIALS)*/) {
                                webSiteList.add(raws.getString(raws
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                            } else if (ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
                                    .equals(type)) {
                                group = raws.getString(columnIndex_GROUP_MEMBERSHIP);
                            }

                            if (contactsIDs != null && contactsIDs.containsKey(contactId)) {
                                contact = contactsIDs.get(contactId);
                            }
                            // filling the bean
                            if (contact != null) {

                                if (emailList.size() > 0)
                                    contact.setContactEmails(emailList.toArray(new ContactEmail[emailList.size()]));
                                if (phoneList.size() > 0) {
                                    contact.setContactPhones(phoneList.toArray(new ContactPhone[phoneList.size()]));
                                }
                                if (phoneList.size() > 0) {
                                    contact.setContactPhones(phoneList.toArray(new ContactPhone[phoneList.size()]));
                                }
                                if (addressList.size() > 0) {
                                    contact.setContactAddresses(addressList.toArray(new ContactAddress[addressList.size()]));
                                }
                                if (webSiteList.size() > 0) {
                                    contact.setContactSocials(webSiteList.toArray(new ContactSocial[webSiteList
                                            .size()]));
                                }

                                if (firstname != null) contact.getPersonalInfo().setName(firstname);
                                if (lastname != null)
                                    contact.getPersonalInfo().setLastName(lastname);
                                if (name != null) contact.getPersonalInfo().setName(name);


                                // replace contact in the hashmap
                                contactsIDs.put(contactId, contact);

                            }
                            raws.moveToNext();
                        }

                    } finally {
                        if (raws != null) {
                            raws.close();
                        }
                    }
                    contactList = new ArrayList<Contact>(contactsIDs.values());
                } catch (Exception ex) {
                    callback.onError(IContactResultCallbackError.NoPermission);
                    return;
                } finally {
                    if (contactsCursor != null) {
                        contactsCursor.close();
                    }
                }

                Logger.log(ILoggingLogLevel.DEBUG, APIService, "ARP: contactList has " + contactList.size() + " Contacts");
                callback.onResult((Contact[]) contactList.toArray(new Contact[contactList.size()]));

            }

        });
    }

    /**
     * Get marked fields of all contacts according to a filter
     *
     * @param callback called for return
     * @param fields   to get for each Contact
     * @param filter   to search for
     * @since ARP1.0
     */
    public void getContactsWithFilter(final IContactResultCallback callback, final IContactFieldGroup[] fields, final IContactFilter[] filter) {
        AppContextDelegate.getExecutorService().submit(new Runnable() {
            public void run() {
                Date startSearching = new Date();
                boolean addressRequired = false, mailRequired = false, phoneRequired = false;
                for (int i = 0; i < filter.length; i++) {
                    if (filter[i].equals(IContactFilter.HAS_ADDRESS)) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Filter: HAS_ADDRESS");
                        addressRequired = true;
                    }
                    if (filter[i].equals(IContactFilter.HAS_EMAIL)) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Filter: HAS_EMAIL");
                        mailRequired = true;
                    }
                    if (filter[i].equals(IContactFilter.HAS_PHONE)) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Filter: HAS_PHONE");
                        phoneRequired = true;
                    }


                }
                Uri uri = null;
                Context context = AppContextDelegate.getMainActivity().getApplicationContext();
                ContentResolver cr = context.getContentResolver();
                Cursor contactsCursor = null;
                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
                Cursor cur;


                uri = ContactsContract.Contacts.CONTENT_URI;
                contactsCursor = cr.query(uri, new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME}, null, null, sortOrder);


                List<Contact> contactList = new ArrayList<Contact>();
                try {
                    // iterate cursor to
                    contactsCursor.moveToFirst();
                    int excludedContacts = 0;
                    Map<String, Contact> contactsIDs = new HashMap<String, Contact>();
                    String contactsIDsString = "";
                    while (!contactsCursor.isAfterLast()) {


                        Long id = contactsCursor.getLong(0);
                        String displayName = contactsCursor.getString(1);
                        Contact contact = new Contact(id.toString());
                        ContactPersonalInfo pI = new ContactPersonalInfo();
                        pI.setName(displayName);
                        contact.setPersonalInfo(pI);

                        contactsIDs.put("" + id, contact);
                        contactsIDsString += (contactsIDsString.length() > 0 ? "," : "") + "'" + id + "'";
                        contactsCursor.moveToNext();
                    }


                    // querying the content resolver for all contacts

                    String selectionMultiple = ContactsContract.RawContactsEntity.CONTACT_ID + " IN (" + contactsIDsString + ")";
                    Cursor raws = cr.query(ContactsContract.RawContactsEntity.CONTENT_URI, projectionFromFieldGroup(fields),
                            selectionMultiple, null, null);

                    try {
                        raws.moveToFirst();

                        // it seems that there is a time saving by querying the indexes before the cursor loop
                        int columnIndex_CONTACTID = raws.getColumnIndex(ContactsContract.RawContactsEntity.CONTACT_ID);
                        int columnIndex_MIMETYPE = raws.getColumnIndex(ContactsContract.Data.MIMETYPE);
                        int columnIndex_PHONE_NUMBER = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int columnIndex_IS_PRIMARY = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY);
                        int columnIndex_PHONE_TYPE = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                        int columnIndex_EMAIL_ADDRESS = raws.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1);
                        //int columnIndex_DISPLAY_NAME = raws.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME);
                        int columnIndex_GROUP_MEMBERSHIP = raws.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.DATA1);
                        int columnIndex_FORMATTED_ADDRESS = raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA1);

                        while (!raws.isAfterLast()) {

                            String contactId = raws.getString(columnIndex_CONTACTID);

                            String firstname = null, group = null, lastname = null, name = null, address = null;
                            List<ContactEmail> emailList = new ArrayList<ContactEmail>();
                            List<ContactPhone> phoneList = new ArrayList<ContactPhone>();
                            List<ContactAddress> addressList = new ArrayList<ContactAddress>();
                            Contact contact = null;

                            if (contactsIDs != null && contactsIDs.containsKey(contactId)) {
                                contact = contactsIDs.get(contactId);
                                // asList returns unmodifiable lists, so we need to create new lists from them
                                if (contact.getContactEmails() != null)
                                    emailList.addAll(Arrays.asList(contact.getContactEmails()));
                                if (contact.getContactPhones() != null)
                                    phoneList.addAll(Arrays.asList(contact.getContactPhones()));
                                if (contact.getContactAddresses() != null)
                                    addressList.addAll(Arrays.asList(contact.getContactAddresses()));
                            }

                            String type = raws.getString(columnIndex_MIMETYPE);
                            if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(type)) {
                                // structured name
                                firstname = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                                lastname = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                                //displayName = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                            } else if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(type)) {
                                // structured name

                                ContactAddress contactAddress = new ContactAddress();
                                address = raws.getString(columnIndex_FORMATTED_ADDRESS);
                                contactAddress.setAddress(address);
                                //email.setCommonName(raws.getString(columnIndex_DISPLAY_NAME));
                                addressList.add(contactAddress);
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

                            if (contactsIDs != null && contactsIDs.containsKey(contactId)) {
                                contact = contactsIDs.get(contactId);
                            }
                            // filling the bean
                            if (contact != null) {

                                if (emailList.size() > 0)
                                    contact.setContactEmails(emailList.toArray(new ContactEmail[emailList.size()]));
                                if (phoneList.size() > 0) {
                                    contact.setContactPhones(phoneList.toArray(new ContactPhone[phoneList.size()]));
                                }
                                if (phoneList.size() > 0) {
                                    contact.setContactPhones(phoneList.toArray(new ContactPhone[phoneList.size()]));
                                }
                                if (addressList.size() > 0) {
                                    contact.setContactAddresses(addressList.toArray(new ContactAddress[addressList.size()]));
                                }

                                if (firstname != null) contact.getPersonalInfo().setName(firstname);
                                if (lastname != null)
                                    contact.getPersonalInfo().setLastName(lastname);
                                if (name != null) contact.getPersonalInfo().setName(name);


                                // replace contact in the hashmap
                                contactsIDs.put(contactId, contact);

                            }
                            raws.moveToNext();
                        }

                    } finally {
                        if (raws != null) {
                            raws.close();
                        }
                    }

                    List<String> removeContact = new ArrayList<String>();
                    for (Contact contact : contactsIDs.values()) {
                        if (mailRequired && contact.getContactEmails() == null) {
                            removeContact.add(contact.getContactId());


                        }
                        if (phoneRequired && contact.getContactPhones() == null) {
                            removeContact.add(contact.getContactId());

                        }
                        if (addressRequired && contact.getContactAddresses() == null) {
                            removeContact.add(contact.getContactId());

                        }
                    }

                    for (String key : removeContact) {
                        contactsIDs.remove(key);
                    }

                    contactList = new ArrayList<Contact>(contactsIDs.values());


                } catch (Exception ex) {
                    callback.onError(IContactResultCallbackError.NoPermission);
                    return;
                } finally {
                    if (contactsCursor != null) {
                        contactsCursor.close();
                    }
                }

                Logger.log(ILoggingLogLevel.DEBUG, APIService, "ARP: contactList has " + contactList.size() + " Contacts");
                callback.onResult((Contact[]) contactList.toArray(new Contact[contactList.size()]));

            }
        });
    }

    /**
     * Search contacts according to a term and send it to the callback
     *
     * @param term     string to search
     * @param callback called for return
     * @since ARP1.0
     */
    public void searchContacts(final String term, final IContactResultCallback callback) {
        AppContextDelegate.getExecutorService().submit(new Runnable() {
            public void run() {
                Logger.log(ILoggingLogLevel.DEBUG, APIService, "searchContacts: term " + term);
                Date startSearching = new Date();
                Uri uri = null;
                Context context = AppContextDelegate.getMainActivity().getApplicationContext();
                ContentResolver cr = context.getContentResolver();
                Cursor contactsCursor = null;
                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";


                if (term != null) {

                    String selection = null;
                    String[] selectionValues = null;
                    selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
                    selectionValues = new String[]{"%" + term + "%"};
                    uri = ContactsContract.Contacts.CONTENT_URI;

                    contactsCursor = cr.query(uri, new String[]{
                            ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME}, selection, selectionValues, sortOrder);

                } else {
                    uri = ContactsContract.Contacts.CONTENT_URI;
                    contactsCursor = cr.query(uri, new String[]{
                            ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME}, null, null, sortOrder);
                }

                List<Contact> contactList = new ArrayList<Contact>();
                try {
                    // iterate cursor to
                    contactsCursor.moveToFirst();
                    int excludedContacts = 0;
                    Map<String, Contact> contactsIDs = new HashMap<String, Contact>();
                    String contactsIDsString = "";
                    while (!contactsCursor.isAfterLast()) {
                        Long id = contactsCursor.getLong(0);
                        String displayName = contactsCursor.getString(1);
                        Contact contact = new Contact(id.toString());
                        ContactPersonalInfo pI = new ContactPersonalInfo();
                        pI.setName(displayName);
                        contact.setPersonalInfo(pI);

                        contactsIDs.put("" + id, contact);
                        contactsIDsString += (contactsIDsString.length() > 0 ? "," : "") + "'" + id + "'";
                        contactsCursor.moveToNext();
                    }

                    // querying the content resolver for all contacts
                    String selectionMultiple = ContactsContract.RawContactsEntity.CONTACT_ID + " IN (" + contactsIDsString + ") ";
                    Cursor raws = cr.query(ContactsContract.RawContactsEntity.CONTENT_URI, null,
                            selectionMultiple, null, null);

                    try {
                        raws.moveToFirst();

                        // it seems that there is a time saving by querying the indexes before the cursor loop
                        int columnIndex_CONTACTID = raws.getColumnIndex(ContactsContract.RawContactsEntity.CONTACT_ID);
                        int columnIndex_MIMETYPE = raws.getColumnIndex(ContactsContract.Data.MIMETYPE);
                        int columnIndex_PHONE_NUMBER = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int columnIndex_IS_PRIMARY = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY);
                        int columnIndex_PHONE_TYPE = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                        int columnIndex_EMAIL_ADDRESS = raws.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1);
                        int columnIndex_GROUP_MEMBERSHIP = raws.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.DATA1);
                        int columnIndex_FORMATTED_ADDRESS = raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA1);

                        while (!raws.isAfterLast()) {

                            String contactId = raws.getString(columnIndex_CONTACTID);

                            String firstname = null, group = null, lastname = null, name = null, address = null;
                            List<ContactEmail> emailList = new ArrayList<ContactEmail>();
                            List<ContactPhone> phoneList = new ArrayList<ContactPhone>();
                            List<ContactAddress> addressList = new ArrayList<ContactAddress>();
                            Contact contact = null;

                            if (contactsIDs != null && contactsIDs.containsKey(contactId)) {
                                contact = contactsIDs.get(contactId);
                                // asList returns unmodifiable lists, so we need to create new lists from them
                                if (contact.getContactEmails() != null)
                                    emailList.addAll(Arrays.asList(contact.getContactEmails()));
                                if (contact.getContactPhones() != null)
                                    phoneList.addAll(Arrays.asList(contact.getContactPhones()));
                                if (contact.getContactAddresses() != null)
                                    addressList.addAll(Arrays.asList(contact.getContactAddresses()));
                            }

                            String type = raws.getString(columnIndex_MIMETYPE);
                            if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(type)) {
                                // structured name
                                firstname = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                                lastname = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                                //displayName = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                            } else if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(type)) {
                                // structured name
                                ContactAddress contactAddress = new ContactAddress();
                                address = raws.getString(columnIndex_FORMATTED_ADDRESS);
                                contactAddress.setAddress(address);
                                //email.setCommonName(raws.getString(columnIndex_DISPLAY_NAME));
                                addressList.add(contactAddress);
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
                                if (addressList.size() > 0) {
                                    contact.setContactAddresses(addressList.toArray(new ContactAddress[addressList.size()]));
                                }
                                if (firstname != null) contact.getPersonalInfo().setName(firstname);
                                if (lastname != null)
                                    contact.getPersonalInfo().setLastName(lastname);
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

                    contactList = new ArrayList<Contact>(contactsIDs.values());
                } catch (Exception ex) {
                    callback.onError(IContactResultCallbackError.NoPermission);
                    return;
                } finally {
                    if (contactsCursor != null) {
                        contactsCursor.close();
                    }
                }

                Logger.log(ILoggingLogLevel.DEBUG, APIService, "ARP: contactList has " + contactList.size() + " Contacts");
                Logger.log(ILoggingLogLevel.DEBUG, APIService, "Time lapsed (query raw data for all contacts matched)" + (new Date().getTime() - startSearching.getTime()) + " ms");
                callback.onResult((Contact[]) contactList.toArray(new Contact[contactList.size()]));
            }
        });
    }

    /**
     * Search contacts according to a term with a filter and send it to the callback
     *
     * @param term     string to search
     * @param callback called for return
     * @param filter   to search for
     * @since ARP1.0
     */
    public void searchContactsWithFilter(final String term, final IContactResultCallback callback, final IContactFilter[] filter) {
        AppContextDelegate.getExecutorService().submit(new Runnable() {
            public void run() {
                Date startSearching = new Date();
                Boolean addressRequired = false;
                Boolean mailRequired = false;
                Boolean phoneRequired = false;
                for (int i = 0; i < filter.length; i++) {
                    if (filter[i].equals(IContactFilter.HAS_ADDRESS)) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Filter: HAS_ADDRESS");
                        addressRequired = true;
                    }
                    if (filter[i].equals(IContactFilter.HAS_EMAIL)) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Filter: HAS_EMAIL");
                        mailRequired = true;
                    }
                    if (filter[i].equals(IContactFilter.HAS_PHONE)) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Filter: HAS_PHONE");
                        phoneRequired = true;
                    }


                }
                Uri uri = null;
                Context context = AppContextDelegate.getMainActivity().getApplicationContext();
                ContentResolver cr = context.getContentResolver();
                Cursor contactsCursor = null;
                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
                Cursor cur;


                if (term != null) {

                    String selection = null;
                    String[] selectionValues = null;

                    selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
                    selectionValues = new String[]{"%" + term + "%"};

                    uri = ContactsContract.Contacts.CONTENT_URI;
                    contactsCursor = cr.query(uri, new String[]{
                            ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME}, selection, selectionValues, sortOrder);


                } else {
                    uri = ContactsContract.Contacts.CONTENT_URI;
                    contactsCursor = cr.query(uri, new String[]{
                            ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME}, null, null, sortOrder);


                }

                List<Contact> contactList = new ArrayList<Contact>();
                try {
                    // iterate cursor to
                    contactsCursor.moveToFirst();
                    int excludedContacts = 0;
                    Map<String, Contact> contactsIDs = new HashMap<String, Contact>();
                    String contactsIDsString = "";
                    while (!contactsCursor.isAfterLast()) {


                        Long id = contactsCursor.getLong(0);
                        String displayName = contactsCursor.getString(1);
                        Contact contact = new Contact(id.toString());
                        ContactPersonalInfo pI = new ContactPersonalInfo();
                        pI.setName(displayName);
                        contact.setPersonalInfo(pI);

                        contactsIDs.put("" + id, contact);
                        contactsIDsString += (contactsIDsString.length() > 0 ? "," : "") + "'" + id + "'";
                        contactsCursor.moveToNext();
                    }


                    // querying the content resolver for all contacts

                    String selectionMultiple = ContactsContract.RawContactsEntity.CONTACT_ID + " IN (" + contactsIDsString + ")";
                    Cursor raws = cr.query(ContactsContract.RawContactsEntity.CONTENT_URI, null,
                            selectionMultiple, null, null);

                    try {
                        raws.moveToFirst();

                        // it seems that there is a time saving by querying the indexes before the cursor loop
                        int columnIndex_CONTACTID = raws.getColumnIndex(ContactsContract.RawContactsEntity.CONTACT_ID);
                        int columnIndex_MIMETYPE = raws.getColumnIndex(ContactsContract.Data.MIMETYPE);
                        int columnIndex_PHONE_NUMBER = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int columnIndex_IS_PRIMARY = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY);
                        int columnIndex_PHONE_TYPE = raws.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                        int columnIndex_EMAIL_ADDRESS = raws.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1);
                        //int columnIndex_DISPLAY_NAME = raws.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME);
                        int columnIndex_GROUP_MEMBERSHIP = raws.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.DATA1);
                        int columnIndex_FORMATTED_ADDRESS = raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA1);

                        while (!raws.isAfterLast()) {

                            String contactId = raws.getString(columnIndex_CONTACTID);

                            String firstname = null, group = null, lastname = null, name = null, address = null;
                            List<ContactEmail> emailList = new ArrayList<ContactEmail>();
                            List<ContactPhone> phoneList = new ArrayList<ContactPhone>();
                            List<ContactAddress> addressList = new ArrayList<ContactAddress>();
                            Contact contact = null;

                            if (contactsIDs != null && contactsIDs.containsKey(contactId)) {
                                contact = contactsIDs.get(contactId);
                                // asList returns unmodifiable lists, so we need to create new lists from them
                                if (contact.getContactEmails() != null)
                                    emailList.addAll(Arrays.asList(contact.getContactEmails()));
                                if (contact.getContactPhones() != null)
                                    phoneList.addAll(Arrays.asList(contact.getContactPhones()));
                                if (contact.getContactAddresses() != null)
                                    addressList.addAll(Arrays.asList(contact.getContactAddresses()));
                            }

                            String type = raws.getString(columnIndex_MIMETYPE);
                            if (ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE.equals(type)) {
                                // structured name
                                firstname = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                                lastname = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                                //displayName = raws.getString(raws.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                            } else if (ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE.equals(type)) {
                                // structured name

                                ContactAddress contactAddress = new ContactAddress();
                                address = raws.getString(columnIndex_FORMATTED_ADDRESS);
                                contactAddress.setAddress(address);
                                //email.setCommonName(raws.getString(columnIndex_DISPLAY_NAME));
                                addressList.add(contactAddress);
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

                            if (contactsIDs != null && contactsIDs.containsKey(contactId)) {
                                contact = contactsIDs.get(contactId);
                            }
                            // filling the bean
                            if (contact != null) {

                                if (emailList.size() > 0)
                                    contact.setContactEmails(emailList.toArray(new ContactEmail[emailList.size()]));
                                if (phoneList.size() > 0) {
                                    contact.setContactPhones(phoneList.toArray(new ContactPhone[phoneList.size()]));
                                }
                                if (phoneList.size() > 0) {
                                    contact.setContactPhones(phoneList.toArray(new ContactPhone[phoneList.size()]));
                                }
                                if (addressList.size() > 0) {
                                    contact.setContactAddresses(addressList.toArray(new ContactAddress[addressList.size()]));
                                }

                                if (firstname != null) contact.getPersonalInfo().setName(firstname);
                                if (lastname != null)
                                    contact.getPersonalInfo().setLastName(lastname);
                                if (name != null) contact.getPersonalInfo().setName(name);


                                // replace contact in the hashmap
                                contactsIDs.put(contactId, contact);

                            }
                            raws.moveToNext();
                        }

                    } finally {
                        if (raws != null) {
                            raws.close();
                        }
                    }


                    List<String> removeContact = new ArrayList<String>();
                    for (Contact contact : contactsIDs.values()) {
                        if (mailRequired && contact.getContactEmails() == null) {
                            removeContact.add(contact.getContactId());


                        }
                        if (phoneRequired && contact.getContactPhones() == null) {
                            removeContact.add(contact.getContactId());

                        }
                        if (addressRequired && contact.getContactAddresses() == null) {
                            removeContact.add(contact.getContactId());

                        }
                    }

                    for (String key : removeContact) {
                        contactsIDs.remove(key);
                    }

                    contactList = new ArrayList<Contact>(contactsIDs.values());


                } catch (Exception ex) {
                    callback.onError(IContactResultCallbackError.NoPermission);
                    return;
                } finally {
                    if (contactsCursor != null) {
                        contactsCursor.close();
                    }
                }

                Logger.log(ILoggingLogLevel.DEBUG, APIService, "ARP: contactList has " + contactList.size() + " Contacts");
                Logger.log(ILoggingLogLevel.DEBUG, APIService, "Time lapsed (main query A)" + (new Date().getTime() - startSearching.getTime()) + " ms");
                callback.onResult((Contact[]) contactList.toArray(new Contact[contactList.size()]));
            }

        });
    }

    /**
     * Set the contact photo
     *
     * @param contact  id to assign the photo
     * @param pngImage photo as byte array
     * @return true if set is successful;false otherwise
     * @since ARP1.0
     */
    public boolean setContactPhoto(ContactUid contact, byte[] pngImage) {
        boolean response;
        // TODO: Not implemented.
        throw new UnsupportedOperationException(this.getClass().getName() + ":setContactPhoto");
        // return response;
    }

    /**
     * Create a projection from selected fields
     *
     * @param fields to search
     * @return string generated
     */
    private String[] projectionFromFieldGroup(IContactFieldGroup[] fields) {
        List<String> projection = new ArrayList<String>();
        int i = 0;
        String CONTACT_ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String NAME = ContactsContract.CommonDataKinds.StructuredName.DATA1;
        String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String EMAIL_DATA = ContactsContract.CommonDataKinds.Email.DATA;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String STARRED_CONTACT = ContactsContract.Contacts.STARRED;
        String ADDRESS = ContactsContract.CommonDataKinds.StructuredPostal.DATA1;


        projection.add(CONTACT_ID);

        for (IContactFieldGroup field : fields) {
            switch (field) {
                case ADDRESSES:
                    projection.add(ADDRESS);
                    break;
                case EMAILS:
                    projection.add(DISPLAY_NAME);
                    projection.add(NAME);
                    break;
                case PERSONAL_INFO:
                    projection.add(EMAIL_CONTACT_ID);
                    projection.add(EMAIL_DATA);
                    break;
                case PHONES:
                    projection.add(PHONE_CONTACT_ID);
                    projection.add(HAS_PHONE_NUMBER);
                    projection.add(PHONE_NUMBER);
                    break;
                case PROFESSIONAL_INFO:
                    projection.add(ContactsContract.CommonDataKinds.Organization.DATA1);
                case SOCIALS:
                    break;
                case TAGS:
                    break;
                case WEBSITES:
                    projection.add(ContactsContract.CommonDataKinds.Website.DATA1);
                    break;
            }
        }

        return (String[]) projection.toArray(new String[projection.size()]);
    }


}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
