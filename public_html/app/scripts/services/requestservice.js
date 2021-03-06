'use strict';

/**
 * @ngdoc service
 * @name appApp.requestService
 * @description
 * # requestService
 * Service in the appApp.
 */
angular.module('appApp')
  .service('requestService', function ($q, $rootScope, authenticationService) {
    // AngularJS will instantiate a singleton by calling "new" on this function

    /**
     * Self-reference used for internal referencing.
     */
    var self = this;

    /**
     * The number of open requests.
     */
    var openRequests = 0;

    /**
     * Adds authentication information to the request header.
     *
     * This is a http interceptor function.
     *
     * @param {object} config The config object.
     * @returns {object} The config object.
     */
    self.request = function(config)
    {
      config.headers = config.headers || {};

      if (authenticationService.isAuthenticated())
      {
        config.headers['Authorization'] = authenticationService.createAuthorizationString();
      }

      requestOpened();

      return config || $q.when(config);
    };

    /**
     * Handles a succesfull response.
     *
     * This is a http interceptor function.
     *
     * @param {object} response The response object.
     * @returns {object} The response object.
     */
    self.response = function(response)
    {
      requestClosed();

      return response || $q.when(response);
    };

    /**
     * Handles an unsuccessfull response.
     *
     * This is a http interceptor function.
     *
     * @param {object} response The response object.
     * @returns {object} The response object.
     */
    self.responseError = function(response)
    {
      requestClosed();

      return $q.reject(response);
    };

    /**
     * Increments the number of open requests.
     */
    var requestOpened = function()
    {
      openRequests++;
      $rootScope.loading = true;
    };

    /**
     * Decrements the number of open requests.
     */
    var requestClosed = function()
    {
      openRequests--;

      if (openRequests === 0)
      {
        $rootScope.loading = false;
      }
    };

  });
