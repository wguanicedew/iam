'use strict';

angular.module('dashboardApp').controller('AddOIDCAccountController',
		AddOIDCAccountController);

AddOIDCAccountController.$inject = [ '$scope', '$uibModalInstance',
		'scimFactory', '$state', 'Utils', 'user' ];

function AddOIDCAccountController($scope, $uibModalInstance, scimFactory,
		$state, Utils, user) {

	var addOidcCtrl = this;
	addOidcCtrl.user = user;
	addOidcCtrl.cancel = cancel;

	addOidcCtrl.addOidcAccount = addOidcAccount;
	addOidcCtrl.reset = reset;

	function reset() {

		addOidcCtrl.issuer = "";
		addOidcCtrl.subject = "";
	}
	;

	function addOidcAccount() {

		addOidcCtrl.enabled = false;
		scimFactory.addOpenIDAccount(addOidcCtrl.user.id, addOidcCtrl.issuer,
				addOidcCtrl.subject).then(function(response) {
			$uibModalInstance.close(response.data);
			addOidcCtrl.enabled = true;
		}, function(error) {
			console.error('Error creating group: ' + error);
			$scope.operationResult = Utils.buildErrorOperationResult(error);
			addOidcCtrl.enabled = true;
		});
	}

	function cancel() {
		$uibModalInstance.dismiss("cancel");
	}
}