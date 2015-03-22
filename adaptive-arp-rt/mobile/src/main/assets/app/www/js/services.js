/// <reference path="adaptive/Adaptive.d.ts" />
/// <reference path="jquery.mobile/jquerymobile.d.ts" />
/*setTimeout(function () {
    $('#textarea-2').css({
        'height': 'auto'
    });
}, 100);*/
function printServicesEvents(response) {
    var $textArea = $('#textarea-2');
    $textArea.html($textArea.html() + response.getContent());
}
$(document).ready(function () {
    // Service Bridge
    var service = Adaptive.AppRegistryBridge.getInstance().getServiceBridge();
    // Services Token
    //var pet:Adaptive.ServiceToken = service.getServiceTokenByUri('http://petstore.swagger.wordnik.com/api/pet/15');
    /*var updatePet:Adaptive.ServiceToken = service.getServiceToken(
        "petstore",
        "http://petstore.swagger.wordnik.com",
        "/api/pet/50",
        Adaptive.IServiceMethod.POST);*/
    var geonames = service.getServiceTokenByUri('http://api.geonames.org/postalCodeLookupJSON');
    // Get the request
    var req = service.getServiceRequest(geonames);
    //var req2:Adaptive.ServiceRequest = service.getServiceRequest(pet);
    var params = [];
    params.push(new Adaptive.ServiceRequestParameter("postalcode", "6600"));
    params.push(new Adaptive.ServiceRequestParameter("country", "AT"));
    params.push(new Adaptive.ServiceRequestParameter("username", "demo"));
    req.setQueryParameters(params);
    // Prepare the callback
    var callback = new Adaptive.ServiceResultCallback(function onError(error) {
        $('#services-error').html("ERROR: " + error.toString()).show();
    }, function onResult(result) {
        printServicesEvents(result);
    }, function onWarning(result, warning) {
        $('#services-warning').html("WARNING: " + warning.toString()).show();
        printServicesEvents(result);
    });
    // Invoke the service
    service.invokeService(req, callback);
    //$('#textarea-2').html($('#textarea-2').html() + "<br>");
    //service.invokeService(req2, callback);
});
//# sourceMappingURL=services.js.map