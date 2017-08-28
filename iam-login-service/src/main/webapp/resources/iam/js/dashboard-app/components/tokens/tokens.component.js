(function() {
  'use strict';

  function TokensManagementController() {

    var self = this;
    self.totAccessTokens = self.accessTokensCount;
    self.totRefreshTokens = self.refreshTokensCount;
    self.selected = 0;

    self.$onInit = function() {
      self.loaded = true;
    };

  }

  angular.module('dashboardApp').component('tokens', {
    templateUrl:
        '/resources/iam/js/dashboard-app/components/tokens/tokens.component.html',
    bindings: {
        clients: '<',
        users: '<',
        accessTokensCount: '<',
        refreshTokensCount: '<'
    },
    controller: [
      TokensManagementController
    ]
  });
})();