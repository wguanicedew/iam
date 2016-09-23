angular.module('dashboardApp').directive('iamUsernameAvailableValidator', function($http, $q){
	return{
		require: 'ngModel',
		link: function($scope, element, attrs, ngModel){
			ngModel.$validators.uniqueUsername = function(username){
				if(username && ngModel.$dirty){
					return $http.get('/registration/username-available/'+username).then(
						function(response){
							ngModel.$setValidity('unique', response.data);
						});
				}
			};
		}
	}
});

angular.module('dashboardApp').directive('iamEmailAvailableValidator', function($http, $q){
	return{
		require: 'ngModel',
		link: function($scope, element, attrs, ngModel){
			ngModel.$validators.uniqueEmail = function(email){
				if(email){
					return $http.get('/registration/email-available/'+email).then(
						function(response){
							ngModel.$setValidity('unique', response.data);
						});
				}
			};
		}
	}
});
