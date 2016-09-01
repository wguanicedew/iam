'use strict';

angular.module('registrationApp').controller('RequestManagementController', RequestManagementController);

RequestManagementController.$inject = ['$scope', 'RegistrationRequestService'];

function RequestManagementController($scope, RegistrationRequestService){
	var vm = this;
	vm.operationResult;
	vm.textAlert;
	
	vm.listRequests = listRequests;
	vm.listPending = listPending;
	vm.approveRequest = approveRequest;
	vm.rejectRequest = rejectRequest;
	vm.init = init;
	vm.elementToDisplay = elementToDisplay;
	vm.pageChanged = pageChanged;

	vm.list = [];
	vm.filteredList = [];
	vm.sortType = 'creationTime';
	vm.sortReverse = true;
	vm.currentPage = 1;
	vm.maxSize = 5;
	vm.numPerPage = 10;
	
	
	function elementToDisplay(){
		var begin = ((vm.currentPage - 1) * vm.numPerPage);
	    var end = begin + vm.numPerPage;

	    vm.filteredList = vm.list.slice(begin, end);
	}
	
	function pageChanged(){
		vm.elementToDisplay();
	}
	
	function init(){
		vm.listPending();
	};

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
	
	function listPending() {
		RegistrationRequestService.listPending().then(
			function(result) {
				vm.list = result.data;
				vm.elementToDisplay();
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
				vm.listPending();
			},
			function(errResponse) {
				vm.textAlert = errResponse.data.error_description || errResponse.data.detail;
				vm.operationResult = 'err';
			})
	};

	function rejectRequest(uuid) {
		RegistrationRequestService.updateRequest(uuid, 'REJECTED').then(
			function() {
				vm.textAlert = "Rejection success";
				vm.operationResult = 'ok';
				vm.listPending();
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
