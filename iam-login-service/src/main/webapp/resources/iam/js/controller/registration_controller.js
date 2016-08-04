'use strict';

RegistrationApp
		.controller(
				'RequestManagementController',
				[
						'$scope',
						'RegistrationRequestService',
						function($scope, RegistrationRequestService) {
							var self = this;

							$scope.list = [];
							$scope.sortType = 'status';
							$scope.sortReverse = false;

							self.listRequests = function(status) {
								RegistrationRequestService
										.listRequests(status)
										.then(
												function(result) {
													self.list = result.data;
												},
												function(errResponse) {
													$scope.textAlert = errResponse.data.error_description
															|| errResponse.data.detail;
													$scope.showErrorAlert = true;
												})
							};

							self.approveRequest = function(uuid) {
								RegistrationRequestService
										.updateRequest(uuid, 'APPROVED')
										.then(
												function() {
													$scope.textAlert = "Approvation success";
													$scope.showSuccessAlert = true;
													self.listRequests();
												},
												function(errResponse) {
													$scope.textAlert = errResponse.data.error_description
															|| errResponse.data.detail;
													$scope.showErrorAlert = true;
												})
							};

							self.rejectRequest = function(uuid) {
								RegistrationRequestService
										.updateRequest(uuid, 'REJECTED')
										.then(
												function() {
													$scope.textAlert = "Operation success";
													$scope.showSuccessAlert = true;
													self.listRequests();
												},
												function(errResponse) {
													$scope.textAlert = errResponse.data.error_description
															|| errResponse.data.detail;
													$scope.showErrorAlert = true;
												})
							};
							
							// switch flag
							$scope.switchBool = function(value) {
								$scope[value] = !$scope[value];
							};

						} ]);

RegistrationApp
		.controller(
				'RegistrationController',
				[
						'$scope',
						'$uibModalInstance',
						'RegistrationRequestService',
						function($scope, $uibModalInstance,
								RegistrationRequestService) {
							var self = this;
							$scope.user = {
								schemas : [
										"urn:ietf:params:scim:schemas:core:2.0:User",
										"urn:indigo-dc:scim:schemas:IndigoUser" ],
								name : {
									givenName : '',
									familyName : '',
								},
								active : "false",
								userName : '',
								emails : [ {
									type : "work",
									value : '',
									primary : "true",
								} ],
							};

							$scope.list = [];
							$scope.sortType = 'status';
							$scope.sortReverse = false;

							$scope.createUser = function(user) {
								RegistrationRequestService
										.create(user)
										.then(
												function(response) {
													$scope.textAlert = "Registration success";
													$scope.showSuccessAlert = true;
													$scope.showErrorAlert = false;
													$scope.reset();
													return response.data;
												},
												function(errResponse) {
													$scope.textAlert = errResponse.data.error_description
															|| errResponse.data.detail;
													$scope.showErrorAlert = true;
													$scope.showSuccessAlert = false;
													return $q
															.reject(errResponse);
												})
							};

							$scope.submit = function() {
								$scope.createUser($scope.user);
							};

							$scope.reset = function() {
								$scope.user.name = {
									givenName : '',
									familyName : '',
								};
								$scope.user.userName = '';
								$scope.user.emails = [ {
									type : "work",
									value : '',
									primary : "true",
								} ];
								$scope.registrationForm.$setPristine();
							};

							$scope.dismiss = function() {
								$uibModalInstance.close();
							};

							// switch flag
							$scope.switchBool = function(value) {
								$scope[value] = !$scope[value];
							};
						} ]);

RegistrationApp.controller('RegistrationFormModalController', [ '$scope',
		'$uibModal', function($scope, $uibModal) {

			$scope.open = function() {
				$uibModal.open({
					templateUrl : "/resources/iam/template/registration.html",
					controller : "RegistrationController",
					size : 'lg',
					animation : true
				});
			};
		} ]);
