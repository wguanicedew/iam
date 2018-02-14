(function() {
    'use strict';

    angular.module('dashboardApp')
        .factory('AccountGroupManagerService', AccountGroupManagerService);

    AccountGroupManagerService.$inject = ['$http', '$q'];

    function AccountGroupManagerService($http, $q) {

        var service = {
            getAccountManagedGroups: getAccountManagedGroups,
            addManagedGroupToAccount: addManagedGroupToAccount,
            removeManagedGroupFromAccount: removeManagedGroupFromAccount,
            getGroupManagersForGroup: getGroupManagersForGroup
        };

        return service;

        function getAccountManagedGroups(accountId) {
            return $http.get("/iam/account/" + accountId + "/managed-groups").then(function(res) {
                return res.data;
            }).catch(function(res) {
                return $q.reject(res);
            });
        }
    }
})();