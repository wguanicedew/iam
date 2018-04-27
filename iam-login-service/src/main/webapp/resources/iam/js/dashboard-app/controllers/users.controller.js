/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

angular.module('dashboardApp').controller('UsersController', UsersController);

UsersController.$inject = [ '$scope', '$rootScope', '$uibModal', '$state', '$filter','$q',
		'filterFilter', 'scimFactory', 'ModalService' , 'Utils'];

function UsersController($scope, $rootScope, $uibModal, $state, $filter, $q, filterFilter,
		scimFactory, ModalService, Utils) {

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
	users.openAddUserDialog = openAddUserDialog;
	users.deleteUser = deleteUser;
	users.removeUserFromList = removeUserFromList;
	users.loadAllUsers = loadAllUsers;

	users.loaded = undefined;

	// Controller actions:
	users.resetFilters();
	users.loadAllUsers();

	function resetFilters() {
		// needs to be a function or it won't trigger a $watch
		users.searchText = "";
	}

	function rebuildFilteredList() {
		
		users.filtered = filterFilter(users.list, function(user) {

			if (!users.searchText) {
				return true;
			}

			var query = users.searchText.toLowerCase();

			if (user.displayName.toLowerCase().indexOf(query) != -1) {
				return true;
			}
			if (user.name.formatted.toLowerCase().indexOf(query) != -1) {
				return true;
			}
			if (user.userName.toLowerCase().indexOf(query) != -1) {
				return true;
			}
			if (user.emails[0].value.toLowerCase().indexOf(query) != -1) {
				return true;
			}
			if (user.id.toLowerCase().indexOf(query) != -1) {
				return true;
			}
			return false;
		});
		users.filtered = $filter('orderBy')(users.filtered,	"name.formatted", false);
	}

	$scope.$watch('users.searchText', function() {

		users.rebuildFilteredList();
	});
	
	function loadAllUsers() {
		users.loaded = false;
		
		var promises = [];
		var chunkRequestSize = 100;
		
		var handleResponse = function(response){
			angular.forEach(response.data.Resources, function(user){
				users.list.push(user);
			});
		};
		
		var handleError = function(error) {
			users.loadingModal.dismiss("Error");
			$scope.operationResult = Utils.buildErrorOperationResult(error);
		};
		
		var handleFirstResponse = function(response){
			var totalResults = response.data.totalResults;
			var lastLoaded = chunkRequestSize;
			
			while (lastLoaded < totalResults) {
				promises.push(scimFactory.getUsers(lastLoaded+1, chunkRequestSize));
				lastLoaded = lastLoaded + chunkRequestSize;
			}
			
			angular.forEach(response.data.Resources, function(user){
				users.list.push(user);
			});
			
			$q.all(promises).then(function(response){
				angular.forEach(promises, function(p){
					p.then(handleResponse);
				});
				$rootScope.pageLoadingProgress = 100;
				users.loaded = true;
				users.rebuildFilteredList();
				users.loadingModal.dismiss("Cancel");
			}, handleError);
		};
		
		$rootScope.pageLoadingProgress = 0;
		
		users.loadingModal = $uibModal.open({
			animation: false,
			templateUrl : '/resources/iam/template/dashboard/loading-modal.html'
		});
		
		users.loadingModal.opened.then(function(){
			scimFactory.getUsers(1, chunkRequestSize).then(handleFirstResponse,	handleError);
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
            headerText: 'Delete user «' + user.name.formatted + '»',
            bodyText: `Are you sure you want to delete user '${user.name.formatted}'?`	
		};
		
		ModalService.showModal({}, modalOptions).then(
				function (){
					scimFactory.deleteUser(user.id).then(
						function(response) {
							users.removeUserFromList(user);
							$rootScope.loggedUser.totUsers = $rootScope.loggedUser.totUsers -1;
							$scope.operationResult = Utils.buildSuccessOperationResult("User " + user.displayName + " has been removed successfully");
						}, function(error) {
							$scope.operationResult = Utils.buildErrorOperationResult(error);
						});
				});
	};
}
