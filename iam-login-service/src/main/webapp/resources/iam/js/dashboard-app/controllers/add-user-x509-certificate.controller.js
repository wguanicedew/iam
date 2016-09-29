'use strict';

angular.module('dashboardApp').controller('AddX509CertificateController',
		AddX509CertificateController);

AddX509CertificateController.$inject = [ '$scope', '$uibModalInstance',
		'scimFactory', 'Utils', '$state', 'user' ];

function AddX509CertificateController($scope, $uibModalInstance, scimFactory, Utils,
		$state, user) {

	var addX509CertCtrl = this;
	addX509CertCtrl.user = user;
	addX509CertCtrl.cancel = cancel;

	addX509CertCtrl.addCertificate = addCertificate;
	addX509CertCtrl.reset = reset;

	addX509CertCtrl.reset();

	function reset() {

		addX509CertCtrl.label = "";
		addX509CertCtrl.value = "";
		addX509CertCtrl.enabled = true;
	}

	function checkBase64Encoding() {

		var base64Matcher = new RegExp(
				"^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})$");
		return (base64Matcher.test(addX509CertCtrl.value));
	}

	function addCertificate() {

		addX509CertCtrl.enabled = false;
		
		console.log("Adding Certificate ", addX509CertCtrl.label,
				addX509CertCtrl.value);

		if (!checkBase64Encoding()) {
			$scope.operationResult = Utils.buildErrorOperationResult({
				data: {
					detail: "Key is not a base64 encoded string!"
				}
			});
			return;
		}

		scimFactory.addX509Certificate(addX509CertCtrl.user.id,
				addX509CertCtrl.label, false, addX509CertCtrl.value).then(
				function(response) {
					console.log("Added x509 Certificate: ",
							addX509CertCtrl.label, addX509CertCtrl.value);
					$uibModalInstance.close(response.data);
					addX509CertCtrl.enabled = true;
				}, function(error) {
					console.error('Error creating x509 certificate: ', error);
					$scope.operationResult = Utils.buildErrorOperationResult(error);
					addX509CertCtrl.enabled = true;
				});
	}

	function cancel() {
		$uibModalInstance.dismiss("cancel");
	}
}