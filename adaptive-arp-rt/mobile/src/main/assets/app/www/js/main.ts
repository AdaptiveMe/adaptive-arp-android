/// <reference path="adaptive/Adaptive.d.ts" />
/// <reference path="jquery.mobile/jquerymobile.d.ts" />

$(document).ready(function () {

    $('.alert-panel').hide();

    // Initialize text-inputs
    $( "input" ).textinput();

    // Adaptive Bridges
    var os:Adaptive.IOS = Adaptive.AppRegistryBridge.getInstance().getOSBridge();
    var globalization:Adaptive.IGlobalization = Adaptive.AppRegistryBridge.getInstance().getGlobalizationBridge();
    var browser:Adaptive.IBrowser = Adaptive.AppRegistryBridge.getInstance().getBrowserBridge();
    var capabilities:Adaptive.ICapabilities = Adaptive.AppRegistryBridge.getInstance().getCapabilitiesBridge();
    var device:Adaptive.IDevice = Adaptive.AppRegistryBridge.getInstance().getDeviceBridge();
    var contact:Adaptive.IContact = Adaptive.AppRegistryBridge.getInstance().getContactBridge();
    var lifecycle:Adaptive.ILifecycle = Adaptive.AppRegistryBridge.getInstance().getLifecycleBridge();
    var media:Adaptive.IVideo = Adaptive.AppRegistryBridge.getInstance().getVideoBridge();
    var display:Adaptive.IDisplay = Adaptive.AppRegistryBridge.getInstance().getDisplayBridge();

    // Adaptive version
    var version:string = Adaptive.AppRegistryBridge.getInstance().getAPIVersion();
    $('.adaptive-version').html(version);

    // Synchronous Method (getOSInfo)
    var osInfo:Adaptive.OSInfo = os.getOSInfo();
    $('#os-info').html("<b>Operating System</b>: " + osInfo.getVendor() + " " + osInfo.getName() + " " + osInfo.getVersion());

    // Synchronous Method with Parameters (getResourceLiteral)
    var locale:Adaptive.Locale = globalization.getDefaultLocale();
    var i18nResource:string = globalization.getResourceLiteral("hello-world", locale);
    $('#i18n-resource').html("<b>String from Adaptive Core</b>: " + i18nResource);

    // Open Browser
    $('#open-browser').click(function () {
        browser.openInternalBrowser("http://www.google.es", "Google Page", "Back")
    });
    $('#open-external-browser').click(function () {
        browser.openExtenalBrowser("http://www.google.es")
    });
    // Open media
    $('#open-media').click(function () {
        media.playStream("http://html5demos.com/assets/dizzy.mp4");
    });

    // Capabilities
    $('#capabilities').html("<b>Has camera support?</b> " + capabilities.hasMediaSupport(Adaptive.ICapabilitiesMedia.Camera));
    $('#has-orientation-support').html("<b>Has portraitDown capability?</b> " + capabilities.hasOrientationSupport(Adaptive.ICapabilitiesOrientation.PortraitDown));
    $('#get-orientation-default').html("<b>Default orientation</b>: " + capabilities.getOrientationDefault().toString());
    $('#get-orientation-current').html("<b>Current orientation</b>: " + device.getOrientationCurrent().toString());

    $('#get-display-orientation-current').html("<b>Current Display orientation</b>: " + display.getOrientationCurrent().toString());

    var orientations:Adaptive.ICapabilitiesOrientation[] = capabilities.getOrientationsSupported();
    var orientationsTxt:string = "";
    for (var i = 0; i < orientations.length; i++) {
        orientationsTxt += orientations[i];
        if(i < orientations.length - 1){
            orientationsTxt += ", "
        }
    }
    $('#get-orientations-supported').html("<b>Supported orientations</b>: " + orientationsTxt);

    // Device
    var deviceInfo:Adaptive.DeviceInfo = device.getDeviceInfo();
    $('#device').html("<b>Model</b>: " + deviceInfo.getModel() + "<br>" +
    "<b>Name</b>: " + deviceInfo.getName() + "<br>" +
    "<b>Uuid</b>: " + deviceInfo.getUuid() + "<br>" +
    "<b>Vendor</b>: " + deviceInfo.getVendor());


    // Asynchronous Method (callback) (getContacts)
    var callback:Adaptive.IContactResultCallback = new Adaptive.ContactResultCallback(
        function onError(error:Adaptive.IContactResultCallbackError) {
            $('#contacts-error').html("ERROR: " + error.toString()).show();
        },
        function onResult(contacts:Adaptive.Contact[]) {
            parseContacts(contacts);
        },
        function onWarning(contacts:Adaptive.Contact[], warning:Adaptive.IContactResultCallbackWarning) {
            $('#contacts-warning').html("WARNING: " + warning.toString()).show();
            parseContacts(contacts);
        }
    );
    contact.getContactsForFields(callback, [Adaptive.IContactFieldGroup.PersonalInfo, Adaptive.IContactFieldGroup.ProfessionalInfo]);

    function parseContacts(contacts:Adaptive.Contact[]):void {

        for (var i = 0; i < contacts.length; i++) {

            var per:Adaptive.ContactPersonalInfo = contacts[i].getPersonalInfo();
            var pro:Adaptive.ContactProfessionalInfo = contacts[i].getProfessionalInfo();

            $('#contacts-lists').append('<li><a href="#"><h2>' + per.getTitle().toString() + ' ' + per.getName() + ' ' + per.getLastName() + '</h2><p>' + pro.getJobTitle() + ' - ' + pro.getJobDescription() + '</p><p class="ui-li-aside">' + pro.getCompany() + '</p></a></li>');
        }
        $("#contacts-lists").listview('refresh');
    }


    // Asynchronous Method (lifecycleListener) (Lifecycle)
    var lifecycleListener:Adaptive.ILifecycleListener = new Adaptive.LifecycleListener(
        function onError(error:Adaptive.ILifecycleListenerError) {
            $('#lifecycle-error').html("ERROR: " + error.toString()).show();
        },
        function onResult(lifecycle:Adaptive.Lifecycle) {
            printLifecycleEvents(lifecycle);
        },
        function onWarning(lifecycle:Adaptive.Lifecycle, warning:Adaptive.ILifecycleListenerWarning) {
            $('#lifecycle-warning').html("WARNING: " + warning.toString()).show();
            printLifecycleEvents(lifecycle);
        }
    );

    var orientationListener:Adaptive.IDeviceOrientationListener = new Adaptive.DeviceOrientationListener(
        function onError(error:Adaptive.IDeviceOrientationListenerError){},
        function onResult(event:Adaptive.RotationEvent){
            printDeviceOrientationEvents(event);
        },
        function onWarning(event:Adaptive.RotationEvent, warning:Adaptive.IDeviceOrientationListenerWarning){}
    );

    var displayListener:Adaptive.IDisplayOrientationListener = new Adaptive.DisplayOrientationListener(
        function onError(error:Adaptive.IDisplayOrientationListenerError){},
        function onResult(event:Adaptive.RotationEvent){
            printDeviceOrientationEvents(event);
        },
        function onWarning(event:Adaptive.RotationEvent, warning:Adaptive.IDisplayOrientationListenerWarning){}
    );

    function printLifecycleEvents(lifecycle:Adaptive.Lifecycle):void {

        var $textArea = $('#textarea-1');
        $textArea.html($textArea.html() + formatTime(new Date()) + ': ' + lifecycle.getState().toString() + '\n');
    }

    function printDeviceOrientationEvents(event:Adaptive.RotationEvent):void {

        var $textArea = $('#textarea-1');
        $textArea.html($textArea.html() + formatTime(new Date(event.getTimestamp())) + ': ' + event.getOrigin() + ' > ' + event.getDestination() + ' [' + event.getState() + ']\n');
    }

    function formatTime(d):string {

        var h = d.getHours() < 10? '0'+d.getHours(): d.getHours();
        var m = d.getMinutes() < 10? '0'+d.getMinutes(): d.getMinutes();
        var s = d.getSeconds() < 10? '0'+d.getSeconds(): d.getSeconds();

        return h + ':' + m + ':' + s;
    }

    lifecycle.addLifecycleListener(lifecycleListener);
    device.addDeviceOrientationListener(orientationListener);
    display.addDisplayOrientationListener(displayListener);
    //device.removeDeviceOrientationListener(orientationListener);
    //device.removeDeviceOrientationListeners();
    //device.addDeviceOrientationListener(orientationListener);

    /**
     * Utility native log function
     * @param level Level of Logging
     * @param message Message to be logged
     */
    function log(level:Adaptive.ILoggingLogLevel, message:string):void {
        Adaptive.AppRegistryBridge.getInstance().getLoggingBridge().logLevelCategoryMessage(level, "APPLICATION", message);
    }
});

