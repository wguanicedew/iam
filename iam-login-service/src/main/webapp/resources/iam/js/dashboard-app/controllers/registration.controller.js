'use strict';

angular.module('dashboardApp').controller('RequestManagementController', RequestManagementController);

RequestManagementController.$inject = ['$scope', '$rootScope', '$state', '$filter', 'RegistrationRequestService', 'ModalService'];

function RequestManagementController($scope, $rootScope, $state, $filter, RegistrationRequestService, ModalService){

	var vm = this;
	vm.operationResult;
	vm.textAlert;
	
	vm.listRequests = listRequests;
	vm.listPending = listPending;
	vm.approveRequest = approveRequest;
	vm.rejectRequest = rejectRequest;
	vm.init = init;

	vm.list = [];
	vm.sortType = 'creationTime';
	vm.sortReverse = true;
	vm.currentPage = 1;
	vm.maxSize = 5;
	vm.numPerPage = 10;

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
				$rootScope.loggedUser.pendingRequests = result.data;
			},
			function(errResponse) {
				vm.textAlert = errResponse.data.error_description || errResponse.data.detail;
				vm.operationResult = 'err';
				$state.go("error", {
					"error" : errResponse
				});
			})
	};

	function approveRequest(request) {
		RegistrationRequestService.updateRequest(request.uuid, 'APPROVED').then(
			function() {
				vm.textAlert = `${request.givenname} ${request.familyname} request approved successfully`;
				vm.operationResult = 'ok';
				vm.listPending();
			},
			function(errResponse) {
				vm.textAlert = errResponse.data.error_description || errResponse.data.detail;
				vm.operationResult = 'err';
			})
	};

	function rejectRequest(request) {
		
		var modalOptions = {
			closeButtonText: 'Cancel',
            actionButtonText: 'Reject Request',
            headerText: 'Reject?',
            bodyText: `Are you sure you want to reject request for ${request.givenname} ${request.familyname}?`	
		};
		
		ModalService.showModal({}, modalOptions).then(
				function (){
					RegistrationRequestService.updateRequest(request.uuid, 'REJECTED').then(
							function() {
								vm.textAlert = `${request.givenname} ${request.familyname} request rejected successfully`;
								vm.operationResult = 'ok';
								vm.listPending();
							},
							function(errResponse) {
								vm.textAlert = errResponse.data.error_description || errResponse.data.detail;
								vm.operationResult = 'err';
							})
				});
	};
};
