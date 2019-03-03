(function () {
    'use strict';
    angular.module('dashboardApp').factory('AccountLabelsService', AccountLabelsService);

    AccountLabelsService.$inject = ['$q', '$http', 'Utils'];

    function AccountLabelsService($q, $http, Utils) {

        var service = {
            getAccountLabels: getAccountLabels,
            getAccountLabelsForAuthenticatedUser: getAccountLabelsForAuthenticatedUser
        };

        return service;

        function getAccountLabels(uuid) {
            return $http.get("/iam/account/" + uuid + "/labels").then(
                function (res) {
                    return res.data;
                }).catch(function (res) {
                return $q.reject(res);
            });
        }

        function getAccountLabelsForAuthenticatedUser() {
            var accountId = Utils.getLoggedUser().info.sub;
            return getAccountLabels(accountId);
        }
    }
})();