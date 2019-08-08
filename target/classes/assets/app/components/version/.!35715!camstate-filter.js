'use strict';

angular.module('myApp.camstate-filter', [])

.filter('camState', [ function() {
  return function(text) {
      if (text == '0') return "Ok";
      if (text == '1') return "Fehler";
      if (text == '2') return "Nicht Installiert";
