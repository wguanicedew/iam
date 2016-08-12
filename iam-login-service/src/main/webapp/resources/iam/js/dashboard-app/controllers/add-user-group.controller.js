'use strict';

angular.module('dashboardApp').controller('AddUserGroupController', AddUserGroupController);

AddUserGroupController.$inject = ['$scope', '$state', '$uibModalInstance', '$filter', 'Utils', 'scimFactory', 'user'];

function AddUserGroupController($scope, $state, $uibModalInstance, $filter, Utils, scimFactory, user) {

	var ctrl = this;
	
	ctrl.groupSelected = null;
	
	ctrl.user = user;
	console.log(ctrl.userGroups);
	ctrl.groups = [];
	ctrl.oGroups = [];
	
	ctrl.getAllGroups = getAllGroups;
	ctrl.evalAddGroupList = evalAddGroupList;
	ctrl.cancel = cancel;
	
	getAllGroups(1,10);
	
	function getAllGroups(startIndex, count) {
		
		scimFactory.getGroups(startIndex, count)
			.then(function(response) {
				
				angular.forEach(response.data.Resources, function(group) {
					ctrl.groups.push(group);
				});
				ctrl.groups = $filter('orderBy')(ctrl.groups, "displayName", false);
				
				if (response.data.totalResults > (response.data.startIndex + response.data.itemsPerPage)) {
					ctrl.getAllGroups(startIndex + count, count);
				} else {
					evalAddGroupList();
				}
			},function(error) {
				$state.go("error", { "errCode": error.status, "errMessage": error.statusText });
			});
	}

	function evalAddGroupList() {
		
		ctrl.oGroups = ctrl.groups;

		angular.forEach(ctrl.user.groups, function(group) {
		
			var found = $filter('getById')(ctrl.oGroups, group.value);
			if (found == null) {
				$state.go("error", { "errCode": 500, "errMessage": "User's group " + group.displayName + " not found into db!" });
				return;
			}
			console.log("Deleting element at ", found[0], ctrl.oGroups[found[0]]);
			// remove using position
			ctrl.oGroups.splice(found[0], 1);
		});
		
		console.log(ctrl.oGroups);
	}
	
	ctrl.lookupGroups = lookupGroups;
	
	function lookupGroups() {

        return ctrl.oGroups;
      }
	
	function cancel() {
        $uibModalInstance.close();
    };
    
    ctrl.addGroup = addGroup;
    
    function addGroup() {
        
    	scimFactory.patchAddUserToGroup(ctrl.groupSelected, user).then(function(response) {
    		ctrl.cancel();
    		console.log(response);
		},function(error) {
			ctrl.cancel();
			$state.go("error", { "errCode": error.status, "errMessage": error.statusText });
		});
    };
	
}