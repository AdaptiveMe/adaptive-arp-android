package me.adaptive.arp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Date;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.Contact;
import me.adaptive.arp.api.ContactEmail;
import me.adaptive.arp.api.IAdaptiveRPGroup;
import me.adaptive.arp.api.ICapabilitiesOrientation;
import me.adaptive.arp.api.IContactFilter;
import me.adaptive.arp.api.IContactResultCallback;
import me.adaptive.arp.api.IContactResultCallbackError;
import me.adaptive.arp.api.IContactResultCallbackWarning;
import me.adaptive.arp.api.IDeviceOrientationListener;
import me.adaptive.arp.api.IDisplayOrientationListener;
import me.adaptive.arp.api.ILifecycleListener;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.Lifecycle;
import me.adaptive.arp.api.LifecycleState;
import me.adaptive.arp.api.RotationEvent;
import me.adaptive.arp.api.RotationEventState;
import me.adaptive.arp.impl.AccelerationDelegate;
import me.adaptive.arp.impl.AppContextDelegate;
import me.adaptive.arp.impl.BrowserDelegate;
import me.adaptive.arp.impl.CapabilitiesDelegate;
import me.adaptive.arp.impl.ContactDelegate;
import me.adaptive.arp.impl.DatabaseDelegate;
import me.adaptive.arp.impl.DeviceDelegate;
import me.adaptive.arp.impl.DisplayDelegate;
import me.adaptive.arp.impl.GeolocationDelegate;
import me.adaptive.arp.impl.GlobalizationDelegate;
import me.adaptive.arp.impl.LifecycleDelegate;
import me.adaptive.arp.impl.LoggingDelegate;
import me.adaptive.arp.impl.MailDelegate;
import me.adaptive.arp.impl.MessagingDelegate;
import me.adaptive.arp.impl.NetworkReachabilityDelegate;
import me.adaptive.arp.impl.NetworkStatusDelegate;
import me.adaptive.arp.impl.OSDelegate;
import me.adaptive.arp.impl.RuntimeDelegate;
import me.adaptive.arp.impl.SecurityDelegate;
import me.adaptive.arp.impl.ServiceDelegate;
import me.adaptive.arp.impl.TelephonyDelegate;
import me.adaptive.arp.impl.VideoDelegate;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;


public class MainActivity extends Activity {

    OrientationEventListener orientationEventListener;
    ILogging Logger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        AppRegistryBridge.getInstance().getPlatformContext().setDelegate(new AppContextDelegate(this));

        AppRegistryBridge.getInstance().getLoggingBridge().setDelegate(new LoggingDelegate());
        AppRegistryBridge.getInstance().getAccelerationBridge().setDelegate(new AccelerationDelegate());
        AppRegistryBridge.getInstance().getBrowserBridge().setDelegate(new BrowserDelegate());
        AppRegistryBridge.getInstance().getCapabilitiesBridge().setDelegate(new CapabilitiesDelegate());
        AppRegistryBridge.getInstance().getContactBridge().setDelegate(new ContactDelegate());
        AppRegistryBridge.getInstance().getDatabaseBridge().setDelegate(new DatabaseDelegate());
        AppRegistryBridge.getInstance().getDeviceBridge().setDelegate(new DeviceDelegate());
        AppRegistryBridge.getInstance().getGeolocationBridge().setDelegate(new GeolocationDelegate());
        AppRegistryBridge.getInstance().getGlobalizationBridge().setDelegate(new GlobalizationDelegate());
        AppRegistryBridge.getInstance().getLifecycleBridge().setDelegate(new LifecycleDelegate());
        AppRegistryBridge.getInstance().getMailBridge().setDelegate(new MailDelegate());
        AppRegistryBridge.getInstance().getMessagingBridge().setDelegate(new MessagingDelegate());
        AppRegistryBridge.getInstance().getNetworkReachabilityBridge().setDelegate(new NetworkReachabilityDelegate());
        AppRegistryBridge.getInstance().getNetworkStatusBridge().setDelegate(new NetworkStatusDelegate());
        AppRegistryBridge.getInstance().getOSBridge().setDelegate(new OSDelegate());
        AppRegistryBridge.getInstance().getRuntimeBridge().setDelegate(new RuntimeDelegate());
        AppRegistryBridge.getInstance().getSecurityBridge().setDelegate(new SecurityDelegate());
        AppRegistryBridge.getInstance().getServiceBridge().setDelegate(new ServiceDelegate());
        AppRegistryBridge.getInstance().getTelephonyBridge().setDelegate(new TelephonyDelegate());
        AppRegistryBridge.getInstance().getVideoBridge().setDelegate(new VideoDelegate());

        Logger = AppRegistryBridge.getInstance().getLoggingBridge();



        Logger.log(ILoggingLogLevel.Debug, "MainActivity", "TEST");


        Button button = (Button) findViewById(R.id.buttonTC);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IContactResultCallback cb = new IContactResultCallback() {
                    @Override
                    public void onError(IContactResultCallbackError error) {
                        Logger.log(ILoggingLogLevel.Error, error.toString());
                    }

                    @Override
                    public void onResult(Contact[] contacts) {
                        System.out.println("SYSO");
                        if(Logger != null) {
                            Log.d("Native",contacts.length +" contacts");
                            Logger.log(ILoggingLogLevel.Debug, "MainActivity Size: " + contacts.length);
                           for (Contact contact : contacts) {
                                Logger.log(ILoggingLogLevel.Debug, "MainActivity ID RETURNED: " + contact.getContactId());
                                /*ContactPhone[] phone = contact.getContactPhones();
                                if(phone != null && phone.length> 0){
                                    for (ContactPhone phon : phone) {
                                        Logger.log(ILoggingLogLevel.DEBUG, "MainActivity PHONE RETURNED: " + phon.getPhone());
                                    }
                                }*/

                           }
                        }else{
                            Log.e("Native","no log "+ contacts.length +" contacts");
                        }

                    }

                    @Override
                    public void onWarning(Contact[] contacts, IContactResultCallbackWarning warning) {
                        AppRegistryBridge.getInstance().getLoggingBridge().log(ILoggingLogLevel.Warn, warning.toString());
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
                AppRegistryBridge.getInstance().getContactBridge().getContacts(cb);
            }
        });


        Button button2 = (Button) findViewById(R.id.buttonTC2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IContactResultCallback cb = new IContactResultCallback() {
                    @Override
                    public void onError(IContactResultCallbackError error) {
                        Logger.log(ILoggingLogLevel.Error, error.toString());
                    }

                    @Override
                    public void onResult(Contact[] contacts) {
                        System.out.println("SYSO");
                        if(Logger != null) {
                            Log.d("Native",contacts.length +" contacts");
                            Logger.log(ILoggingLogLevel.Debug, "MainActivity Size: " + contacts.length);
                            for (Contact contact : contacts) {
                                Logger.log(ILoggingLogLevel.Debug, "MainActivity ID RETURNED: " + contact.getContactId());
                                /*ContactPhone[] phone = contact.getContactPhones();
                                if(phone != null && phone.length> 0){
                                    for (ContactPhone phon : contact.getContactPhones()) {
                                        Logger.log(ILoggingLogLevel.DEBUG, "MainActivity PHONE RETURNED: " + phon.getPhone());
                                    }
                                }*/
                                ContactEmail[] email = contact.getContactEmails();
                                if(email != null && email.length> 0){
                                    for (ContactEmail mail : email) {
                                        Logger.log(ILoggingLogLevel.Debug, "MainActivity MAIL RETURNED: " + mail.getEmail());
                                    }
                                }

                            }
                        }else{
                            Log.e("Native","no log "+ contacts.length +" contacts");
                        }

                    }

                    @Override
                    public void onWarning(Contact[] contacts, IContactResultCallbackWarning warning) {
                        AppRegistryBridge.getInstance().getLoggingBridge().log(ILoggingLogLevel.Warn, warning.toString());
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
                //AppRegistryBridge.getInstance().getContactBridge().searchContacts("Kmail",cb);
                //AppRegistryBridge.getInstance().getContactBridge().getContact(new ContactUid("4331"),cb);
                /* DOES NOT WORK YET
                AppRegistryBridge.getInstance().getContactBridge().getContactsForFields(cb,new IContactFieldGroup[]{

                    IContactFieldGroup.EMAILS
                });
                 */
                AppRegistryBridge.getInstance().getContactBridge().getContactsWithFilter(cb,null,new IContactFilter[]{
                        IContactFilter.HasEmail
                });
            }
        });

        orientationEventListener = new OrientationEventListener(getApplicationContext(), SensorManager.SENSOR_DELAY_UI) {
            public void onOrientationChanged(int orientation) {

                if (AppRegistryBridge.getInstance().getDisplayBridge().getDelegate() != null) {
                    for (IDisplayOrientationListener listener : ((DisplayDelegate) AppRegistryBridge
                            .getInstance().getDisplayBridge().getDelegate()).listeners) {
                        listener.onResult(new RotationEvent(ICapabilitiesOrientation.Unknown, getOrientation(orientation), RotationEventState.Unknown,new Date().getTime()));
                    }
                }

                ICapabilitiesOrientation state;
                final int rotation = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
                switch (rotation) {
                    case Surface.ROTATION_0:
                        state = ICapabilitiesOrientation.PortraitUp;
                    case Surface.ROTATION_90:
                        state = ICapabilitiesOrientation.LandscapeRight;
                    case Surface.ROTATION_180:
                        state = ICapabilitiesOrientation.PortraitDown;
                    case Surface.ROTATION_270:
                        state = ICapabilitiesOrientation.LandscapeLeft;
                    default:
                        state = ICapabilitiesOrientation.Unknown;
                }
                if(AppRegistryBridge.getInstance().getDeviceBridge().getDelegate() != null){
                    for(IDeviceOrientationListener listener : ((DeviceDelegate)AppRegistryBridge.getInstance().getDeviceBridge().getDelegate()).listenersOrientation){
                        listener.onResult(new RotationEvent(null, state,RotationEventState.DidFinishRotation,new Date().getTime()));
                    }
                }
            }
        };
        if(orientationEventListener.canDetectOrientation()) orientationEventListener.enable();

        LifecicleUpdate(LifecycleState.Started);
        LifecicleUpdate(LifecycleState.Running);
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first

        LifecicleUpdate(LifecycleState.Started);
    }

    private void LifecicleUpdate(LifecycleState state) {
        if(AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate() != null){
            for(ILifecycleListener listener : ((LifecycleDelegate)AppRegistryBridge.getInstance().getLifecycleBridge().getDelegate()).listeners){
                listener.onResult(new Lifecycle(LifecycleState.Resuming));
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        LifecicleUpdate(LifecycleState.Running);


    }

    @Override
    public void onResume(){
        super.onResume();
        if(orientationEventListener.canDetectOrientation()) orientationEventListener.enable();
        LifecicleUpdate(LifecycleState.Resuming);
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        LifecicleUpdate(LifecycleState.Pausing);

        orientationEventListener.disable();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        /* Save the user's current game state
        savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
        savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);*/

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        /* Restore state members from saved instance
        mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
        mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);*/
    }

    @Override
    public void onStop() {
        super.onPause();  // Always call the superclass method first
        LifecicleUpdate(LifecycleState.Stopping);
        orientationEventListener.disable();
    }

    @Override
    public void onDestroy() {
        super.onPause();  // Always call the superclass method first

        LifecicleUpdate(LifecycleState.Stopping);
        orientationEventListener.disable();
    }

    private ICapabilitiesOrientation getOrientation(int orientation) {
        switch(orientation){
            case ORIENTATION_LANDSCAPE:
                return ICapabilitiesOrientation.PortraitUp;
            case ORIENTATION_PORTRAIT:
                return ICapabilitiesOrientation.LandscapeLeft;
        }
        return ICapabilitiesOrientation.Unknown;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
