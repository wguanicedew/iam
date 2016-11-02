'use strict';

angular.module('dashboardApp').controller('EditUserController',
		EditUserController);

EditUserController.$inject = [ '$scope', '$rootScope', '$state', '$uibModalInstance',
		'Utils', 'scimFactory', 'user' ];

function EditUserController($scope, $rootScope, $state, $uibModalInstance, Utils,
		scimFactory, user) {

	var editUserCtrl = this;

	editUserCtrl.oUser = user;
	editUserCtrl.id = user.id;

	editUserCtrl.submit = submit;
	editUserCtrl.reset = reset;
	editUserCtrl.dismiss = dismiss;
	editUserCtrl.isSubmitDisabled = isSubmitDisabled;

	editUserCtrl.reset();

	function submit() {

		editUserCtrl.enabled = false;

		var scimUser = {};

		if ($scope.userUpdateForm.name.$dirty || $scope.userUpdateForm.surname.$dirty) {
			scimUser.displayName = editUserCtrl.eUser.name + " "
					+ editUserCtrl.eUser.surname;
			scimUser.name = {
					givenName : editUserCtrl.eUser.name,
					familyName : editUserCtrl.eUser.surname,
					middleName : ""
			};
		}
		if ($scope.userUpdateForm.email.$dirty) {
			scimUser.emails = [ {
				type : "work",
				value : editUserCtrl.eUser.email,
				primary : true
			} ];
		}
		if ($scope.userUpdateForm.username.$dirty) {
			scimUser.userName = editUserCtrl.eUser.username;
		}
		if ($scope.userUpdateForm.picture.$dirty) {
			if (!editUserCtrl.eUser.picture) {
				scimUser.photos = [];
			} else {
				scimUser.photos = [{
					type : "photo",
					value : editUserCtrl.eUser.picture
				}];
			}
		}

		console.info("Edited user ... ", scimUser);

		scimFactory.updateUser(user.id, scimUser).then(
			function(response) {

				if (Utils.isMe(user.id)) {
					$rootScope.reloadUser();
				}

				$uibModalInstance.close(response);
				editUserCtrl.enabled = true;
			},
			function(error) {
				$scope.operationResult = Utils.buildErrorOperationResult(error);
				editUserCtrl.enabled = true;
			});
	}

	function reset() {
		editUserCtrl.eUser = {
				name : editUserCtrl.oUser.name.givenName,
				surname : editUserCtrl.oUser.name.familyName,
				picture : editUserCtrl.oUser.photos ? editUserCtrl.oUser.photos[0].value : "",
				email : editUserCtrl.oUser.emails[0].value,
				username : editUserCtrl.oUser.userName
			};
		if ($scope.userUpdateForm) {
			$scope.userUpdateForm.$setPristine();
		}
		editUserCtrl.enabled = true;
	}

	function dismiss() {
		$uibModalInstance.dismiss('Cancel');
	}

	function isSubmitDisabled() {
		return !editUserCtrl.enabled 
				|| !$scope.userUpdateForm.$dirty 
				|| $scope.userUpdateForm.name.$invalid
				|| $scope.userUpdateForm.surname.$invalid
				|| $scope.userUpdateForm.email.$invalid
				|| $scope.userUpdateForm.username.$invalid
				|| $scope.userUpdateForm.picture.$invalid;
	}
}