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

package me.adaptive.arp.impl.PIM;

import me.adaptive.arp.api.ContactUid;
import me.adaptive.arp.api.IContact;
import me.adaptive.arp.api.IContactResultCallback;

public class ContactImpl implements IContact {

    @Override
    public void searchContacts(String term, IContactResultCallback callback) {

    }

    @Override
    public void searchContacts(String term, IContactResultCallback callback, Filter... filter) {

    }

    @Override
    public void getContact(ContactUid contact, IContactResultCallback callback) {

    }

    @Override
    public void getContactPhoto(ContactUid contact, IContactResultCallback callback) {

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
