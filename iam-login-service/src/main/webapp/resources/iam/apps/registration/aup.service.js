'use strict';

angular.module('registrationApp').factory('Aup', Aup);

Aup.$inject = ['$http', '$q'];

function Aup($http, $q) {

    var service = {
        getAup: getAup
    };

    return service;

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