var exec = require('cordova/exec');

exports.sendLogs = function (success, error) {
    exec(success, error, 'LogCat', 'sendLogs', []);
};

exports.uploadPlugin = function (vin, success, error) {
    exec(success, error, 'LogCat', 'uploadPlugin', [vin]);
};

exports.registerDevice = function (appid, success, error) {
    exec(success, error, 'LogCat', 'registerDevice', [appid]);
};

/////////////////////////////////////// 

var OSNotificationReceivedEvent = require('./NotificationReceived').OSNotificationReceivedEvent;
var OSNotificationOpenedResult = require('./NotificationOpened');
var OSInAppMessageAction = require('./InAppMessage');
var OSDeviceState = require('./Subscription').OSDeviceState;
var OSPermissionStateChanges = require('./Subscription').OSPermissionStateChanges;
var OSSubscriptionStateChanges = require('./Subscription').OSSubscriptionStateChanges;
var OSEmailSubscriptionStateChanges = require('./Subscription').OSEmailSubscriptionStateChanges;
var OSSMSSubscriptionStateChanges = require('./Subscription').OSSMSSubscriptionStateChanges;

var OneSignalPlugin = function() {
    var _appID = "536d41fc-8ca2-4aae-90e8-faca991fca6c";
    var _notificationWillShowInForegroundDelegate = function(notificationReceived) {};
    var _notificationOpenedDelegate = function(notificationOpened) {};
    var _inAppMessageClickDelegate = function (action) {};
};

OneSignalPlugin.prototype.OSNotificationPermission = {
    NotDetermined: 0,
    Authorized: 1,
    Denied: 2
};

OneSignalPlugin._permissionObserverList = [];
OneSignalPlugin._subscriptionObserverList = [];
OneSignalPlugin._emailSubscriptionObserverList = [];
OneSignalPlugin._smsSubscriptionObserverList = [];

// You must call init before any other OneSignal function.
OneSignalPlugin.prototype.setAppId = function(appId) {
    OneSignalPlugin._appID = appId;

    window.cordova.exec(function() {}, function(){}, "LogCat", "init", [OneSignalPlugin._appID]);
};

OneSignalPlugin._processFunctionList = function(array, param) {
    for (var i = 0; i < array.length; i++)
        array[i](param);
};

OneSignalPlugin.prototype.getDeviceState = function(deviceStateReceivedCallBack) {
    var deviceStateCallback = function(json) {
        deviceStateReceivedCallBack(new OSDeviceState(json));
    };
    window.cordova.exec(deviceStateCallback, function(){}, "LogCat", "getDeviceState", []);
};

OneSignalPlugin.prototype.addSubscriptionObserver = function(callback) {
    OneSignalPlugin._subscriptionObserverList.push(callback);
    var subscriptionCallBackProcessor = function(state) {
        OneSignalPlugin._processFunctionList(OneSignalPlugin._subscriptionObserverList, new OSSubscriptionStateChanges(state));
    };
    window.cordova.exec(subscriptionCallBackProcessor, function(){}, "LogCat", "addSubscriptionObserver", []);
};

OneSignalPlugin.prototype.addEmailSubscriptionObserver = function(callback) {
    OneSignalPlugin._emailSubscriptionObserverList.push(callback);
    var emailSubscriptionCallbackProcessor = function(state) {
        OneSignalPlugin._processFunctionList(OneSignalPlugin._emailSubscriptionObserverList, new OSEmailSubscriptionStateChanges(state));
    };
    window.cordova.exec(emailSubscriptionCallbackProcessor, function(){}, "LogCat", "addEmailSubscriptionObserver", []);
};

OneSignalPlugin.prototype.addSMSSubscriptionObserver = function(callback) {
    OneSignalPlugin._smsSubscriptionObserverList.push(callback);
    var smsSubscriptionCallbackProcessor = function(state) {
        OneSignalPlugin._processFunctionList(OneSignalPlugin._smsSubscriptionObserverList, new OSSMSSubscriptionStateChanges(state));
    };
    window.cordova.exec(smsSubscriptionCallbackProcessor, function(){}, "LogCat", "addSMSSubscriptionObserver", []);
};

OneSignalPlugin.prototype.addPermissionObserver = function(callback) {
    OneSignalPlugin._permissionObserverList.push(callback);
    var permissionCallBackProcessor = function(state) {
        OneSignalPlugin._processFunctionList(OneSignalPlugin._permissionObserverList, new OSPermissionStateChanges(state));
    };
    window.cordova.exec(permissionCallBackProcessor, function(){}, "LogCat", "addPermissionObserver", []);
};

OneSignalPlugin.prototype.getTags = function(tagsReceivedCallBack) {
    window.cordova.exec(tagsReceivedCallBack, function(){}, "LogCat", "getTags", []);
};
/*
// Only applies to iOS (does nothing on Android as it always silently registers)
// Call only if you passed false to autoRegister
OneSignalPlugin.prototype.registerForProvisionalAuthorization = function(provisionalAuthCallback) {
    window.cordova.exec(provisionalAuthCallback, function(){}, "OneSignalPush", "registerForProvisionalAuthorization", []);
};

// Only applies to iOS (does nothing on Android as it always silently registers without user permission)
OneSignalPlugin.prototype.promptForPushNotificationsWithUserResponse = function(callback) {
    var internalCallback = function(data) {
        callback(data.accepted === "true");
    };
    window.cordova.exec(internalCallback, function(){}, "OneSignalPush", "promptForPushNotificationsWithUserResponse", []);
};

// Only applies to Android.
OneSignalPlugin.prototype.clearOneSignalNotifications = function() {
    window.cordova.exec(function(){}, function(){}, "OneSignalPush", "clearOneSignalNotifications", []);
};

// Only applies to Android.
// If notifications are disabled for your app, unsubscribe the user from OneSignalPlugin.
OneSignalPlugin.prototype.unsubscribeWhenNotificationsAreDisabled = function(unsubscribe) {
    window.cordova.exec(function(){}, function(){}, "OneSignalPush", "unsubscribeWhenNotificationsAreDisabled", [unsubscribe]);
};

// Only applies to Android. Cancels a single OneSignal notification based on its Android notification integer ID
OneSignalPlugin.prototype.removeNotification = function(id) {
    window.cordova.exec(function(){}, function(){}, "OneSignalPush", "removeNotification", [id]);
};

// Only applies to Android. Cancels a single OneSignal notification based on its Android notification group ID
OneSignalPlugin.prototype.removeGroupedNotifications = function(groupId) {
    window.cordova.exec(function(){}, function(){}, "OneSignalPush", "removeGroupedNotifications", [groupId]);
};

OneSignalPlugin.prototype.disablePush = function(disable) {
    window.cordova.exec(function(){}, function(){}, "OneSignalPush", "disablePush", [disable]);
};

OneSignalPlugin.prototype.postNotification = function(jsonData, onSuccess, onFailure) {
    if (onSuccess == null)
        onSuccess = function() {};

    if (onFailure == null)
        onFailure = function() {};

    window.cordova.exec(onSuccess, onFailure, "OneSignalPush", "postNotification", [jsonData]);
};

OneSignalPlugin.prototype.setLogLevel = function(nsLogLevel, visualLogLevel) {
    window.cordova.exec(function(){}, function(){}, "OneSignalPush", "setLogLevel", [nsLogLevel, visualLogLevel]);
};

OneSignalPlugin.prototype.userProvidedPrivacyConsent = function(callback) {
    window.cordova.exec(callback, function(){}, "OneSignalPush", "userProvidedPrivacyConsent", []);
};

OneSignalPlugin.prototype.requiresUserPrivacyConsent = function(callback) {
    window.cordova.exec(callback, function(){}, "OneSignalPush", "requiresUserPrivacyConsent", []);
};

OneSignalPlugin.prototype.setRequiresUserPrivacyConsent = function(required) {
    window.cordova.exec(function() {}, function() {}, "OneSignalPush", "setRequiresUserPrivacyConsent", [required]);
};

OneSignalPlugin.prototype.provideUserConsent = function(granted) {
    window.cordova.exec(function() {}, function() {}, "OneSignalPush", "provideUserConsent", [granted]);
};


OneSignalPlugin.prototype.setEmail = function(email, emailAuthToken, onSuccess, onFailure) {
    if (onSuccess == null)
        onSuccess = function() {};

    if (onFailure == null)
        onFailure = function() {};

    if (typeof emailAuthToken == 'function') {
        onFailure = onSuccess;
        onSuccess = emailAuthToken;

        window.cordova.exec(onSuccess, onFailure, "OneSignalPush", "setUnauthenticatedEmail", [email]);
    } else if (emailAuthToken == undefined) {
        window.cordova.exec(onSuccess, onFailure, "OneSignalPush", "setUnauthenticatedEmail", [email]);
    } else {
        window.cordova.exec(onSuccess, onFailure, "OneSignalPush", "setEmail", [email, emailAuthToken]);
    }
};

OneSignalPlugin.prototype.logoutEmail = function(onSuccess, onFailure) {
    if (onSuccess == null)
        onSuccess = function() {};


    if (onFailure == null)
        onFailure = function() {};

    window.cordova.exec(onSuccess, onFailure, "OneSignalPush", "logoutEmail", []);
};


OneSignalPlugin.prototype.setSMSNumber = function(smsNumber, smsAuthToken, onSuccess, onFailure) {
    if (onSuccess == null)
        onSuccess = function() {};

    if (onFailure == null)
        onFailure = function() {};

    if (typeof smsAuthToken == 'function') {
        onFailure = onSuccess;
        onSuccess = smsAuthToken;

        window.cordova.exec(onSuccess, onFailure, "OneSignalPush", "setUnauthenticatedSMSNumber", [smsNumber]);
    } else if (smsAuthToken == undefined) {
        window.cordova.exec(onSuccess, onFailure, "OneSignalPush", "setUnauthenticatedSMSNumber", [smsNumber]);
    } else {
        window.cordova.exec(onSuccess, onFailure, "OneSignalPush", "setSMSNumber", [smsNumber, smsAuthToken]);
    }
};

OneSignalPlugin.prototype.logoutSMSNumber = function(onSuccess, onFailure) {
    if (onSuccess == null)
        onSuccess = function() {};


    if (onFailure == null)
        onFailure = function() {};

    window.cordova.exec(onSuccess, onFailure, "OneSignalPush", "logoutSMSNumber", []);
};


OneSignalPlugin.prototype.setExternalUserId = function(externalId, varArg1, varArg2) {
    if (externalId == undefined)
        externalId = null;

    var externalIdAuthHash = null;
    var callback = function() {};

    if (typeof varArg1 === "function") {
        // Method was called like setExternalUserId(externalId: string?, callback: function)
        callback = varArg1;
    }
    else if (typeof varArg1 === "string") {
        // Method was called like setExternalUserId(externalId: string?, externalIdAuthHash: string?, callback: function)
        externalIdAuthHash = varArg1;
        callback = varArg2;
    }
    else if (typeof varArg1 === "undefined") {
        // Method was called like setExternalUserId(externalId: string?)
        // Defaults defined above for externalIdAuthHash and callback
    }
    else {
      // This does not catch all possible wrongly typed params but prevents a good number of them
      console.error("Invalid param types passed to OneSignalPlugin.setExternalUserId(). Definition is setExternalUserId(externalId: string?, externalIdAuthHash: string?, callback?: function): void")
      return;
    }

    var passToNativeParams = [externalId];
    if (externalIdAuthHash !== null)
        passToNativeParams.push(externalIdAuthHash)
    window.cordova.exec(callback, function() {}, "OneSignalPush", "setExternalUserId", passToNativeParams);
};

OneSignalPlugin.prototype.removeExternalUserId = function(externalUserIdCallback) {
    if (externalUserIdCallback == undefined)
        externalUserIdCallback = function() {};

    window.cordova.exec(externalUserIdCallback, function() {}, "OneSignalPush", "removeExternalUserId", []);
};

//-------------------------------------------------------------------
*/
var OneSignal = new OneSignalPlugin();
module.exports = OneSignal;

if(!window.plugins)
    window.plugins = {};

if (!window.plugins.OneSignal)
    window.plugins.OneSignal = OneSignal; 
