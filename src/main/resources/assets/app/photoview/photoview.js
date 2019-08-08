'use strict';

angular.module('myApp.photoview', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/photoview', {
    templateUrl: 'photoview/photoview.html',
    controller: 'PhotoViewCtrl'
  });
}])

.controller('PhotoViewCtrl', ['$scope','$http','$timeout','atmosphereService','$log',function ($scope,$http,$timeout,atmosphereService,$log) {
    $http.get('api/photocollection').then(function (response) {
        $scope.shots = response.data;
    });
    $scope.takeShot=function(){
        $http.post('api/photocollection');
        $timeout(function(){
            $http.get('api/photocollection').then(function (response) {
        $scope.shots = response.data;
    });
        },500);
    };
    
    
    $scope.saveKundennummer= function(shot){
        //"/updatekundennummer/{shotid}/{knr}"
        $http.put('api/photocollection/updatekundennummer/'+shot.collectionId+'/'+shot.kundennummer);
    };
    
     var socket;

                var request = {
                    url: '/websocket/round',
                    contentType: 'application/json',
                    logLevel: 'debug',
                    transport: 'websocket',
                    trackMessageLength: true,
                    reconnectInterval: 5000,
                    enableXDR: true,
                    timeout: 60000
                };

                request.onOpen = function (response) {
                    $log.info("open photoview");
                    $scope.model.transport = response.transport;
                    $scope.model.connected = true;
                    $scope.model.content = 'Atmosphere connected using ' + response.transport;                    
                };

                request.onClientTimeout = function (response) {
                    $scope.model.content = 'Client closed the connection after a timeout. Reconnecting in ' + request.reconnectInterval;
                    $scope.model.connected = false;
                    socket.push(atmosphere.util.stringifyJSON({author: author, message: 'is inactive and closed the connection. Will reconnect in ' + request.reconnectInterval}));
                    setTimeout(function () {
                        socket = atmosphereService.subscribe(request);
                    }, request.reconnectInterval);
                };

                request.onReopen = function (response) {
                    $scope.model.connected = true;
                    $scope.model.content = 'Atmosphere re-connected using ' + response.transport;
                };

                //For demonstration of how you can customize the fallbackTransport using the onTransportFailure function
                request.onTransportFailure = function (errorMsg, request) {
                    atmosphere.util.info(errorMsg);
                    request.fallbackTransport = 'long-polling';
                    $scope.model.header = 'Atmosphere Chat. Default transport is WebSocket, fallback is ' + request.fallbackTransport;
                };

                request.onMessage = function (response) {
                    $log.info(response);
                    var responseText = response.responseBody;
                    try {
                        var message = atmosphere.util.parseJSON(responseText);
                        //"{"action":"shotupdate","shotid":"m8rv8majae2e32ogtikq","count":2}"
                        if (message.hasOwnProperty("action")) {
                            if (message.action === "shotupdate") {
                                $http.get('api/photocollection').then(function (response) {
        $scope.shots = response.data;
    });
                            }
                        }
                    } catch (e) {
                        console.error("Error parsing JSON: ", responseText);
                        throw e;
                    }
                };

                request.onClose = function (response) {
                    $scope.model.connected = false;
                    $scope.model.content = 'Server closed the connection after a timeout';
                    socket.push(atmosphere.util.stringifyJSON({author: $scope.model.name, message: 'disconnecting'}));
                };

                request.onError = function (response) {
                    $scope.model.content = "Sorry, but there's some problem with your socket or the server is down";
                    $scope.model.logged = false;
                };

                request.onReconnect = function (request, response) {
                    $scope.model.content = 'Connection lost. Trying to reconnect ' + request.reconnectInterval;
                    $scope.model.connected = false;
                };

                socket = atmosphereService.subscribe(request);
    
}]);
