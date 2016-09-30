'use strict'

angular.module('passwordResetApp').factory('ResetPasswordService', ResetPasswordService);

ResetPasswordService.$inject = ['$http', '$httpParamSerializerJQLike'];

function ResetPasswordService($http, $httpParamSerializerJQLike){
	
	var service = {
		forgotPassword : forgotPassword,
		changePassword : changePassword,
	};
	
	return service;
	
	function forgotPassword(email){
		
		var data = $httpParamSerializerJQLike({
				email: email
		});
		
		var config = {
			headers : {
				'Content-Type' : 'application/x-www-form-urlencoded'
			}
		} 
		
		return $http.post('/iam/password-reset/token', data, config);
		
	};
	
	function changePassword(resetKey, newPassword){
		var data = $httpParamSerializerJQLike({
			token : resetKey,
			password : newPassword
		});
		
		var config = {
			headers : {
				'Content-Type': 'application/x-www-form-urlencoded'
			}
		}
		
		return $http.post('/iam/password-reset', data, config);
	}
}