var exec = require('cordova/exec');

exports.scan = function (arg0, success, error) {
    exec(success, error, 'scanner', 'scan', [arg0]);
};

exports.permission = function (success, error) {
    exec(success, error, 'scanner', 'permission', []);
};