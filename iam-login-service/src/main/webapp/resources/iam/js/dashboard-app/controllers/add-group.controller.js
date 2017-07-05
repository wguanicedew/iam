'use strict';

angular.module('dashboardApp').controller('AddGroupController',
		AddGroupController);

AddGroupController.$inject = [ '$scope', '$rootScope', '$uibModalInstance', 'Utils',
		'scimFactory' ];

function AddGroupController($scope, $rootScope, $uibModalInstance, Utils, scimFactory) {
	
	var addGroupCtrl = this;

	addGroupCtrl.group = {};
	addGroupCtrl.addGroup = addGroup;
	addGroupCtrl.resetGroup = resetGroup;
	addGroupCtrl.cancel = cancel;
	addGroupCtrl.parents = $rootScope.groups;
	addGroupCtrl.parentSelected = addGroupCtrl.parents[-1];

	function resetGroup() {

		addGroupCtrl.group.displayName = "";
		addGroupCtrl.enabled = true;

	}

	addGroupCtrl.resetGroup();

	function addGroup() {

		addGroupCtrl.enabled = false;

		addGroupCtrl.group.schemas = [];
		addGroupCtrl.group.schemas[0] = "urn:ietf:params:scim:schemas:core:2.0:Group";
		addGroupCtrl.group.schemas[1] = "urn:indigo-dc:scim:schemas:IndigoGroup";

		var parent = addGroupCtrl.parentSelected
		if(parent){
			addGroupCtrl.group["urn:indigo-dc:scim:schemas:IndigoGroup"] = {
					"parentGroup": {
						display: parent.displayName,
						value: parent.id,
						$ref: parent.meta.location
					}
			}
		}
		
		console.info(addGroupCtrl.group);

		scimFactory.createGroup(addGroupCtrl.group).then(function(response) {
			$rootScope.loggedUser.totGroups = $rootScope.loggedUser.totGroups + 1;
			$uibModalInstance.close(response.data);
			addGroupCtrl.enabled = true;
		}, function(error) {
			console.error('Error creating group', error);
			$scope.operationResult = Utils.buildErrorOperationResult(error);
			addGroupCtrl.enabled = true;
		});
	}

	function cancel() {
		$uibModalInstance.dismiss("cancel");
	}
}