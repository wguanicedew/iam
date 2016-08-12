'use strict'

angular.module('passwordResetApp').factory('ResetPasswordService', ResetPasswordService);

ResetPasswordService.$inject = ['$http'];

function ResetPasswordService($http){
	
	var service = {
		forgotPassword : forgotPassword
	};
	return service;
	
	
	function forgotPassword(email){
		return $http.get('/iam/password-forgot/'+email);
	};
}