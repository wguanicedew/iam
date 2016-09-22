'use strict';

angular.module('dashboardApp').controller('UsersController', UsersController);

UsersController.$inject = [ '$scope', '$rootScope', '$uibModal', '$state', '$filter',
		'filterFilter', 'scimFactory', 'ModalService' ];

function UsersController($scope, $rootScope, $uibModal, $state, $filter, filterFilter,
		scimFactory, ModalService) {

	var users = this;

	// all the users list
	users.list = [];
	// filtered users to show
	users.filtered = [];
	// text to find to filter users
	users.searchText = "";

	// pagination controls
	users.currentPage = 1;
	users.entryLimit = 10; // items per page

	// functions
	users.resetFilters = resetFilters;
	users.rebuildFilteredList = rebuildFilteredList;
	users.getAllUsers = getAllUsers;
	users.openAddUserDialog = openAddUserDialog;
	users.deleteUser = deleteUser;
	users.removeUserFromList = removeUserFromList;
	users.loadUserList = loadUserList;

	// Controller actions:
	users.resetFilters();
	users.loadUserList();

	function resetFilters() {
		// needs to be a function or it won't trigger a $watch
		users.searchText = "";
	}

	function rebuildFilteredList() {
		
		users.filtered = filterFilter(users.list, {$: users.searchText});
		users.filtered = $filter('orderBy')(users.filtered,	"name.formatted", false);
	}

	$scope.$watch('users.searchText', function() {

		users.rebuildFilteredList();
	});

	function loadUserList() {

		$rootScope.usersLoadingProgress = 0;
		users.loadingModal = $uibModal
		.open({
			animation: false,
			templateUrl : '/resources/iam/template/dashboard/users/loading-modal.html'
		});

		users.loadingModal.opened.then(function() {
			getAllUsers(1, users.entryLimit);
		});
	}

	function getAllUsers(startIndex, count) {

		scimFactory
				.getUsers(startIndex, count)
				.then(
						function(response) {
							
							angular.forEach(response.data.Resources, function(
									user) {
								users.list.push(user);
							});
							
							users.rebuildFilteredList();
							
							if (response.data.totalResults > (response.data.startIndex - 1 + response.data.itemsPerPage)) {
							
								users.getAllUsers(startIndex + count, count);
								$rootScope.usersLoadingProgress = Math.floor((startIndex + count) * 100 / response.data.totalResults);
							
							} else {
							
								$rootScope.usersLoadingProgress = 100;
								users.loadingModal.dismiss("Cancel");
							
							}
						}, function(error) {
							
							users.loadingModal.dismiss("Error");
							users.textAlert = error.data.error_description || error.data.detail;
							users.operationResult = 'err';
						
						});
	}

	function openAddUserDialog() {

		var modalInstance = $uibModal
				.open({
					templateUrl : '/resources/iam/template/dashboard/users/newuser.html',
					controller : 'AddUserController',
					controllerAs : 'addUserCtrl'
				});
		modalInstance.result.then(function(createdUser) {
			console.info(createdUser);
			users.list.push(createdUser);
			users.rebuildFilteredList();
		}, function() {
			console.info('Modal dismissed at: ', new Date());
		});
	}
	
	function removeUserFromList(user) {

		var i = users.list.indexOf(user);
		users.list.splice(i, 1);

		users.rebuildFilteredList();
	}
	
	function deleteUser(user) {
		
		var modalOptions = {
			closeButtonText: 'Cancel',
            actionButtonText: 'Delete User',
            headerText: 'Delete?',
            bodyText: `Are you sure you want to delete user '${user.name.formatted}'?`	
		};
		
		ModalService.showModal({}, modalOptions).then(
				function (){
					scimFactory.deleteUser(user.id).then(
						function(response) {
							users.removeUserFromList(user);
							$rootScope.loggedUser.totUsers = $rootScope.loggedUser.totUsers -1;
							users.textAlert = `User ${user.displayName} deleted successfully`;
							users.operationResult = 'ok';
						}, function(error) {
							users.textAlert = error.data.error_description || error.data.detail;
							users.operationResult = 'err';
						});
				});
	};
}
