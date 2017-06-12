'use strict';

angular.module('dashboardApp').controller('GroupController', GroupController);

GroupController.$inject = [ '$scope', '$rootScope', '$state', '$filter', 'scimFactory', '$uibModal', 'ModalService', 'Utils' ];

function GroupController($scope, $rootScope, $state, $filter, scimFactory, $uibModal, ModalService, Utils) {

	var group = this;

	group.loadGroup = loadGroup;
	group.clickToOpen = clickToOpen;

	group.id = $state.params.id;
	group.data = [];

	group.loadGroup();

	function loadGroup() {

		$rootScope.pageLoadingProgress = 30;
		group.loadingModal = $uibModal
		.open({
			animation: false,
			templateUrl : '/resources/iam/template/dashboard/loading-modal.html'
		});

		group.loadingModal.opened.then(function() {
			scimFactory.getGroup(group.id).then(
					function(response) {

						group.data = response.data;
						group.data.members = $filter('orderBy')(group.data.members, "display", false);
						$rootScope.pageLoadingProgress = 100;
						group.loadingModal.dismiss("Dismiss");

					}, function(error) {

						$rootScope.pageLoadingProgress = 100;
						$scope.operationResult = Utils.buildErrorOperationResult(error);
						group.loadingModal.dismiss("Dismiss");

					});
		});
	}

	group.removeMemberFromList = removeMemberFromList;

	function removeMemberFromList(member) {

		var i = group.data.members.indexOf(member);
		group.data.members.splice(i, 1);
	}

	group.deleteMember = deleteMember;

	function deleteMember(member) {

		var modalOptions = {
				closeButtonText: 'Cancel',
				actionButtonText: 'Remove membership',
				headerText: 'Remove «' + member.display + "» from «" + group.data.displayName + "»",
				bodyText: `Are you sure you want to remove '${member.display}' from '${group.data.displayName}'?`	
			};
				
			ModalService.showModal({}, modalOptions).then(
				function (){
					scimFactory.removeMemberFromGroup(group.id, member.value, member.$ref,
							member.display)
						.then(function(response) {
							console.log("Deleted: ", member.display);
							group.removeMemberFromList(member);
							$scope.operationResult = Utils.buildSuccessOperationResult("Member " + member.display + " has been removed successfully");
						}, function(error) {
							$scope.operationResult = Utils.buildErrorOperationResult(error);
						});
				});
	}
	
	function clickToOpen() {
		var modalInstance = $uibModal.open({
			templateUrl: '/resources/iam/template/dashboard/group/addsubgroup.html',
			controller: 'AddSubGroupController',
			controllerAs: 'addSubGroupCtrl',
			resolve: {group: function() { return group.data; }}
		});
		modalInstance.result.then(function(createdGroup) {
			console.info(createdGroup);
			group.loadGroup();
		}, function() {
			console.info('Modal dismissed at: ', new Date());
		});
	}
}