'use strict';

angular.module('dashboardApp').controller('AddSubGroupController', AddSubGroupController);

AddSubGroupController.$inject = [ '$scope', '$rootScope', '$uibModalInstance', 'Utils', 'scimFactory', 'group' ];

function AddSubGroupController($scope, $rootScope, $uibModalInstance, Utils, scimFactory, group) {
	
	var addSubGroupCtrl = this;

	addSubGroupCtrl.parent = group;
	addSubGroupCtrl.addSubGroup = addSubGroup;
	addSubGroupCtrl.resetGroup = resetGroup;
	addSubGroupCtrl.cancel = cancel;
	
	addSubGroupCtrl.subgroup = {};

	function resetGroup() {

		addSubGroupCtrl.subgroup.displayName = "";
		addSubGroupCtrl.enabled = true;

	}

	addSubGroupCtrl.resetGroup();

	function addSubGroup() {

		addSubGroupCtrl.enabled = false;

		addSubGroupCtrl.subgroup.schemas = ["urn:ietf:params:scim:schemas:core:2.0:Group", "urn:indigo-dc:scim:schemas:IndigoGroup"];
		addSubGroupCtrl.subgroup["urn:indigo-dc:scim:schemas:IndigoGroup"] = {
				"parentGroup": {
					display: addSubGroupCtrl.parent.displayName,
					value: addSubGroupCtrl.parent.id,
					$ref: addSubGroupCtrl.parent.meta.location
				}
		}
		
		console.info(addSubGroupCtrl.subgroup);

		scimFactory.createGroup(addSubGroupCtrl.subgroup).then(function(response) {
			$rootScope.loggedUser.totGroups = $rootScope.loggedUser.totGroups + 1;
			$uibModalInstance.close(response.data);
			addSubGroupCtrl.enabled = true;
		}, function(error) {
			console.error('Error creating group', error);
			$scope.operationResult = Utils.buildErrorOperationResult(error);
			addSubGroupCtrl.enabled = true;
		});
	}

	function cancel() {
		$uibModalInstance.dismiss("cancel");
	}
}