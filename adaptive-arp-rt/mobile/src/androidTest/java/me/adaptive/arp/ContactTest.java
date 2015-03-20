package me.adaptive.arp;/*
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

import android.os.AsyncTask;
import android.test.InstrumentationTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseCallbackImpl;
import me.adaptive.arp.api.Contact;
import me.adaptive.arp.api.ContactBridge;
import me.adaptive.arp.api.IAdaptiveRPGroup;
import me.adaptive.arp.api.IContactResultCallback;
import me.adaptive.arp.api.IContactResultCallbackError;
import me.adaptive.arp.api.IContactResultCallbackWarning;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.LoggingBridge;
import me.adaptive.arp.impl.AppContextDelegate;
import me.adaptive.arp.impl.ContactDelegate;
import me.adaptive.arp.impl.LoggingDelegate;

public class ContactTest extends InstrumentationTestCase {
    private MainActivity activity;

    private LoggingBridge Logger = AppRegistryBridge.getInstance().getLoggingBridge();
    private ContactBridge contactBridge  = AppRegistryBridge.getInstance().getContactBridge();
    public ContactTest() {
        setName("MainActivity");

    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        AppRegistryBridge.getInstance().getPlatformContext().setDelegate(new AppContextDelegate(activity, Executors.newSingleThreadExecutor()));
        Logger.setDelegate(new LoggingDelegate());
        contactBridge.setDelegate(new ContactDelegate());

    }

    public void testGetContacts() throws Exception {
        ContactTestCallback cb = new ContactTestCallback(0);
        contactBridge.getContacts(cb);
        Logger.log(ILoggingLogLevel.Debug, "TESTING", " Hello contacts");
       System.out.println("Hello contacts");
        //assertTrue("fail ffs",false);
    }

    private class ContactTestCallback extends BaseCallbackImpl implements IContactResultCallback {

        private ContactTestCallback(long id) {
            super(id);
        }

        @Override
        public void onError(IContactResultCallbackError error) {

            Logger.log(ILoggingLogLevel.Error, "TESTING", error.toString());
            assertTrue("There is an error getting contacts: " + error.toString(), false);
        }

        @Override
        public void onResult(Contact[] contacts) {
            //assertEquals(contacts.length,1031);
            assertTrue("fail ffs",false);
            //assertNotNull(contacts);
            Logger.log(ILoggingLogLevel.Debug, "TESTING", String.valueOf(contacts.length)+" contacts");
            assertTrue("There are no contacts", contacts.length < 0);
        }

        @Override
        public void onWarning(Contact[] contacts, IContactResultCallbackWarning warning) {
            Logger.log(ILoggingLogLevel.Warn, "TESTING", warning.toString());
            //assertNotNull(contacts);
            Logger.log(ILoggingLogLevel.Debug, "TESTING", String.valueOf(contacts.length)+" contacts");
            //assertTrue("There are no contacts", contacts.length < 0);

        }
    }

    public void testSearchContacts() throws Exception,Throwable {
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                IContactResultCallback cb = new IContactResultCallback() {
                    @Override
                    public void onWarning(Contact[] contacts, IContactResultCallbackWarning warning) {

                    }

                    @Override
                    public void onError(IContactResultCallbackError error) {

                    }

                    @Override
                    public void onResult(Contact[] contacts) {
                        assertEquals(2,contacts.length);
                    }

                    @Override
                    public IAdaptiveRPGroup getAPIGroup() {
                        return null;
                    }

                    @Override
                    public String getAPIVersion() {
                        return null;
                    }
                };
                contactBridge.searchContacts("Kmail", cb);
            }
        });



    }

    /**
     * This demonstrates how to test AsyncTasks in android JUnit. Below I used
     * an in line implementation of a asyncTask, but in real life you would want
     * to replace that with some task in your application.
     * @throws Throwable
     */
    public void testSomeAsynTask () throws Throwable {
        // create  a signal to let us know when our task is done.
        final CountDownLatch signal = new CountDownLatch(1);

    /* Just create an in line implementation of an asynctask. Note this
     * would normally not be done, and is just here for completeness.
     * You would just use the task you want to unit test in your project.
     */
        final AsyncTask<String, Void, String> myTask = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                //Do something meaningful.
                return "something happened!";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

            /* This is the key, normally you would use some type of listener
             * to notify your activity that the async call was finished.
             *
             * In your test method you would subscribe to that and signal
             * from there instead.
             */
                signal.countDown();
            }
        };

        // Execute the async task on the UI thread! THIS IS KEY!
        runTestOnUiThread(new Runnable() {

            @Override
            public void run() {
                myTask.execute("Do something");
            }
        });

    /* The testing thread will wait here until the UI thread releases it
     * above with the countDown() or 30 seconds passes and it times out.
     */
        signal.await(30, TimeUnit.SECONDS);

        // The task is done, and now you can assert some things!
        assertTrue("Happiness", true);
    }
}
