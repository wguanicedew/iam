'use strict';

angular.module('registrationApp').factory('RegistrationRequestService', RegistrationRequestService);

RegistrationRequestService.$inject = ['$http'];

function RegistrationRequestService($http){
	
	var service = {
		create : create,
		listRequests : listRequests,
		updateRequest : updateRequest
	};

	return service;

	function create(user) {
		var config = {
			headers : {
				'Accept' : 'application/scim+json',
				'Content-Type' : 'application/scim+json'
			},
		}
		return $http.post('/registration', user, config);
	}
	;

	function listRequests(status) {
		return $http.get('/registration', {
			params : {
				status : status
			}
		});
	}
	;

	function updateRequest(uuid, decision) {
		return $http.post('/registration/' + uuid + '/' + decision);
	}
	;
};
