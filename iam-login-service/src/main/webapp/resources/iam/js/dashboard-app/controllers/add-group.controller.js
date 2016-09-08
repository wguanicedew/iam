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

	function resetGroup() {

		addGroupCtrl.group.id = "";
		addGroupCtrl.group.displayName = "";
		addGroupCtrl.group.description = "";

	}

	addGroupCtrl.resetGroup();

	function addGroup() {

		addGroupCtrl.group.id = Utils.uuid();
		addGroupCtrl.group.schemas = [];
		addGroupCtrl.group.schemas[0] = "urn:ietf:params:scim:schemas:core:2.0:Group";

		console.info(addGroupCtrl.group);

		scimFactory.createGroup(addGroupCtrl.group).then(function(response) {
			$rootScope.loggedUser.totGroups = $rootScope.loggedUser.totGroups + 1;
			$uibModalInstance.close(response.data);
		}, function(error) {
			console.error('Error creating group', error);
			addGroupCtrl.textAlert = error.data.error_description || error.data.detail;
			addGroupCtrl.operationResult = 'err';
		});
	}

	function cancel() {
		$uibModalInstance.dismiss("cancel");
	}
}