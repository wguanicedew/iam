'use strict';

angular.module('dashboardApp').controller('GroupsController', GroupsController);

GroupsController.$inject = ['$scope', '$location', '$uibModal', '$state', '$filter', 'filterFilter', 'scimFactory'];

function GroupsController($scope, $location, $uibModal, $state, $filter, filterFilter, scimFactory) {

	var gc = this;

	// group data
	gc.groups = [];
	gc.getAllGroups = getAllGroups;
	
	// messages/alert
	gc.alerts = [];
	gc.addAlert = addAlert;
	gc.closeAlert = closeAlert;

	// create empty search model (object) to trigger $watch on update
	gc.search = {};

	// pagination controls
	gc.currentPage = 1;
	gc.totalItems = gc.groups.length;
	gc.entryLimit = 10; // items per page

	// functions
	gc.resetFilters = resetFilters;
	gc.updateNoOfPages = updateNoOfPages;
	gc.updateTotalItems = updateTotalItems;
	
	// add group
	gc.clickToOpen = clickToOpen;

	// delete group
	gc.deleteGroup = deleteGroup;
	gc.removeGroup = removeGroup;
	
	// Controller actions:
	gc.resetFilters()
	gc.getAllGroups(1, gc.entryLimit); // eval gc.groups

	function updateTotalItems() {

		gc.totalItems = gc.groups.length;
	}

	function updateNoOfPages() {

		gc.noOfPages = Math.ceil(gc.totalItems / gc.entryLimit);
	}

	function resetFilters() {
		// needs to be a function or it won't trigger a $watch
		gc.search = {};
	};

	// $watch search to update pagination
	$scope.$watch('gc.search', function (newVal, oldVal) {
		gc.filtered = filterFilter(gc.groups, newVal);
		gc.updateTotalItems();
		gc.updateNoOfPages();
		gc.currentPage = 1;
	}, true);

	function getAllGroups(startIndex, count) {

		scimFactory.getGroups(startIndex, count)
			.then(function(response) {
				angular.forEach(response.data.Resources, function(group) {
					gc.groups.push({
						displayName: group.displayName,
						id: group.id
					});
				});
				gc.groups = $filter('orderBy')(gc.groups, "displayName", false);
				gc.updateTotalItems();
				gc.updateNoOfPages();
				if (response.data.totalResults > (response.data.startIndex + response.data.itemsPerPage)) {
					gc.getAllGroups(startIndex + count, count);
				}
			},function(error) {
				$state.go("error", { "errCode": error.status, "errMessage": error.statusText });
			});
	}

	function clickToOpen() {
		var modalInstance = $uibModal.open({
			templateUrl: '/resources/iam/template/dashboard/newgroup.html',
			controller: 'AddGroupController'
		});
		modalInstance.result.then(function(createdGroup) {
			console.info(createdGroup);
			gc.groups.push({
				displayName: createdGroup.displayName,
				id: createdGroup.id
			});
			gc.groups = $filter('orderBy')(gc.groups, "displayName", false);
			gc.updateTotalItems();
			gc.updateNoOfPages();
			gc.addAlert(new Date(), "success", "Created","Group '" + createdGroup.displayName + "' has been created");
		}, function () {
			console.info('Modal dismissed at: ' + new Date());
		});
	};

	function removeGroup(groupIdToRemove) {
		
		console.log("looking for " + groupIdToRemove);
		var found = $filter('getById')(gc.groups, groupIdToRemove);
		
		if (found == null) {
			$state.go("error", { "errCode": error.status, "errMessage": error.statusText });
			return;
		}

		console.log("Deleting element at ", found[0], gc.groups[found[0]]);
		// remove using position
		gc.groups.splice(found[0], 1);
		
		gc.filtered = filterFilter(gc.groups, gc.search);
		gc.updateTotalItems();
		gc.updateNoOfPages();
	}

	function addAlert(time, type, title, text) {
		
		gc.alerts.push({
			"time": time,
			"type": type,
			"title": title,
			"text": text
		});
	}
	
	function closeAlert(index) {
		
		gc.alerts.splice(index, 1);
	}
	
	function deleteGroup(group) {
		scimFactory
		.deleteGroup(group.id)
			.then(
				function(response) {
					alert("deleted by remote backend!");
					gc.removeGroup(group.id);
					gc.addAlert(new Date(), "danger", "Deleted", "Group '" + group.displayName + "' has been deleted");
				},
				function(errorResponse) {
					console.info(errorResponse);
					console.error('Error deleting group: ' + errorResponse.data.detail);
					gc.addAlert(new Date(), "danger", "Error on deleting!", "Group '" + group.displayName + "' not removed: " + errorResponse.data.detail);
				}
			);
	};
}