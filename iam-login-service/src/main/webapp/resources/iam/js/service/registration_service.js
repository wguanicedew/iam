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
			return $http.post('/registration', user, config);
		},
		
		listRequests: function(status){
			return $http.get('/registration', {params: {status: status}});
		},
		
		updateRequest: function(uuid, decision){
			return $http.post('/registration/'+uuid+'/'+decision);
		},
		
	};
}]);

RegistrationApp.directive('usernameAvailableValidator', function($http, $q){
	return{
		require: 'ngModel',
		link: function($scope, element, attrs, ngModel){
			ngModel.$asyncValidators.uniqueUsername = function(username){
				return $http.get('/registration/username-available/'+username).then(
						function(response){
							if(response.data === true){
								return true;
							}
							return $q.reject('exists');
						});
			};
		}
	}
});
