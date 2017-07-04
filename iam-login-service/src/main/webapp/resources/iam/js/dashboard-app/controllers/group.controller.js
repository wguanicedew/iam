'use strict';

angular.module('dashboardApp').controller('GroupController', GroupController);

GroupController.$inject = [ '$scope', '$rootScope', '$state', '$filter', 'scimFactory', '$uibModal', 'ModalService', 'Utils', 'toaster' ];

function GroupController($scope, $rootScope, $state, $filter, scimFactory, $uibModal, ModalService, Utils, toaster) {

	var group = this;

	group.loadGroup = loadGroup;
	group.openAddSubgroupDialog = openAddSubgroupDialog;

	group.id = $state.params.id;
	group.data = [];
	group.subgroups = [];
	
	group.removeMemberFromList = removeMemberFromList;
	group.deleteMember = deleteMember;
	group.deleteGroup = deleteGroup;

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
						separateSubGroups();

					}, function(error) {

						$rootScope.pageLoadingProgress = 100;
						$scope.operationResult = Utils.buildErrorOperationResult(error);
						group.loadingModal.dismiss("Dismiss");

					});
		});
	}

	function removeMemberFromList(member) {

		var i = group.data.members.indexOf(member);
		group.data.members.splice(i, 1);
	}
	
	function removeSubgroupFromList(member) {

		var i = group.subgroups.indexOf(member);
		group.subgroups.splice(i, 1);
	}

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
							toaster.pop({
						          type: 'success',
						          body:
						              `Member ${member.display} has been removed successfully`
						        });
						}, function(error) {
							toaster.pop({
						          type: 'error',
						          body:
						              `${error.data.detail}`
						        });
						});
				});
	}
	
	function separateSubGroups(){
		if(group.data.members){
			group.subgroups = group.data.members.filter(memberIsAGroup);
			group.subgroups.forEach(function(member){
				removeMemberFromList(member);
			});
		}else{
			group.subgroups = [];
		}
	}
	
	function memberIsAGroup(member){
		return (member.$ref.indexOf('scim/Groups')!=-1);
	}
	
	function openAddSubgroupDialog() {
		var modalInstance = $uibModal.open({
			templateUrl: '/resources/iam/template/dashboard/group/addsubgroup.html',
			controller: 'AddSubGroupController',
			controllerAs: 'addSubGroupCtrl',
			resolve: {group: function() { return group.data; }}
		});
		modalInstance.result.then(function(createdGroup) {
			console.info(createdGroup);
			group.loadGroup();
			toaster.pop({
		          type: 'success',
		          body:
		              `Group '${createdGroup.displayName}' CREATED successfully`
		        });
		}, function() {
			console.info('Modal dismissed at: ', new Date());
		});
	}
	
	function deleteGroup(member) {
		
		var modalOptions = {
			closeButtonText: 'Cancel',
			actionButtonText: 'Delete Group',
			headerText: "Delete Group «" + member.display + "»",
			bodyText: `Are you sure you want to delete group '${member.display}'?`
		};
		
		ModalService.showModal({}, modalOptions).then(
			function (){
				scimFactory.deleteGroup(member.value)
					.then(function(response) {
						removeSubgroupFromList(member);
						$rootScope.loggedUser.totGroups = $rootScope.loggedUser.totGroups -1;
						toaster.pop({
					          type: 'success',
					          body:
					              `Group '${member.display}' DELETED successfully`
					        });
					}, function(error) {
						toaster.pop({
					          type: 'error',
					          body:
					              `${error.data.detail}`
					        });
					});
			});
	}
}