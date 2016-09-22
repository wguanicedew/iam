'use strict';

angular.module('dashboardApp').controller('RequestManagementController', RequestManagementController);

RequestManagementController.$inject = ['$scope', '$rootScope', '$state', '$filter', 'filterFilter', '$uibModal', 'RegistrationRequestService', 'ModalService'];

function RequestManagementController($scope, $rootScope, $state, $filter, filterFilter, $uibModal, RegistrationRequestService, ModalService){

	var requests = this;
	requests.operationResult;
	requests.textAlert;
	
//	requests.listRequests = listRequests;
	requests.listPending = listPending;
	requests.approveRequest = approveRequest;
	requests.rejectRequest = rejectRequest;
	requests.loadData = loadData;

	requests.list = [];
	requests.filtered = [];
	requests.sortType = 'creationTime';
	requests.sortReverse = true;
	requests.currentPage = 1;
	requests.maxSize = 5;
	requests.numPerPage = 10;

	// search
	requests.searchText = "";
	requests.resetFilters = resetFilters;
	requests.rebuildFilteredList = rebuildFilteredList;

	function resetFilters() {
		// needs to be a function or it won't trigger a $watch
		requests.searchText = "";
	}

	function rebuildFilteredList() {

		requests.filtered = filterFilter(requests.list, function(request) {

			if (!requests.searchText) {
				return true;
			}

			var query = requests.searchText.toLowerCase();
			
			if (request.familyname.toLowerCase().indexOf(query) != -1) {
				return true;
			}
			if (request.givenname.toLowerCase().indexOf(query) != -1) {
				return true;
			}
			if (request.username.toLowerCase().indexOf(query) != -1) {
				return true;
			}
			if (request.email.toLowerCase().indexOf(query) != -1) {
				return true;
			}
			if (request.notes.toLowerCase().indexOf(query) != -1) {
				return true;
			}
			return false;
		});
	}

	$scope.$watch('requests.searchText', function() {

		requests.rebuildFilteredList();
	});

	function loadData() {
		
		$rootScope.requestsLoadingProgress = 0;
		
		requests.loadingModal = $uibModal
		.open({
			animation: false,
			templateUrl : '/resources/iam/template/dashboard/requests/loading-modal.html'
		});

		requests.loadingModal.opened.then(function() {
		
			RegistrationRequestService.listPending().then(
				function(result) {
					requests.list = result.data;
					requests.rebuildFilteredList();
					$rootScope.requestsLoadingProgress = 100;
					$rootScope.loggedUser.pendingRequests = result.data;
					
					requests.loadingModal.dismiss("Cancel");
				},
				function(errResponse) {
					requests.textAlert = errResponse.data.error_description || errResponse.data.detail;
					requests.operationResult = 'err';
					
					requests.loadingModal.dismiss("Error");
				});
		});
	}

	function listPending() {
		RegistrationRequestService.listPending().then(
			function(result) {
				requests.list = result.data;
				requests.rebuildFilteredList();
				$rootScope.loggedUser.pendingRequests = result.data;
			},
			function(errResponse) {
				requests.textAlert = errResponse.data.error_description || errResponse.data.detail;
				requests.operationResult = 'err';
			})
	};

	function approveRequest(request) {
		RegistrationRequestService.updateRequest(request.uuid, 'APPROVED').then(
			function() {
				requests.textAlert = `${request.givenname} ${request.familyname} request approved successfully`;
				requests.operationResult = 'ok';
				requests.listPending();
			},
			function(errResponse) {
				requests.textAlert = errResponse.data.error_description || errResponse.data.detail;
				requests.operationResult = 'err';
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
								requests.textAlert = `${request.givenname} ${request.familyname} request rejected successfully`;
								requests.operationResult = 'ok';
								requests.listPending();
							},
							function(errResponse) {
								requests.textAlert = errResponse.data.error_description || errResponse.data.detail;
								requests.operationResult = 'err';
							})
				});
	};
};
