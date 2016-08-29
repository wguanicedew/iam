'use strict';

angular.module('registrationApp').controller('RequestManagementController', RequestManagementController);

RequestManagementController.$inject = ['$scope', 'RegistrationRequestService'];

function RequestManagementController($scope, RegistrationRequestService){
	var vm = this;
	vm.operationResult;
	vm.textAlert;
	
	vm.listRequests = listRequests;
	vm.approveRequest = approveRequest;
	vm.rejectRequest = rejectRequest;

	vm.list = [];
	vm.sortType = 'status';
	vm.sortReverse = false;
	

	function listRequests(status) {
		RegistrationRequestService.listRequests(status).then(
				function(result) {
					vm.list = result.data;
				},
				function(errResponse) {
					vm.textAlert = errResponse.data.error_description || errResponse.data.detail;
					vm.operationResult = 'err';
				})
	};

	function approveRequest(uuid) {
		RegistrationRequestService.updateRequest(uuid, 'APPROVED').then(
				function() {
					vm.textAlert = "Approvation success";
					vm.operationResult = 'ok';
					vm.listRequests();
				},
				function(errResponse) {
					vm.textAlert = errResponse.data.error_description || errResponse.data.detail;
					vm.operationResult = 'err';
				})
	};

	function rejectRequest(uuid) {
		RegistrationRequestService.updateRequest(uuid, 'REJECTED').then(
				function() {
					vm.textAlert = "Operation success";
					vm.operationResult = 'ok';
					vm.listRequests();
				},
				function(errResponse) {
					vm.textAlert = errResponse.data.error_description || errResponse.data.detail;
					vm.operationResult = 'err';
				})
	};

};

angular.module('registrationApp').controller('RegistrationController', RegistrationController);

RegistrationController.$inject = [ '$scope', '$q', '$uibModalInstance', '$window', 'RegistrationRequestService' ];

function RegistrationController($scope, $q, $uibModalInstance, $window, RegistrationRequestService) {
	$scope.request = {
		givenname : '',
		familyname : '',
		username : '',
		email : '',
		notes : '',
	};

	$scope.list = [];
	$scope.sortType = 'status';
	$scope.sortReverse = false;
	
	$scope.textAlert;
	$scope.operationResult;

	$scope.createRequest = createRequest; 
	$scope.submit = submit;
	$scope.reset = reset;
	$scope.dismiss = dismiss;
	
		
	function createRequest(request) {
		RegistrationRequestService.createRequest(request).then(
			function() {
				$window.location.href = "/registration/submitted";
			},
			function(errResponse) {
				$scope.operationResult = 'err';
				$scope.textAlert = errResponse.data.error_description || errResponse.data.detail;
				return $q.reject(errResponse);
			})
	};

	function submit() {
		$scope.createRequest($scope.request);
	};

	function reset() {
		$scope.request = {
			givenname : '',
			familyname : '',
			username : '',
			email : '',
			notes : '',
		};
		$scope.registrationForm.$setPristine();
	};

	function dismiss() {
		$uibModalInstance.close();
	};
};

angular.module('registrationApp').controller('RegistrationFormModalController', RegistrationFormModalController);

RegistrationFormModalController.$inject = [ '$scope', '$uibModal' ];

function RegistrationFormModalController($scope, $uibModal) {
	$scope.open = function() {
		$uibModal.open({
			templateUrl : "/resources/iam/template/registration.html",
			controller : "RegistrationController",
			size : '600px',
			animation : true
		});
	};
};
