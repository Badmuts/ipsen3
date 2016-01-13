'use strict';

/**
 * @ngdoc function
 * @name appApp.controller:ContactCtrl
 * @description
 * # ContactCtrl
 * Controller of the appApp
 */
angular.module('appApp').controller('ContactCtrl', function ($scope, $http, mailservice) {
    $scope.formModel = {};

        $scope.submitContact = function (valid) {
          if(valid) {
          console.log("form submitted");
          console.log($scope.formModel);
            mailservice.send($scope.formModel).then(function succesCallback(result) {
                    console.log("Send succes");
            }, function errorCallback(result) {
                console.log("Send failed");
            });
        } else {
            alert("U heeft niet alle velden ingevuld");
            console.log("invalid!");
        }};

  });