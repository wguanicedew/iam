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
	
	self.list = [];
	
	self.createUser = function(user){
		RegistrationRequestService.create(user).
			then(
				function(response){
					$scope.textAlert = "Registration success";
				    $scope.showSuccessAlert = true;
				    $scope.showErrorAlert = false;
				    self.reset();
					return response.data;
				},
				function(errResponse){
					$scope.textAlert = errResponse.data.error_description || errResponse.data.detail;
					$scope.showErrorAlert = true;
					$scope.showSuccessAlert = false;
					return $q.reject(errResponse);
				}
			)
	};
	
	self.listRequests = function(status){
		RegistrationRequestService.listRequests(status).
			then(
				function(result){
					self.list = result.data;
				},
				function(errResponse){
					$scope.textAlert = errResponse.data.error_description || errResponse.data.detail;
					$scope.showErrorAlert = true;
				}
			)
	};
	
	self.approveRequest = function(uuid){
		RegistrationRequestService.updateRequest(uuid, 'APPROVED').
			then(
				function(){
					$scope.textAlert = "Approvation success";
				    $scope.showSuccessAlert = true;
				},
				function(errResponse){
					$scope.textAlert = errResponse.data.error_description || errResponse.data.detail;
					$scope.showErrorAlert = true;
				}
			)
	};
	
	self.rejectRequest = function(uuid){
		RegistrationRequestService.updateRequest(uuid, 'REJECTED').
		then(
			function(){
				$scope.textAlert = "Operation success";
			    $scope.showSuccessAlert = true;
			},
			function(errResponse){
				$scope.textAlert = errResponse.data.error_description || errResponse.data.detail;
				$scope.showErrorAlert = true;
			}
		)
	};
	
	self.listRequests('NEW');
	
	self.submit = function(){
		self.createUser(self.user);
	};
	
	self.reset = function(){
		self.user.name = { givenName : '', familyName : '', };
		self.user.userName = '';
		self.user.emails = [{ type : "work", value : '', primary : "true", }];
		$scope.registrationForm.$setPristine();
	};

    // switch flag
    $scope.switchBool = function (value) {
        $scope[value] = !$scope[value];
    };
}]);
