/// <reference path="adaptive/Adaptive.d.ts" />
/// <reference path="jquery.mobile/jquerymobile.d.ts" />
$(document).ready(function () {
    $('.alert-panel').hide();
    // Initialize text-inputs
    $("input").textinput();
    // Adaptive Bridges
    var os = Adaptive.AppRegistryBridge.getInstance().getOSBridge();
    var globalization = Adaptive.AppRegistryBridge.getInstance().getGlobalizationBridge();
    var browser = Adaptive.AppRegistryBridge.getInstance().getBrowserBridge();
    var capabilities = Adaptive.AppRegistryBridge.getInstance().getCapabilitiesBridge();
    var device = Adaptive.AppRegistryBridge.getInstance().getDeviceBridge();
    var contact = Adaptive.AppRegistryBridge.getInstance().getContactBridge();
    var lifecycle = Adaptive.AppRegistryBridge.getInstance().getLifecycleBridge();
    var media = Adaptive.AppRegistryBridge.getInstance().getVideoBridge();
    var display = Adaptive.AppRegistryBridge.getInstance().getDisplayBridge();
    // Adaptive version
    var version = Adaptive.AppRegistryBridge.getInstance().getAPIVersion();
    $('.adaptive-version').html(version);
    // Synchronous Method (getOSInfo)
    var osInfo = os.getOSInfo();
    $('#os-info').html("<b>Operating System</b>: " + osInfo.getVendor() + " " + osInfo.getName() + " " + osInfo.getVersion());
    // Synchronous Method with Parameters (getResourceLiteral)
    var locale = globalization.getDefaultLocale();
    var i18nResource = globalization.getResourceLiteral("hello-world", locale);
    $('#i18n-resource').html("<b>String from Adaptive Core</b>: " + i18nResource);
    // Open Browser
    $('#open-browser').click(function () {
        browser.openInternalBrowser("http://www.google.es", "Google Page", "Back");
    });
    $('#open-external-browser').click(function () {
        browser.openExtenalBrowser("http://www.google.es");
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
    var orientations = capabilities.getOrientationsSupported();
    var orientationsTxt = "";
    for (var i = 0; i < orientations.length; i++) {
        orientationsTxt += orientations[i];
        if (i < orientations.length - 1) {
            orientationsTxt += ", ";
        }
    }
    $('#get-orientations-supported').html("<b>Supported orientations</b>: " + orientationsTxt);
    // Device
    var deviceInfo = device.getDeviceInfo();
    $('#device').html("<b>Model</b>: " + deviceInfo.getModel() + "<br>" + "<b>Name</b>: " + deviceInfo.getName() + "<br>" + "<b>Uuid</b>: " + deviceInfo.getUuid() + "<br>" + "<b>Vendor</b>: " + deviceInfo.getVendor());
    // Asynchronous Method (callback) (getContacts)
    var callback = new Adaptive.ContactResultCallback(function onError(error) {
        $('#contacts-error').html("ERROR: " + error.toString()).show();
    }, function onResult(contacts) {
        parseContacts(contacts);
    }, function onWarning(contacts, warning) {
        $('#contacts-warning').html("WARNING: " + warning.toString()).show();
        parseContacts(contacts);
    });
    contact.getContactsForFields(callback, [Adaptive.IContactFieldGroup.PersonalInfo, Adaptive.IContactFieldGroup.ProfessionalInfo]);
    function parseContacts(contacts) {
        for (var i = 0; i < contacts.length; i++) {
            var per = contacts[i].getPersonalInfo();
            var pro = contacts[i].getProfessionalInfo();
            $('#contacts-lists').append('<li><a href="#"><h2>' + per.getTitle().toString() + ' ' + per.getName() + ' ' + per.getLastName() + '</h2><p>' + pro.getJobTitle() + ' - ' + pro.getJobDescription() + '</p><p class="ui-li-aside">' + pro.getCompany() + '</p></a></li>');
        }
        $("#contacts-lists").listview('refresh');
    }
    // Asynchronous Method (lifecycleListener) (Lifecycle)
    var lifecycleListener = new Adaptive.LifecycleListener(function onError(error) {
        $('#lifecycle-error').html("ERROR: " + error.toString()).show();
    }, function onResult(lifecycle) {
        printLifecycleEvents(lifecycle);
    }, function onWarning(lifecycle, warning) {
        $('#lifecycle-warning').html("WARNING: " + warning.toString()).show();
        printLifecycleEvents(lifecycle);
    });
    var orientationListener = new Adaptive.DeviceOrientationListener(function onError(error) {
    }, function onResult(event) {
        printDeviceOrientationEvents(event);
    }, function onWarning(event, warning) {
    });
    var displayListener = new Adaptive.DisplayOrientationListener(function onError(error) {
    }, function onResult(event) {
        printDeviceOrientationEvents(event);
    }, function onWarning(event, warning) {
    });
    function printLifecycleEvents(lifecycle) {
        var $textArea = $('#textarea-1');
        $textArea.html($textArea.html() + formatTime(new Date()) + ': ' + lifecycle.getState().toString() + '\n');
    }
    function printDeviceOrientationEvents(event) {
        var $textArea = $('#textarea-1');
        $textArea.html($textArea.html() + formatTime(new Date(event.getTimestamp())) + ': ' + event.getOrigin() + ' > ' + event.getDestination() + ' [' + event.getState() + ']\n');
    }
    function formatTime(d) {
        var h = d.getHours() < 10 ? '0' + d.getHours() : d.getHours();
        var m = d.getMinutes() < 10 ? '0' + d.getMinutes() : d.getMinutes();
        var s = d.getSeconds() < 10 ? '0' + d.getSeconds() : d.getSeconds();
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
    function log(level, message) {
        Adaptive.AppRegistryBridge.getInstance().getLoggingBridge().logLevelCategoryMessage(level, "APPLICATION", message);
    }
});
//# sourceMappingURL=main.js.map