'use strict';

angular.module('dashboardApp').controller('AddSamlAccountController', AddSamlAccountController);

AddSamlAccountController.$inject = ['$scope', '$uibModalInstance', 'scimFactory', '$state', 'user'];

function AddSamlAccountController($scope, $uibModalInstance, scimFactory, $state, user) {
	
	var addSamlAccountCtrl = this;
	addSamlAccountCtrl.user = user;
	addSamlAccountCtrl.cancel = cancel;
	
	addSamlAccountCtrl.addSamlAccount = addSamlAccount;
	addSamlAccountCtrl.reset = reset;
	
	function reset() {

		addSamlAccountCtrl.idpId = "";
		addSamlAccountCtrl.userId = "";
	};
	
	function addSamlAccount() {
		
		scimFactory.addSamlId(addSamlAccountCtrl.user.id, addSamlAccountCtrl.idpId, addSamlAccountCtrl.userId).then(function(response) {
			$uibModalInstance.close(response.data);
		},function(error) {
			console.error('Error creating new saml account: ' + error);
			addSamlAccountCtrl.textAlert = error.data.error_description || error.data.detail;
			addSamlAccountCtrl.operationResult = 'err';
		});
	}
	
	function cancel() {
		$uibModalInstance.dismiss("Cancel");
	}
}