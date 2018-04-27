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
'use strict';

angular.module('dashboardApp', [
  'ui.router', 'ui.bootstrap', 'ui.bootstrap.tpls', 'ui.select', 'ngCookies',
  'ngSanitize', 'relativeDate', 'ngResource', 'toaster'
]);

angular.module('dashboardApp')
    .run(function(
        $window, $rootScope, $state, $stateParams, $q, $uibModal, $trace, Utils,
        scimFactory, UserService, RegistrationRequestService,
        LoadTemplatesService, TokensService) {

      $state.defaultErrorHandler(function(error) { console.error(error); });

      $rootScope.iamVersion = getIamVersion();
      $rootScope.iamCommitId = getIamGitCommitId();
      $rootScope.accountLinkingEnabled = getAccountLinkingEnabled();
      
      LoadTemplatesService.loadTemplates();

      $trace.enable('TRANSITION');

      // Offline dialog
      $rootScope.closeOfflineDialog = function() {

        console.log('into: closeOfflineDialog');

        if ($rootScope.offlineDialog) {
          console.log('Closing offline dialog');
          $rootScope.offlineDialog.dismiss('Back online');
          $rootScope.offlineDialog = undefined;
        }
      };

      $rootScope.openOfflineDialog = function() {

        if (!$rootScope.offlineDialog) {
          console.log('Opening offline dialog');
          $rootScope.offlineDialog = $uibModal.open({
            animation: false,
            backdrop: 'static',
            keyboard: false,
            templateUrl: 'noConnectionTemplate.html'
          });
        }
      };

      $rootScope.reloadInfo = function() {

        var promises = [];
        promises.push(UserService.updateLoggedUserInfo());

        if ($rootScope.isRegistrationEnabled && Utils.isAdmin()) {
          promises.push(RegistrationRequestService.listPending().then(function(r) {
            console.debug("listPending response", r);
            $rootScope.loggedUser.pendingRequests = r.data;
          }));
          promises.push(TokensService.getAccessTokensCount().then(function(r) {
            console.debug("getAccessTokensCount response", r);
            $rootScope.accessTokensCount = r.data.totalResults;
          }));
          promises.push(TokensService.getRefreshTokensCount().then(function(r) {
            console.debug("getRefreshTokensCount response", r);
            $rootScope.refreshTokensCount = r.data.totalResults;
          }));
        }

        return $q.all(promises).catch(function(error){
          console.error("Error loading logged user info"+error);
        });
      };

      $rootScope.reloadInfo();

      // ctrl+R refresh
      $rootScope.reload = function() { $window.location.reload(); };

      // refresh last state loaded
      $rootScope.refresh = function() {

        $rootScope.closeOfflineDialog();
        $state.transitionTo(
            $state.current, $stateParams,
            {reload: true, inherit: false, notify: true});
      };

      $('#body').show();

    });
