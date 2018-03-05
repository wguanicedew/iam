(function() {
  'use strict';

  function TokensManagementController($scope, $rootScope) {

    var self = this;

    self.$onInit = function() {
      self.selected = 0;
      $rootScope.accessTokensCount = self.accessTokensFirstResponse.totalResults;
      $rootScope.refreshTokensCount = self.refreshTokensFirstResponse.totalResults;
      self.loaded = true;
    };

    self.isSelected = function(tabNumber) {
      return tabNumber == self.selected;
    }

    self.doSelect = function(tabNumber) {
      console.debug("doSelect", tabNumber);
      self.selected = tabNumber;
      if (tabNumber == 0) {
        self.selected = 0;
        $scope.$broadcast('refreshAccessTokensList');
      }
      if (tabNumber == 1) {
        self.selected = 1;
        $scope.$broadcast('refreshRefreshTokensList');
      }
    }
  }

  angular
    .module('dashboardApp')
    .component('tokens', {
      templateUrl:
        '/resources/iam/js/dashboard-app/components/tokens/tokens.component.html',
      bindings: {
        clients: '<',
        users: '<',
        accessTokensFirstResponse: '<',
        refreshTokensFirstResponse: '<'
      },
      controller: [
        '$scope', '$rootScope', TokensManagementController
      ]
    });
})();