(function() {
    'use strict';

    angular.module('dashboardApp')
        .factory('AupService', AupService);

    AupService.$inject = ['$http', '$q'];

    function AupService($http, $q) {

        var service = {
            getAup: getAup,
            createAup: createAup,
            updateAup: updateAup,
            deleteAup: deleteAup,
            getAupSignature: getAupSignature,
            getAupSignatureForUser: getAupSignatureForUser
        };

        return service;

        function deleteAup() {
            return $http.delete('/iam/aup');
        }

        function createAup(aup) {
            return $http.post('/iam/aup', aup);
        }

        function updateAup(aup) {
            return $http.patch('/iam/aup', aup);
        }

        function getAupSignatureForUser(userId) {
            return $http.get('/iam/aup/signature/' + userId).catch(function(res) {
                if (res.status == 404) {
                    console.info("AUP signature not found");
                    return null;
                }
                return $q.reject(res);
            });
        }

        function getAupSignature() {
            return $http.get('/iam/aup/signature').catch(function(res) {
                if (res.status == 404) {
                    console.info("AUP signature not found");
                    return null;
                }
                return $q.reject(res);
            });
        }

        function getAup() {
            return $http.get('/iam/aup').catch(function(res) {
                if (res.status == 404) {
                    console.info("AUP not defined");
                    return null;
                }
                return $q.reject(res);
            });
        }
    }
})();