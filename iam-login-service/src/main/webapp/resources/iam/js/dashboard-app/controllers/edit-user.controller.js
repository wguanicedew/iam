'use strict';

angular.module('dashboardApp').controller('EditUserController',
		EditUserController);

EditUserController.$inject = [ '$scope', '$state', '$uibModalInstance',
		'Utils', 'scimFactory', 'user' ];

function EditUserController($scope, $state, $uibModalInstance, Utils,
		scimFactory, user) {

	var editUserCtrl = this;

	console.log("received user to edit: ", user);

	editUserCtrl.userToEdit = user;
	
	editUserCtrl.user = {
		givenName : user.name.givenName,
		familyName : user.name.familyName,
		userName : user.userName,
		email : user.emails[0].value,
		picture : user.picture,
		id: user.id
	}

	console.log("builded user: ", editUserCtrl.user);

	editUserCtrl.submit = submit;
	editUserCtrl.reset = reset;
	editUserCtrl.dismiss = dismiss;

	editUserCtrl.enabled = true;

	function submit() {

		editUserCtrl.enabled = false;

		var scimUser = {};

		scimUser.id = user.id;
		scimUser.schemas = [];
		scimUser.schemas[0] = "urn:ietf:params:scim:schemas:core:2.0:User";
		scimUser.displayName = editUserCtrl.user.givenName + " "
				+ editUserCtrl.user.familyName;
		scimUser.name = {
			givenName : editUserCtrl.user.givenName,
			familyName : editUserCtrl.user.familyName,
			middleName : ""
		};
		scimUser.emails = [ {
			type : "work",
			value : editUserCtrl.user.email,
			primary : true
		} ];
		scimUser.userName = editUserCtrl.user.userName;
		scimUser.active = true;
		scimUser.picture = editUserCtrl.user.picture;

		console.info("Adding user ... ", scimUser);

		scimFactory.updateUser(scimUser).then(
			function(response) {
				$uibModalInstance.close(response);
				editUserCtrl.enabled = true;
			},
			function(error) {
				$scope.operationResult = Utils.buildErrorOperationResult(error);
				editUserCtrl.enabled = true;
			});
	}

	function reset() {
		editUserCtrl.user = {
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