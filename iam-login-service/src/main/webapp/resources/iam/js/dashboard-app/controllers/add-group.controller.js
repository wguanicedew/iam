'use strict';

angular.module('dashboardApp').controller('AddGroupController', AddGroupController);

AddGroupController.$inject = ['$scope', '$uibModalInstance', 'Utils', 'scimFactory', '$location'];

function AddGroupController($scope, $uibModalInstance, Utils, scimFactory, $location) {

	$scope.group = {};
	$scope.addGroup = addGroup;
	$scope.resetGroup = resetGroup;
	
	function resetGroup() {

		$scope.group.id = "";
		$scope.group.displayName = "";
		$scope.group.description = "";

	};

	$scope.resetGroup();

	function addGroup() {
		
		$scope.group.id = Utils.uuid();
		$scope.group.schemas = [];
		$scope.group.schemas[0] = "urn:ietf:params:scim:schemas:core:2.0:Group";

		console.info($scope.group);
		
		scimFactory
			.createGroup($scope.group)
				.then(
					function(response) {
						$uibModalInstance.close(response.data);
					},
					function(error) {
						console.error('Error creating group: ' + error);
						$location.path("/dashboard#/error?e=" + error);
					}
				);
	};
	
	function cancel() {
		$uibModalInstance.dismiss("cancel");
	}
}