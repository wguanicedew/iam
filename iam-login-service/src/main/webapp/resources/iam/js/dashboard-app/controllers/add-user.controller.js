'use strict';

angular.module('dashboardApp').controller('AddUserController',
		AddUserController);

AddUserController.$inject = [ '$scope', '$uibModalInstance', 'Utils',
		'scimFactory', '$state' ];

function AddUserController($scope, $uibModalInstance, Utils, scimFactory,
		$state) {
	
	var addUserCtrl = this;
	
	addUserCtrl.user = {
			givenname : '',
			familyname : '',
			username : '',
			email : ''
	};

	addUserCtrl.textAlert;
	addUserCtrl.operationResult;

	addUserCtrl.submit = submit;
	addUserCtrl.reset = reset;
	addUserCtrl.dismiss = dismiss;
	addUserCtrl.enabled = true;

	function submit() {
		
		addUserCtrl.enabled = false;

		var scimUser = {};
		
		scimUser.id = Utils.uuid();
		scimUser.schemas = [];
		scimUser.schemas[0] = "urn:ietf:params:scim:schemas:core:2.0:User";
		scimUser.displayName = addUserCtrl.user.givenname + " " + addUserCtrl.user.surname;
		scimUser.name = {
			givenName: addUserCtrl.user.givenname,
			familyName: addUserCtrl.user.familyname,
			middleName: ""				
		};
		scimUser.emails = [{
			type: "work",
			value: addUserCtrl.user.email,
			primary: true
		}];
		scimUser.userName = addUserCtrl.user.username;
		scimUser.active = true;
		scimUser.picture = "resources/iam/img/default-avatar.png";
		
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
				givenname : '',
				familyname : '',
				username : '',
				email : ''
		};
		$scope.userCreationForm.$setPristine();
	}

	function dismiss() {
		$uibModalInstance.dismiss('Cancel');
	}
}
