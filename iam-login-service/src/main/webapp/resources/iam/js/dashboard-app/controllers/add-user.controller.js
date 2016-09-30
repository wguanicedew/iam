'use strict';

angular.module('dashboardApp').controller('AddUserController',
		AddUserController);

AddUserController.$inject = [ '$scope', '$uibModalInstance', 'Utils',
		'scimFactory', '$state' ];

function AddUserController($scope, $uibModalInstance, Utils, scimFactory,
		$state) {
	
	var addUserCtrl = this;

	addUserCtrl.submit = submit;
	addUserCtrl.reset = reset;
	addUserCtrl.dismiss = dismiss;

	function submit() {
		
		console.log("User info to add ", addUserCtrl.user);
		addUserCtrl.enabled = false;

		var scimUser = {};
		
		scimUser.id = Utils.uuid();
		scimUser.schemas = [];
		scimUser.schemas[0] = "urn:ietf:params:scim:schemas:core:2.0:User";
		scimUser.displayName = addUserCtrl.user.name + " " + addUserCtrl.user.surname;
		scimUser.name = {
			givenName: addUserCtrl.user.name,
			familyName: addUserCtrl.user.surname,
			middleName: ""
		};
		scimUser.emails = [{
			type: "work",
			value: addUserCtrl.user.email,
			primary: true
		}];
		scimUser.userName = addUserCtrl.user.username;
		scimUser.active = true;
		scimUser.picture = "";
		
		console.info("Adding user ... ", scimUser);

		scimFactory.createUser(scimUser).then(
			function(response) {
				console.info("Returned created user", response.data);
				$uibModalInstance.close(response.data);
				addUserCtrl.enabled = true;
			},
			function(error) {
				$scope.operationResult = Utils.buildErrorOperationResult(error);
				addUserCtrl.enabled = true;
			});
	}

	function reset() {
		addUserCtrl.user = {
				name : '',
				surname : '',
				username : '',
				email : ''
		};
		if ($scope.userCreationForm) {
			$scope.userCreationForm.setPristine();
		}
		addUserCtrl.enabled = true;
	}

	function dismiss() {
		$uibModalInstance.dismiss('Cancel');
	}
}
