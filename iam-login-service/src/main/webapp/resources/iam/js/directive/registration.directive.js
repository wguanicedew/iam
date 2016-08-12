angular.module('registrationApp').directive('iamUsernameAvailableValidator', function($http, $q){
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