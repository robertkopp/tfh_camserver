'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
  'ngRoute','ngCookies',
  'myApp.version','myApp.clientview','myApp.camstate-filter','myApp.photoview','myApp.settingsservice',
  'ngFileUpload', 'ui.grid',
  'ui.grid.selection','ui.grid.edit',
  'textAngular','myApp.settingsview','angular.atmosphere'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/clientview'});
}]);
