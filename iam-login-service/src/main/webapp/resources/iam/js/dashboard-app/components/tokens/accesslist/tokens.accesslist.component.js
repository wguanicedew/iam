(function() {
  'use strict';

  function RevokeAccessTokenController($rootScope, $uibModalInstance, TokensService, token) {
    var self = this;

    self.token = token;
    self.enabled = true;
    self.error = undefined;

    self.doRevoke = function (token) {
      self.error = undefined;
      self.enabled = false;
      TokensService.revokeAccessToken(token.id).then(function (response) {
        $uibModalInstance.close(token);
        $rootScope.reloadInfo();
        self.enabled = true;
      }).catch(function (error) {
        console.error(error);
        self.error = error;
        self.enabled = true;
      });
    };

    self.cancel = function () {
      $uibModalInstance.dismiss('Dismissed');
    };
  }

  function AccessTokensListController($q, $rootScope, $uibModal, ModalService,
      TokensService, scimFactory, clipboardService, Utils, toaster) {

    var self = this;

    // pagination controls
    self.currentPage = 1;
    self.itemsPerPage = 10;

    self.$onInit = function() {
      self.searchTokens();
      self.loaded = true;
    };

    self.copyToClipboard = function(toCopy) {
      clipboardService.copyToClipboard(toCopy);
      toaster.pop({ type: 'success', body: 'Token copied to clipboard!' });
    };

    self.searchTokens = function() {

      self.tokens = [];
      self.loaded = false;

      var promises = [];
      var chunkRequestSize = 20;
      
      var handleResponse = function(response){
        angular.forEach(response.data.Resources, function(token){
          self.tokens.push(token);
        });
      };
      
      var handleError = function(error) {
        self.loadingModal.dismiss("Error");
        toaster.pop({type: 'error', body: error});
      };
      
      var handleFirstResponse = function(response){

        var totalResults = response.data.totalResults;
        var lastLoaded = chunkRequestSize;
        
        while (lastLoaded < totalResults) {
          promises.push(self.getAccessTokenList(lastLoaded+1, chunkRequestSize));
          lastLoaded = lastLoaded + chunkRequestSize;
        }
        
        angular.forEach(response.data.Resources, function(token){
          self.tokens.push(token);
        });
        
        $q.all(promises).then(function(response){
          angular.forEach(promises, function(p){
            p.then(handleResponse);
          });
          $rootScope.pageLoadingProgress = 100;
          self.loaded = true;
          self.loadingModal.dismiss("Cancel");
          $rootScope.reloadInfo();
        }, handleError);
      };
      
      $rootScope.pageLoadingProgress = 0;
      
      self.loadingModal = $uibModal.open({
        animation: false,
        templateUrl : '/resources/iam/template/dashboard/loading-modal.html'
      });
      
      self.loadingModal.opened.then(function(){
        self.getAccessTokenList(1, chunkRequestSize).then(handleFirstResponse, handleError);
      });
    }

    self.getAccessTokenList = function(startIndex, count) {
      if (self.clientSelected && self.userSelected) {
        return TokensService.getAccessTokensFilteredByUserAndClient(startIndex, count, self.userSelected.userName, self.clientSelected.clientId);
      } 
      if (self.clientSelected) {
        return TokensService.getAccessTokensFilteredByClient(startIndex, count, self.clientSelected.clientId);
      } 
      if (self.userSelected) {
        return TokensService.getAccessTokensFilteredByUser(startIndex, count, self.userSelected.userName);
      }
      return TokensService.getAccessTokens(startIndex, count);
    }

    self.handleRevokeSuccess = function(token) {
      self.enabled = true;
      toaster.pop({
        type: 'success',
        body: 'Token Revoked'
      });
      self.searchTokens();
    };

    self.openRevokeAccessTokenDialog = function (token) {

      var modalInstance = $uibModal.open({
        templateUrl: '/resources/iam/js/dashboard-app/components/tokens/accesslist/token.revoke.dialog.html',
        controller: RevokeAccessTokenController,
        controllerAs: '$ctrl',
        resolve: {
          token: token
        }
      });

      modalInstance.result.then(self.handleRevokeSuccess);
    };
  }

  angular
      .module('dashboardApp')
      .component(
          'tokensAccesslist',
          {
            require : {
              $parent : '^tokens'
            },
            bindings: {
              clients: '=',
              users: '='
            },
            templateUrl : '/resources/iam/js/dashboard-app/components/tokens/accesslist/tokens.accesslist.component.html',
            controller : [ '$q', '$rootScope', '$uibModal', 'ModalService',
                'TokensService', 'scimFactory', 'clipboardService', 'Utils', 'toaster', AccessTokensListController ]
          });
})();