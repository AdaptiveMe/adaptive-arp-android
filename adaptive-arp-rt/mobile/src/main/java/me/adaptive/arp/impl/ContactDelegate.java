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

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import me.adaptive.arp.api.ContactEmailType;
import me.adaptive.arp.api.ContactPersonalInfo;
import me.adaptive.arp.api.ContactPersonalInfoTitle;
import me.adaptive.arp.api.ContactPhone;
import me.adaptive.arp.api.ContactPhoneType;
import me.adaptive.arp.api.ContactProfessionalInfo;
import me.adaptive.arp.api.ContactUid;
import me.adaptive.arp.api.ContactWebsite;
import me.adaptive.arp.api.IContact;
import me.adaptive.arp.api.IContactFieldGroup;
import me.adaptive.arp.api.IContactFilter;
import me.adaptive.arp.api.IContactPhotoResultCallback;
import me.adaptive.arp.api.IContactPhotoResultCallbackError;
import me.adaptive.arp.api.IContactResultCallback;
import me.adaptive.arp.api.IContactResultCallbackError;
import me.adaptive.arp.api.IContactResultCallbackWarning;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Interface for Managing the Contact operations
 * Auto-generated implementation of IContact specification.
 */
public class ContactDelegate extends BasePIMDelegate implements IContact {


    public static String APIService = "contact";
    private static LoggingDelegate Logger;

    /**
     * Default Constructor.
     */
    public ContactDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());
    }

    /**
     * Get all the details of a contact according to its id
     *
     * @param contact  id to search for
     * @param callback called for return
     * @since ARP1.0
     */
    public void getContact(final ContactUid contact, final IContactResultCallback callback) {
        nativeToAdaptive(callback, contact, null, null, null);

    }

    /**
     * Get all contacts
     *
     * @param callback called for return
     * @since ARP1.0
     */
    public void getContacts(IContactResultCallback callback) {
        nativeToAdaptive(callback, null, null, null, null);
    }

    /**
     * Get marked fields of all contacts
     *
     * @param callback called for return
     * @param fields   to get for each Contact
     * @since ARP1.0
     */
    public void getContactsForFields(IContactResultCallback callback, IContactFieldGroup[] fields) {
        nativeToAdaptive(callback, null, null, fields, null);
    }

    /**
     * Get marked fields of all contacts according to a filter
     *
     * @param callback called for return
     * @param fields   to get for each Contact
     * @param filter   to search for
     * @since ARP1.0
     */
    public void getContactsWithFilter(IContactResultCallback callback, IContactFieldGroup[] fields, IContactFilter[] filter) {
        nativeToAdaptive(callback, null, null, fields, filter);
    }

    /**
     * Search contacts according to a term and send it to the callback
     *
     * @param term     string to search
     * @param callback called for return
     * @since ARP1.0
     */
    public void searchContacts(String term, IContactResultCallback callback) {
        nativeToAdaptive(callback, null, term, null, null);
    }

    /**
     * Search contacts according to a term with a filter and send it to the callback
     *
     * @param term     string to search
     * @param callback called for return
     * @param filter   to search for
     * @since ARP1.0
     */
    public void searchContactsWithFilter(String term, IContactResultCallback callback, IContactFilter[] filter) {
        nativeToAdaptive(callback, null, term, null, filter);
    }



    @Override
    public void getContactPhoto(ContactUid contact, IContactPhotoResultCallback callback) {
        if(contact == null){
            callback.onError(IContactPhotoResultCallbackError.WrongParams);
            return;
        }
        Context context = ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext();
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contact.getContactId()));
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd = context.
                    getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            InputStream image = fd.createInputStream();
            byte[] bytes = getBytesFromInputStream(image);
            if(bytes == null) {
                callback.onResult(bytes);
                return;
            }
        } catch (IOException e) {
            Logger.log(ILoggingLogLevel.Error,"Error getting the photo: "+Log.getStackTraceString(e));
            callback.onError(IContactPhotoResultCallbackError.Unknown);
        }
        callback.onError(IContactPhotoResultCallbackError.Unknown);
    }


    /**
     * Cast from InputStream to byte[]
     * @param is InputStream
     * @return byte[]
     */
    public static byte[] getBytesFromInputStream(InputStream is)
    {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
        {
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = is.read(buffer)) != -1;)
                os.write(buffer, 0, len);

            os.flush();

            return os.toByteArray();
        }
        catch (IOException e)
        {
            return null;
        }
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

        try {
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, contact.getContactId()) // here 9 is _ID where I'm inserting image
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, pngImage)
                    .build();
        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.Error, APIService, "setContactPhoto Error: " + Log.getStackTraceString(ex));
            return false;
        }
        return true;

    }

    /**
     * @param callback
     * @param contact
     * @param term
     * @param fields
     * @param filter
     */
    private void nativeToAdaptive(final IContactResultCallback callback, final ContactUid contact, final String term, final IContactFieldGroup[] fields, final IContactFilter[] filter) {
        ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getExecutorService().submit(new Runnable() {
            public void run() {
                Logger.log(ILoggingLogLevel.Debug, APIService, "androidContactToAdaptive contactID[" + (contact == null ? "NO_ID" : contact.getContactId()) + "] - term[" + (term == null ? "NO_TERM" : term) + "] - fields[" + (fields == null ? "NO_FILTER" : fields.toString()) + "] - filter[" + (filter == null ? "NO_FILTER" : filter.toString()) + "]");
                Date ini = new Date();
                Logger.log(ILoggingLogLevel.Debug, "nativeToAdaptive@" + ini.toString());


                Context context = ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext();
                ContentResolver cr = context.getContentResolver();
                List<Contact> contactList = null;
                String selection = null;
                String[] args = null;
                String[] projection = projectionFromFieldGroup(fields);

                Map<String, Contact> contactsIDs = new HashMap<>();
                Uri uri = null;
                Cursor cursorID = null;
                int cursorLength = 0;
                boolean addressRequired = false, mailRequired = false, phoneRequired = false, error = false;
                String sortOrder = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
                try {


                    if (term != null && filter != null) {
                        //throw new UnsupportedOperationException(this.getClass().getName() + ": androidContactToAdaptive");
                        Logger.log(ILoggingLogLevel.Error, "Error: Wrong Params");
                        error = true;
                    }


                    if (contact != null) {
                        selection = ContactsContract.Contacts._ID + " = ?";
                        args = new String[]{contact.getContactId()};
                    } else if (term != null) {
                        selection = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " LIKE ?";
                        args = new String[]{"%" + term + "%"};
                    } else if(fields != null){

                    }else if (filter != null) {
                        for (IContactFilter aFilter : filter) {
                            if (aFilter.equals(IContactFilter.HasAddress)) {
                                Logger.log(ILoggingLogLevel.Debug, APIService, "Filter: HAS_ADDRESS");
                                selection = (selection == null ? "" : " AND ") + ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS + " IS NOT NULL ";
                                addressRequired = true;
                            }
                            if (aFilter.equals(IContactFilter.HasEmail)) {
                                Logger.log(ILoggingLogLevel.Debug, APIService, "Filter: HAS_EMAIL");
                                selection = (selection == null ? "" : " AND ") + ContactsContract.CommonDataKinds.Email.ADDRESS + " NOT LIKE ''";

                                mailRequired = true;
                            }
                            if (aFilter.equals(IContactFilter.HasPhone)) {
                                Logger.log(ILoggingLogLevel.Debug, APIService, "Filter: HAS_PHONE");
                                selection = (selection == null ? "" : " AND ") + ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL ";
                                phoneRequired = true;
                            }


                        }
                    }
                    //THIS PRESELECTION JUST ADD OVERLOAD
            /*uri = ContactsContract.Contacts.CONTENT_URI;

            cursorID = cr.query(uri, new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME}, selection, args, sortOrder);
            cursorID.moveToFirst();
            cursorLength = cursorID.getCount();
            //Map<String, Contact> contactsIDs = new HashMap<>();
            String contactsIDsString = "";
            while (!cursorID.isAfterLast()) {
                Long id = cursorID.getLong(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
                contactsIDsString += (contactsIDsString.length() > 0 ? "," : "") + "'" + id + "'";
                cursorID.moveToNext();
            }
            String selectionMultiple = ContactsContract.RawContactsEntity.CONTACT_ID + " IN (" + contactsIDsString + ") ";
            */

                    uri = ContactsContract.RawContactsEntity.CONTENT_URI;

                    // querying the content resolver for all contacts

                    cursorID = cr.query(uri, projection,
                            selection, args, sortOrder);
                    cursorID.moveToFirst();
                    while (!cursorID.isAfterLast()) {
                        ContactPhone phones = null;
                        ContactWebsite websites = null;
                        ContactAddress addresses = null;
                        ContactEmail emails = null;
                        String WEBSITE,
                                FAMILY_NAME = null,
                                MIDDLE_NAME = null,
                                DISPLAY_NAME = null,
                                PREFIX = null,
                                JOB = null,
                                JOB_TITLE = null,
                                COMPANY = null;
                        Long id = null;
                        id = cursorID.getLong(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
                        Contact contactBean = contactsIDs.get(String.valueOf(id));
                        if (contactBean == null) {
                            contactBean = new Contact(id.toString());
                        }

                        int columnIndex_MIMETYPE = cursorID.getColumnIndex(ContactsContract.Data.MIMETYPE);

                        String type = cursorID.getString(columnIndex_MIMETYPE);

                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                                ContactPhoneType contactPhoneType = null;
                                int phoneType = cursorID.getInt(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                switch (phoneType) {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                        contactPhoneType = ContactPhoneType.Home;
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                        contactPhoneType = ContactPhoneType.Mobile;
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                        contactPhoneType = ContactPhoneType.Work;
                                        break;
                                    default:
                                        break;
                                }
                                phones = new ContactPhone(cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), contactPhoneType);
                                break;
                            case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                                ContactAddressType contactAddressType = null;
                                int addressType = cursorID.getInt(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                                switch (addressType) {
                                    case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM:
                                    case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
                                        contactAddressType = ContactAddressType.Home;
                                        break;
                                    case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
                                        contactAddressType = ContactAddressType.Work;
                                        break;
                                    case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER:
                                        contactAddressType = ContactAddressType.Other;
                                        break;
                                    default:
                                        break;
                                }
                                addresses = new ContactAddress(cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)), contactAddressType);
                                break;
                            case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                                ContactEmailType contactEmailType = null;
                                int emailType = cursorID.getInt(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                                switch (emailType) {
                                    case ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM:
                                        contactEmailType = ContactEmailType.Other;
                                        break;
                                    case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                                        contactEmailType = ContactEmailType.Other;
                                        break;
                                    case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                                        contactEmailType = ContactEmailType.Other;
                                        break;
                                    case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
                                        contactEmailType = ContactEmailType.Other;
                                        break;
                                    default:
                                        break;
                                }
                                emails = new ContactEmail(contactEmailType,
                                        Boolean.valueOf(cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Email.IS_PRIMARY))),
                                        cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                                break;
                            case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                                WEBSITE = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                                websites = new ContactWebsite(WEBSITE);
                                break;
                            case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                                DISPLAY_NAME = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                                FAMILY_NAME = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                                MIDDLE_NAME = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));
                                PREFIX = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PREFIX));
                                break;
                            case ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE:
                                JOB = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION));
                                COMPANY = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));
                                JOB_TITLE = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));

                                break;
                        }

                        if (contactsIDs != null && contactsIDs.containsKey(id)) {
                            contactBean = contactsIDs.get(id);
                        }
                        // filling the bean
                        if (contactBean != null) {

                            //Emails
                            if (emails != null) {
                                ContactEmail[] array = contactBean.getContactEmails();
                                if (array == null) {
                                    array = new ContactEmail[0];
                                }
                                contactBean.setContactEmails(addElement(array, emails));
                            }
                            //Phones
                            if (phones != null) {
                                ContactPhone[] array = contactBean.getContactPhones();
                                if (array == null) {
                                    array = new ContactPhone[0];
                                }
                                contactBean.setContactPhones(addElement(array, phones));
                            }
                            //Addresses
                            if (addresses != null) {
                                ContactAddress[] array = contactBean.getContactAddresses();
                                if (array == null) {
                                    array = new ContactAddress[0];
                                }
                                contactBean.setContactAddresses(addElement(array, addresses));
                            }
                            //Websites/social
                            if (websites != null) {
                                ContactWebsite[] array = contactBean.getContactWebsites();
                                if (array == null) {
                                    array = new ContactWebsite[0];
                                }
                                contactBean.setContactWebsites(addElement(array, websites));
                            }

                            //PersonalInfo
                            if (DISPLAY_NAME != null) {
                                if (contactBean.getPersonalInfo() == null)
                                    contactBean.setPersonalInfo(new ContactPersonalInfo());
                                contactBean.getPersonalInfo().setName(DISPLAY_NAME);
                            }
                            if (MIDDLE_NAME != null) {
                                if (contactBean.getPersonalInfo() == null)
                                    contactBean.setPersonalInfo(new ContactPersonalInfo());
                                contactBean.getPersonalInfo().setMiddleName(MIDDLE_NAME);
                            }
                            if (FAMILY_NAME != null) {
                                if (contactBean.getPersonalInfo() == null)
                                    contactBean.setPersonalInfo(new ContactPersonalInfo());
                                contactBean.getPersonalInfo().setLastName(FAMILY_NAME);
                            }
                            if (PREFIX != null) {
                                ContactPersonalInfoTitle prefix = getContactTitle(PREFIX);
                                if (contactBean.getPersonalInfo() == null)
                                    contactBean.setPersonalInfo(new ContactPersonalInfo());

                            }
                            //ProfessionalInfo
                            if (JOB != null) {
                                if (contactBean.getProfessionalInfo() == null)
                                    contactBean.setProfessionalInfo(new ContactProfessionalInfo());
                                contactBean.getProfessionalInfo().setJobDescription(JOB);
                            }
                            if (COMPANY != null) {
                                if (contactBean.getProfessionalInfo() == null)
                                    contactBean.setProfessionalInfo(new ContactProfessionalInfo());
                                contactBean.getProfessionalInfo().setCompany(COMPANY);
                            }
                            if (JOB_TITLE != null) {
                                if (contactBean.getPersonalInfo() == null)
                                    contactBean.setPersonalInfo(new ContactPersonalInfo());
                                contactBean.getProfessionalInfo().setJobTitle(JOB_TITLE);
                            }

                        }

                        contactsIDs.put(String.valueOf(id), contactBean);
                        cursorID.moveToNext();


                    }

                } catch (Exception ex) {
                    Logger.log(ILoggingLogLevel.Error, "Error: " + Log.getStackTraceString(ex));
                    error = true;
                } finally {
                    Logger.log(ILoggingLogLevel.Error, "finally clause");
                    if (cursorID != null) {
                        cursorID.close();
                    }
                }


                contactList = new ArrayList<>(contactsIDs.values());

                Logger.log(ILoggingLogLevel.Debug, "nativeToAdaptive", "It tooks " + String.valueOf(new Date().getTime() - ini.getTime()) + "ms retrieving " + contactList.size() + " contacts");

                if (error) {
                    callback.onError(IContactResultCallbackError.WrongParams);
                } else {
                    if (contactList.size() == 0) {
                        callback.onWarning((Contact[]) contactList.toArray(new Contact[contactList.size()]), IContactResultCallbackWarning.NoMatches);
                    } else if (contactList.size() < cursorLength) {
                        callback.onWarning((Contact[]) contactList.toArray(new Contact[contactList.size()]), IContactResultCallbackWarning.LimitExceeded);
                    } else
                        callback.onResult((Contact[]) contactList.toArray(new Contact[contactList.size()]));
                }
            }
        });

    }


    public <APIBean> APIBean[] addElement(APIBean[] a, APIBean e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }


    /**
     * Create a projection from selected fields
     *
     * @param fields to search
     * @return string generated
     */
    private String[] projectionFromFieldGroup(IContactFieldGroup[] fields) {
        List<String> projection = new ArrayList<>();

        String CONTACT_ID = ContactsContract.Contacts._ID;
        String MIMETYPE = ContactsContract.Data.MIMETYPE;

        String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String PHONETYPE = ContactsContract.CommonDataKinds.Phone.TYPE;

        String ADDRESS = ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;
        String ADDRESSTYPE = ContactsContract.CommonDataKinds.StructuredPostal.TYPE;

        String EMAIL_DATA = ContactsContract.CommonDataKinds.Email.ADDRESS;
        String EMAILPRIMARY = ContactsContract.CommonDataKinds.Email.IS_PRIMARY;
        String EMAILTYPE = ContactsContract.CommonDataKinds.Email.TYPE;

        String WEBSITE = ContactsContract.CommonDataKinds.Website.URL;

        String DISPLAY_NAME = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME;
        String MIDDLE_NAME = ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME;
        String FAMILY_NAME = ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME;

        String JOB = ContactsContract.CommonDataKinds.Organization.JOB_DESCRIPTION;
        String COMPANY = ContactsContract.CommonDataKinds.Organization.COMPANY;
        String JOBTITLE = ContactsContract.CommonDataKinds.Organization.TITLE;

        projection.add(CONTACT_ID);
        projection.add(MIMETYPE);

        if (fields != null) {
            for (IContactFieldGroup field : fields) {
                switch (field) {
                    case Addresses:
                        projection.add(ADDRESS);
                        projection.add(ADDRESSTYPE);
                        break;
                    case PersonalInfo:
                        projection.add(DISPLAY_NAME);
                        projection.add(MIDDLE_NAME);
                        projection.add(FAMILY_NAME);
                        break;
                    case Emails:
                        projection.add(EMAIL_DATA);
                        projection.add(EMAILPRIMARY);
                        projection.add(EMAILTYPE);
                        break;
                    case Phones:
                        projection.add(PHONETYPE);
                        projection.add(PHONE_NUMBER);
                        break;
                    case ProfessionalInfo:
                        projection.add(JOB);
                        projection.add(JOBTITLE);
                        projection.add(COMPANY);
                    case Socials:
                        break;
                    case Tags:
                        break;
                    case Websites:
                        projection.add(WEBSITE);
                        break;
                }
            }
        } else {

            projection.add(PHONE_NUMBER);
            projection.add(PHONETYPE);

            projection.add(ADDRESS);
            projection.add(ADDRESSTYPE);

            projection.add(EMAIL_DATA);
            projection.add(EMAILPRIMARY);
            projection.add(EMAILTYPE);

            projection.add(WEBSITE);

            projection.add(DISPLAY_NAME);
            projection.add(MIDDLE_NAME);
            projection.add(FAMILY_NAME);

            projection.add(JOB);
            projection.add(COMPANY);
            projection.add(JOBTITLE);

        }

        return (String[]) projection.toArray(new String[projection.size()]);
    }

    /**
     * @param prefix
     * @return
     */
    private ContactPersonalInfoTitle getContactTitle(String prefix) {
        ContactPersonalInfoTitle result = null;
        if (prefix != null) {
            switch (prefix) {
                case "Mr":
                    result = ContactPersonalInfoTitle.Mr;
                    break;
                case "Ms":
                    result = ContactPersonalInfoTitle.Ms;
                    break;
                case "Mrs":
                    result = ContactPersonalInfoTitle.Mrs;
                    break;
                case "Dr":
                    result = ContactPersonalInfoTitle.Dr;
                    break;
                default:
                    result = ContactPersonalInfoTitle.Unknown;
            }
        }
        return result;
    }


}
/**
 ------------------------------------| Engineered with ? in Barcelona, Catalonia |--------------------------------------
 */
