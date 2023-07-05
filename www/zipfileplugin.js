var exec = require('cordova/exec');

exports.uploadPlugin = function (success, error) {
    exec(success, error, 'ZipFilesPlugin', 'uploadPlugin', []);
};
