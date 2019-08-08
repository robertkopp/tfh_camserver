'use strict';

angular.module('myApp.clientview', ['ngRoute'])

        .config(['$routeProvider', function ($routeProvider) {
                $routeProvider.when('/clientview', {
                    templateUrl: 'clientview/clientview.html',
                    controller: 'ClientDataCtrl'
                });
            }])

//\"action\":\"expiry\"

        .controller('ClientDataCtrl', ['$scope', '$http', '$log', 'atmosphereService', function ($scope, $http, $log, atmosphereService) {
                $http.get('api/clientraspi').then(function (response) {
                    $scope.gridOptions.data = response.data;
                });



                $scope.columns = [{field: 'ipAdress', displayName: 'IP'},
                    {field: 'hostId', displayName: 'Name'},
                   {field: 'cameras', displayName: 'Kameras', cellTemplate:'<span ng-repeat="cam in row.entity.cameras">{{cam.cameraId}};</span>'}
                ];
                
                $scope.gridOptions = {
                    enableFiltering: true,
                    enableSorting: true,
                    enableRowSelection: true,
                    enableRowHeaderSelection: false,
                    multiSelect: false,
                    columnDefs: $scope.columns,
                    onRegisterApi: function (gridApi) {
                        $scope.gridApi = gridApi;
                        
                        gridApi.selection.on.rowSelectionChanged($scope, function (row) {

                            //$scope.recipe = row.entity;
                            

                        });

                    }
                };

                $scope.model = {
                    transport: 'websocket',
                    messages: []
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
                        if (message.hasOwnProperty("action")) {
                            if (message.action === "expiry") {
                                $http.get('api/clientraspi').then(function (response) {
                                    $scope.gridOptions.data = response.data;
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
