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
import me.adaptive.arp.api.BasePIMDelegate;
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
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;

/**
 * Interface for Managing the Contact operations
 * Auto-generated implementation of IContact specification.
 */
public class ContactDelegate extends BasePIMDelegate implements IContact {

    // logger
    private static final String LOG_TAG = "ContactDelegate";
    private ILogging logger;

    // Context
    private Context context;

    /**
     * Default Constructor.
     */
    public ContactDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
    }

    //TODO ADD TO COMMON
    /**
     * Cast from InputStream to byte[]
     *
     * @param is InputStream
     * @return byte[]
     */
    public static byte[] getBytesFromInputStream(InputStream is) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = is.read(buffer)) != -1; )
                os.write(buffer, 0, len);

            os.flush();

            return os.toByteArray();
        } catch (IOException e) {
            return null;
        }
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
        if (contact == null) {
            callback.onError(IContactPhotoResultCallbackError.WrongParams);
            return;
        }
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contact.getContactId()));
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd = context.
                    getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            InputStream image = fd.createInputStream();
            byte[] bytes = getBytesFromInputStream(image);
            if (bytes == null) {
                callback.onResult(bytes);
                return;
            }
        } catch (IOException e) {
            logger.log(ILoggingLogLevel.Error, "Error getting the photo: " + Log.getStackTraceString(e));
            callback.onError(IContactPhotoResultCallbackError.Unknown);
        }
        callback.onError(IContactPhotoResultCallbackError.Unknown);
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
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "setContactPhoto Error: " + Log.getStackTraceString(ex));
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

                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "androidContactToAdaptive contactID[" + (contact == null ? "NO_ID" : contact.getContactId()) + "] - term[" + (term == null ? "NO_TERM" : term) + "] - fields[" + (fields == null ? "NO_FILTER" : fields.toString()) + "] - filter[" + (filter == null ? "NO_FILTER" : filter.toString()) + "]");
                Date ini = new Date();
                logger.log(ILoggingLogLevel.Debug, "nativeToAdaptive@" + ini.toString());



                ContentResolver cr = context.getContentResolver();
                List<Contact> contactList = null;
                String selection = null;
                String[] args = null;

                Map<String, Contact> contactsIDs = new HashMap<>();
                Uri uri = null;
                Cursor cursorID = null;
                int cursorLength = 0;
                boolean addressRequired = false, mailRequired = false, phoneRequired = false, address = false, personalInfo = false, professionalInfo = false, socials = false, websites = false, email = false, phones = false, error = false;
                String sortOrder = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
                try {

                    if (term != null && filter != null) {
                        //throw new UnsupportedOperationException(this.getClass().getName() + ": androidContactToAdaptive");
                        logger.log(ILoggingLogLevel.Error, "Error: Wrong Params");
                        error = true;
                    }


                    if (contact != null) {
                        selection = ContactsContract.Contacts._ID + " = ?";
                        args = new String[]{contact.getContactId()};
                    } else if (term != null) {
                        selection = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " LIKE ?";
                        args = new String[]{"%" + term + "%"};
                    }

                    uri = ContactsContract.RawContactsEntity.CONTENT_URI;

                    // querying the content resolver for all contacts
                    cursorID = cr.query(uri, null,
                            selection, args, sortOrder);
                    cursorID.moveToFirst();
                    while (!cursorID.isAfterLast()) {
                        ContactPhone contactPhones = null;
                        ContactWebsite contactWebsites = null;
                        ContactAddress contactAddresses = null;
                        ContactEmail contactEmails = null;
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
                                contactPhones = new ContactPhone(cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), contactPhoneType);
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
                                contactAddresses = new ContactAddress(cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)), contactAddressType);
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
                                contactEmails = new ContactEmail(contactEmailType,
                                        Boolean.valueOf(cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Email.IS_PRIMARY))),
                                        cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                                break;
                            case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                                WEBSITE = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                                contactWebsites = new ContactWebsite(WEBSITE);
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
                            if (contactEmails != null) {
                                ContactEmail[] array = contactBean.getContactEmails();
                                if (array == null) {
                                    array = new ContactEmail[0];
                                }
                                contactBean.setContactEmails(addElement(array, contactEmails));
                            }
                            //Phones
                            if (contactPhones != null) {
                                ContactPhone[] array = contactBean.getContactPhones();
                                if (array == null) {
                                    array = new ContactPhone[0];
                                }
                                contactBean.setContactPhones(addElement(array, contactPhones));
                            }
                            //Addresses
                            if (contactAddresses != null) {
                                ContactAddress[] array = contactBean.getContactAddresses();
                                if (array == null) {
                                    array = new ContactAddress[0];
                                }
                                contactBean.setContactAddresses(addElement(array, contactAddresses));
                            }
                            //Websites/social
                            if (contactWebsites != null) {
                                ContactWebsite[] array = contactBean.getContactWebsites();
                                if (array == null) {
                                    array = new ContactWebsite[0];
                                }
                                contactBean.setContactWebsites(addElement(array, contactWebsites));
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
                    logger.log(ILoggingLogLevel.Error, "Error: " + Log.getStackTraceString(ex));
                    error = true;
                } finally {
                    logger.log(ILoggingLogLevel.Error, "finally clause");
                    if (cursorID != null) {
                        cursorID.close();
                    }
                }

                if (filter != null) {
                    for (IContactFilter aFilter : filter) {
                        contactList = new ArrayList<>(contactsIDs.values());
                        Date test1 = new Date();
                        logger.log(ILoggingLogLevel.Debug, "Prefilter: " + contactsIDs.size());

                        for (Contact contact2 : contactList) {
                            if (aFilter.equals(IContactFilter.HasEmail) && contact2.getContactEmails() == null) {
                                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "Filter: HasEmail");
                                contactsIDs.remove(contact2.getContactId());
                            }
                            if (aFilter.equals(IContactFilter.HasAddress) && contact2.getContactAddresses() == null) {
                                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "Filter: HasAddress");
                                contactsIDs.remove(contact2.getContactId());
                            }
                            if (aFilter.equals(IContactFilter.HasPhone) && contact2.getContactPhones() == null) {
                                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "Filter: HasPhone");
                                contactsIDs.remove(contact2.getContactId());
                            }
                        }
                       logger.log(ILoggingLogLevel.Debug, (new Date().getTime() - test1.getTime()) + "ms - Postfilter: " + contactsIDs.size());
                    }
                }

                if (fields != null) {
                    contactList = new ArrayList<>(contactsIDs.values());
                    Date test1 = new Date();
                    logger.log(ILoggingLogLevel.Debug, "Prefilter: " + contactsIDs.size());

                    for (Contact contact2 : contactList) {

                        if (Arrays.binarySearch(fields, IContactFieldGroup.Addresses) < 0) {
                            //logger.log(ILoggingLogLevel.Debug, LOG_TAG, "NO Fields: Addresses");
                            contact2.setContactAddresses(null);
                        }
                        if (Arrays.binarySearch(fields, IContactFieldGroup.Emails) < 0) {
                            //logger.log(ILoggingLogLevel.Debug, LOG_TAG, "NO Fields: Emails");
                            contact2.setContactEmails(null);
                        }
                        if (Arrays.binarySearch(fields, IContactFieldGroup.PersonalInfo) < 0) {
                            //logger.log(ILoggingLogLevel.Debug, LOG_TAG, "NO Fields: PersonalInfo");
                            contact2.setPersonalInfo(null);
                        }
                        if (Arrays.binarySearch(fields, IContactFieldGroup.Phones) < 0) {
                            //logger.log(ILoggingLogLevel.Debug, LOG_TAG, "NO Fields: Phones");
                            contact2.setContactPhones(null);
                        }
                        if (Arrays.binarySearch(fields, IContactFieldGroup.ProfessionalInfo) < 0) {
                            //logger.log(ILoggingLogLevel.Debug, LOG_TAG, "NO Fields: ProfessionalInfo");
                            contact2.setProfessionalInfo(null);
                        }
                        if (Arrays.binarySearch(fields, IContactFieldGroup.Socials) < 0) {
                            //logger.log(ILoggingLogLevel.Debug, LOG_TAG, "NO Fields: Socials");
                            contact2.setContactSocials(null);
                        }
                        if (Arrays.binarySearch(fields, IContactFieldGroup.Websites) < 0) {
                            //logger.log(ILoggingLogLevel.Debug, LOG_TAG, "NO Fields: Websites");
                            contact2.setContactWebsites(null);
                        }
                        contactsIDs.put(contact2.getContactId(), contact2);
                    }

                    logger.log(ILoggingLogLevel.Debug, (new Date().getTime() - test1.getTime()) + "ms - Postfields: " + contactsIDs.size());
                }


                contactList = new ArrayList<>(contactsIDs.values());

                logger.log(ILoggingLogLevel.Debug, "nativeToAdaptive", "It tooks " + String.valueOf(new Date().getTime() - ini.getTime()) + "ms retrieving " + contactList.size() + " contacts");

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

    //TODO MOVE TO COMMON
    public <APIBean> APIBean[] addElement(APIBean[] a, APIBean e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    //TODO DOCUMENT
    /**
     *
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
