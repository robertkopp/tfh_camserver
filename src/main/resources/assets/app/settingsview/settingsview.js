'use strict';

angular.module('myApp.settingsview', ['ngRoute', 'angular.atmosphere'])

        .config(['$routeProvider', function ($routeProvider) {
                $routeProvider.when('/settingsview', {
                    templateUrl: 'settingsview/settingsview.html',
                    controller: 'SettingsViewCtrl'
                });
            }])

        .controller('SettingsViewCtrl', ['$scope', '$http', '$log', 'atmosphereService', 'SettingsService', function ($scope, $http, $log, atmosphereService, settingsService) {
                $scope.model = {
                    transport: 'websocket',
                    messages: []
                };
                $scope.settings=settingsService;
                $http.get('api/clientraspi').then(function (response) {
                    $scope.clients = response.data;
                });
$scope.savingSettings=false;
$scope.saveSettings= function(){
                     $scope.savingSettings=true;
                    $http.post('api/settings/' + raspiid + '/' + camid, $scope.settings).then(function(){
                        $scope.savingSettings=false;
                    });
};
                $scope.getImage = function () {
                    $scope.loadingImage = true;
                    var raspiid = $scope.clients[$scope.selectedClientIndex].hostId;
                    var camid = $scope.clients[$scope.selectedClientIndex].cameras[$scope.selectedCamIndex].cameraId;
                    $http.post('api/singleimage/takesnap/' + raspiid + '/' + camid);

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
                    $log.info("yaaaa open");
                    $scope.model.transport = response.transport;
                    $scope.model.connected = true;
                    $scope.model.content = 'Atmosphere connected using ' + response.transport;
                    $http.post("api/settings/snap").then(function () {
                        $log.info("snap");
                    });
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
                        if (message.hasOwnProperty("update")) {
                            if (message.update === 1) {
                                var raspiid = $scope.clients[$scope.selectedClientIndex].hostId;
                                var camid = $scope.clients[$scope.selectedClientIndex].cameras[$scope.selectedCamIndex].cameraId;
                                $scope.imageSource = 'api/singleimage/snapresult/' + raspiid + '/' + camid + '?' + Math.random();
                                $scope.loadingImage = false;
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
