'use strict';

angular.module('dashboardApp').controller('AddUserController',
		AddUserController);

AddUserController.$inject = [ '$scope', '$uibModalInstance', 'Utils',
		'scimFactory', '$state' ];

function AddUserController($scope, $uibModalInstance, Utils, scimFactory,
		$state) {
	
	var addUserCtrl = this;
	
	addUserCtrl.valid = valid;
	addUserCtrl.cancel = cancel;
	
	addUserCtrl.user = {};
	
	function cancel() {
		$uibModalInstance.dismiss("cancel");
	}
	
	function valid() {
		return addUserCtrl.user.name && addUserCtrl.user.email;
	}

	function addUser() {
		
		var scimUser = {};
		
		scimUser.id = Utils.uuid();
		scimUser.schemas = [];
		scimUser.schemas[0] = "urn:ietf:params:scim:schemas:core:2.0:User";
		scimUser.displayName = addUserCtrl.user.name;
		scimUser.emails = [{
			type: "work",
			value: addUserCtrl.email,
			primary: true
		}];
		
		console.info(addUserCtrl.user);

		scimFactory.createUser(addUserCtrl.user).then(function(response) {
			$uibModalInstance.close(response.data);
		}, function(error) {
			console.error('Error creating user ', error);
			addUserCtrl.error = error;
		});
	}
}