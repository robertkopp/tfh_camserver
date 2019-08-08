'use strict';

var module = angular.module('myApp.settingsservice', []);
module.factory('SettingsService', [function () {

        var instance = {};
        instance.brightness = 1.0;
        instance.contrast = 1.0;
        instance.saturation = 1.0;
        instance.hue = 1.0;
        instance.sharpness = 1.0;
        instance.gamme = 1.0;
        instance.gain = 1.0;
        instance.whitebalance = 1.0;
        instance.backlightcontrast = 1.0;
        instance.exposure = 1.0;

        return instance;

    }]);
