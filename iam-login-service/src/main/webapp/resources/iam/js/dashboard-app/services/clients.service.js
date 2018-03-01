'use strict'

angular.module('dashboardApp').factory('ClientsService', ClientsService);

ClientsService.$inject = [ '$http' ];

function ClientsService($http) {
    var service = {
        getClientList : getClientList
    };

    var urlClients = "/api/clients";

    return service;

    function getClientList() {
        console.info("Getting client list");
        return $http.get(urlClients);
    }
}