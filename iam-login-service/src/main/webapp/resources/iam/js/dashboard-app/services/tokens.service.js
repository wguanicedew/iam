'use strict'

angular.module('dashboardApp').factory('TokensService', TokensService);

TokensService.$inject = [ '$q', '$rootScope', '$http', '$httpParamSerializer' ];

function TokensService($q, $rootScope, $http, $httpParamSerializer) {
    var service = {
        getAccessTokens : getAccessTokens,
        getAccessToken : getAccessToken,
        getAccessTokensFilteredByUser : getAccessTokensFilteredByUser,
        getAccessTokensFilteredByClient : getAccessTokensFilteredByClient,
        getAccessTokensFilteredByUserAndClient : getAccessTokensFilteredByUserAndClient,
        revokeAccessToken : revokeAccessToken,
        getRefreshToken : getRefreshToken,
        getRefreshTokensFilteredByUser : getRefreshTokensFilteredByUser,
        getRefreshTokensFilteredByClient : getRefreshTokensFilteredByClient,
        getRefreshTokensFilteredByUserAndClient : getRefreshTokensFilteredByUserAndClient,
        getRefreshTokens : getRefreshTokens,
        revokeRefreshToken : revokeRefreshToken,
        getAccessTokensCount : getAccessTokensCount,
        getRefreshTokensCount : getRefreshTokensCount
    };

    var urlAccessTokens = "/iam/api/access-tokens";
    var urlRefreshTokens = "/iam/api/refresh-tokens";

    return service;

    function getAccessTokensFilteredByUser(startIndex, count, userName) {
        console.info("Getting access-tokens offset-limit: ", startIndex, count);
        console.info("Filtered user: ", userName);
        var qs = $httpParamSerializer({
            'startIndex' : startIndex,
            'count' : count,
            'userId' : userName
        });
        var url = urlAccessTokens + '?' + qs;
        return $http.get(url);
    }

    function getRefreshTokensFilteredByUser(startIndex, count, userName) {
      console.info("Getting refresh-tokens offset-limit: ", startIndex, count);
      console.info("Filtered user: ", userName);
      var qs = $httpParamSerializer({
          'startIndex' : startIndex,
          'count' : count,
          'userId' : userName
      });
      var url = urlRefreshTokens + '?' + qs;
      return $http.get(url);
    }

    function getAccessTokensFilteredByClient(startIndex, count, clientId) {
        console.info("Getting access-tokens offset-limit: ", startIndex, count);
        console.info("Filtered client: ", clientId);
        var qs = $httpParamSerializer({
            'startIndex' : startIndex,
            'count' : count,
            'clientId' : clientId
        });
        var url = urlAccessTokens + '?' + qs;
        return $http.get(url);
    }

    function getRefreshTokensFilteredByClient(startIndex, count, clientId) {
      console.info("Getting refresh-tokens offset-limit: ", startIndex, count);
      console.info("Filtered client: ", clientId);
      var qs = $httpParamSerializer({
          'startIndex' : startIndex,
          'count' : count,
          'clientId' : clientId
      });
      var url = urlRefreshTokens + '?' + qs;
      return $http.get(url);
  }

    function getAccessTokensFilteredByUserAndClient(startIndex, count, userName, clientId) {
        console.info("Getting access-tokens offset-limit: ", startIndex, count);
        console.info("Filtered user and client: ", userName, clientId);
        var qs = $httpParamSerializer({
            'startIndex' : startIndex,
            'count' : count,
            'clientId' : clientId,
            'userId' : userName
        });
        var url = urlAccessTokens + '?' + qs;
        return $http.get(url);
    }

    function getRefreshTokensFilteredByUserAndClient(startIndex, count, userName, clientId) {
        console.info("Getting refresh-tokens offset-limit: ", startIndex, count);
        console.info("Filtered user and client: ", userName, clientId);
        var qs = $httpParamSerializer({
            'startIndex' : startIndex,
            'count' : count,
            'clientId' : clientId,
            'userId' : userName
        });
        var url = urlRefreshTokens + '?' + qs;
        return $http.get(url);
    }

    function getAccessTokens(startIndex, count) {
      console.info("Getting access-tokens offset-limit: ", startIndex, count);
      var qs = $httpParamSerializer({
          'startIndex' : startIndex,
          'count' : count
      });
      var url = urlAccessTokens + '?' + qs;
      return $http.get(url);
    }

    function getRefreshTokens(startIndex, count) {
      console.info("Getting refresh-tokens offset-limit: ", startIndex, count);
      var qs = $httpParamSerializer({
          'startIndex' : startIndex,
          'count' : count
      });
      var url = urlRefreshTokens + '?' + qs;
      return $http.get(url);
    }

    function getAccessToken(id) {
        console.info("Getting access-token with id: ", id);
        var url = urlAccessTokens + '/' + id;
        return $http.get(url);
    }

    function revokeAccessToken(id) {
      console.info("Revoking access-token with id: ", id);
      var url = urlAccessTokens + '/' + id;
      return $http.delete(url);
    }

    function getRefreshToken(id) {
      console.info("Getting refresh-token with id: ", id);
      var url = urlRefreshTokens + '/' + id;
      return $http.get(url);
    }

    function revokeRefreshToken(id) {
      console.info("Revoking refresh-token with id: ", id);
      var url = urlRefreshTokens + '/' + id;
      return $http.delete(url);
    }

    function getAccessTokensCount() {
      var qs = $httpParamSerializer({
          'count' : 0
      });
      var url = urlAccessTokens + '?' + qs;
      return $http.get(url);
    }

    function getRefreshTokensCount() {
      var qs = $httpParamSerializer({
          'count' : 0
      });
      var url = urlRefreshTokens + '?' + qs;
      return $http.get(url);
    }

}