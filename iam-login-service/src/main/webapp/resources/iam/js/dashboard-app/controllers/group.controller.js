'use strict';

angular.module('dashboardApp').controller('GroupController', GroupController);

GroupController.$inject = [ '$state', 'scimFactory', 'ModalService' ];

function GroupController($state, scimFactory, ModalService) {

	var group = this;

	group.id = $state.params.id;
	group.data = [];

	scimFactory.getGroup(group.id).then(
			function(response) {

				console.log(response.data);
				group.data = response.data;
				group.data.members = $filter('orderBy')(group.data.members,
						"display", false);

			}, function(error) {
				$state.go("error", {
					"error" : error
				});
			});

	group.removeMemberFromList = removeMemberFromList;

	function removeMemberFromList(user) {

		var i = group.data.members.indexOf(user);
		group.data.members.splice(i, 1);
	}

	group.deleteMember = deleteMember;

	function deleteMember(user) {

		var modalOptions = {
				closeButtonText: 'Cancel',
				actionButtonText: 'Remove user from group',
				headerText: 'Remove membership?',
				bodyText: `Are you sure you want to remove user memebership to '${group.data.displayName}'?`	
			};
				
			ModalService.showModal({}, modalOptions).then(
				function (){
					scimFactory.removeUserFromGroup(group.id, user.value, user.$ref,
							user.display)
						.then(function(response) {
							console.log("Deleted: ", user.display);
							group.removeMemberFromList(user);
							group.textAlert = `User ${user.display} membership removed successfully`;
							group.operationResult = 'ok';
						}, function(error) {
							group.textAlert = error.data.error_description || error.data.detail;
							group.operationResult = 'err';
						});
				});
	}
}