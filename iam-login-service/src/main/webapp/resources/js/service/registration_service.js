'use strict';

RegistrationApp.factory('RegistrationRequestService', ['$http', '$q', function($http, $q){
	
	return {
		create: function(user){
			var config = {
					headers : {
						'Accept': 'application/scim+json',
				        'Content-Type': 'application/scim+json'
					}
			}
			return $http.post('http://localhost:8080/registration', user, config)
				.then(
						function success(response){
							alert('Request successfully saved. Check your mail.');
							return response.data;
						},
						function error(errResponse){
							console.error('Error creating user');
							return $q.reject(errResponse);
						}
				);
		},
		
	};
	
}]);