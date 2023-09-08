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

OneSignalPlugin.prototype.addSubscriptionObserver = function(callback) {
    OneSignalPlugin._subscriptionObserverList.push(callback);
    var subscriptionCallBackProcessor = function(state) {
        OneSignalPlugin._processFunctionList(OneSignalPlugin._subscriptionObserverList, new OSSubscriptionStateChanges(state));
    };
    window.cordova.exec(subscriptionCallBackProcessor, function(){}, "OneSignalPush", "addSubscriptionObserver", []);
};
