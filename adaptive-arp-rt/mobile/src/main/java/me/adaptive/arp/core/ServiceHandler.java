package me.adaptive.arp.core;

import me.adaptive.arp.api.APIBridge;
import me.adaptive.arp.api.APIRequest;
import me.adaptive.arp.api.APIResponse;
import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.impl.AppContextDelegate;

/**
 * Service Handler for Adaptive Native calls
 */
public class ServiceHandler {

    // Logger
    private static final String LOG_TAG = "WebViewClient";
    private static ILogging logger;

    // Singleton instance
    private static ServiceHandler instance = null;

    public static ServiceHandler getInstance() {
        if (instance == null) {
            instance = new ServiceHandler();
            logger = AppRegistryBridge.getInstance().getLoggingBridge();
        }
        return instance;
    }

    /**
     * Method that executes the method with the parameters from the APIRequest object send it by the
     * Typescript. This method could return some data in the syncronous methods or execute some
     * callback/listeners in the asyncronous ones
     *
     * @param request API Request object
     * @return Data for returning the syncronous responses
     */
    public APIResponse handleServiceUrl(APIRequest request) {

        String bridgeType = request.getBridgeType();

        if (bridgeType != null && !bridgeType.isEmpty()) {

            final APIBridge bridge = AppRegistryBridge.getInstance().getBridge(bridgeType);

            if (bridge != null) {

                if (request.getAsyncId() != -1) {

                    // ASYNCHRONOUS METHODS

                    final APIRequest asyncRequest = request;

                    ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getExecutor().submit(new Runnable() {
                        @Override
                        public void run() {
                            bridge.invoke(asyncRequest);
                        }
                    });
                    return new APIResponse("", 200, "Please see native platform log for details.");


                } else {

                    // SYNCHRONOUS METHODS

                    APIResponse response = bridge.invoke(request);
                    if (response != null) {
                        return response;
                    } else {
                        logger.log(ILoggingLogLevel.Error, LOG_TAG, "There is an error executing the synchronous method: " + request.getMethodName());
                        return new APIResponse("", 400, "There is an error executing the synchronous method: " + request.getMethodName());
                    }
                }
            } else {
                logger.log(ILoggingLogLevel.Error, LOG_TAG, "There is no bridge with the identifier: " + bridgeType);
                return new APIResponse("", 400, "There is no bridge with the identifier: " + bridgeType);
            }
        } else {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "There is no bridge type inside the API Request object");
            return new APIResponse("", 400, "There is no bridge type inside the API Request object.");
        }
    }
}
