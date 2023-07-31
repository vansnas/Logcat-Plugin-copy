var exec = require('cordova/exec');

exports.sendLogs = function (success, error) {
    exec(success, error, 'LogCat', 'sendLogs', []);
};

exports.uploadPlugin = function (vin, success, error) {
    exec(success, error, 'LogCat', 'uploadPlugin', [vin]);
};

exports.registerDevice = function (appid, success, error) {
    exec(success, error, 'LogCat', 'uploadPlugin', [appid]);
};

/*document.addEventListener('notificationReceived', function(event) {
    var notification = event.data.notification;
    
    var title = notification.title;
    var message = notification.body;
    var customData = notification.additionalData;

    exports.uploadPlugin(
        'Test1111111VIN',
        function(successData) {
            console.log('Logs sent successfully');
        },
        function(errorData) {
            console.log('Failed to send logs:', errorData);
        }
    );
});

window.plugins.OneSignal.handleNotificationOpened(function(jsonData) {
    var notification = jsonData.notification;

    exports.uploadPlugin(
        'Test2222222VIN',
        function(successData) {
            console.log('Logs sent successfully');
        },
        function(errorData) {
            console.log('Failed to send logs:', errorData);
        }
    );
    
});*/
