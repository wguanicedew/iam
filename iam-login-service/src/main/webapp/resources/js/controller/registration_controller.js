'use strict';

RegistrationApp.controller('RegistrationController', [ '$scope', 'RegistrationRequestService', function($scope, RegistrationRequestService){
	var self = this;
	self.user = {
			schemas: [ "urn:ietf:params:scim:schemas:core:2.0:User",
			           "urn:indigo-dc:scim:schemas:IndigoUser" ],
			name : { givenName : '', familyName : '', },
			active : "false",
			userName : '',
			emails : [{ type : "work", value : '', primary : "true", }],
	};
	
	self.createUser = function(user){
		RegistrationRequestService.create(user);
	};
	
	self.submit = function(){
		self.createUser(self.user);
//		self.reset();
	};
	
	self.reset = function(){
		self.user.name = { givenName : '', familyName : '', };
		self.user.userName = '';
		self.user.emails = [{ type : "work", value : '', primary : "true", }];
		$scope.registrationForm.$setPristine();
	};
	
}]);