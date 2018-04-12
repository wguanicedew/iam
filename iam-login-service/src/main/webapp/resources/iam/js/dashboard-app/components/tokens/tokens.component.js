/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function() {
  'use strict';

  function TokensManagementController($scope, $rootScope) {

    var self = this;

    self.$onInit = function() {
      console.debug(self.accessTokensFirstResponse, self.refreshTokensFirstResponse);
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