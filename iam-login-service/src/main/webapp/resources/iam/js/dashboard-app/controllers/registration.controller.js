'use strict';

angular.module('dashboardApp').controller('RequestManagementController', RequestManagementController);

RequestManagementController.$inject = ['$scope', '$state', 'RegistrationRequestService'];

function RequestManagementController($scope, $state, RegistrationRequestService){
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
				console.log(errResponse);
				$state.go("error", {
					"error" : errResponse
				});
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