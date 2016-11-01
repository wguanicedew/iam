'use strict';

angular.module('dashboardApp').controller('HomeController', HomeController);

HomeController.$inject = [ '$scope', '$state', 'Utils', 'scimFactory', 'ModalService', '$uibModal' ];

function HomeController($scope, $state, Utils, scimFactory, ModalService, $uibModal) {

	var home = this;

	home.loaded = false;

	scimFactory.getMe().then(function(response) {
		$scope.user = response.data;
		home.loaded = true;
	}, function(error) {
		$scope.operationResult = Utils.buildErrorOperationResult(error);
	});

	home.showSshKeyValue = showSshKeyValue;
	home.showCertValue = showCertValue;
	home.openEditPasswordDialog = openEditPasswordDialog;

	function showSshKeyValue(value) {
		ModalService.showModal({}, {
			closeButtonText: null,
			actionButtonText: 'OK',
			headerText: 'SSH Key value',
			bodyText: `${value}`
		});
	}

	function showCertValue(cert) {
		ModalService.showModal({}, {
			closeButtonText: null,
			actionButtonText: 'OK',
			headerText: 'x509 Certificate value',
			bodyText: `${cert.value}`
		});
	}

	function openEditPasswordDialog() {

		var modalInstance = $uibModal
				.open({
					templateUrl : '/resources/iam/template/dashboard/home/editpassword.html',
					controller : 'EditPasswordController',
					controllerAs : 'editPasswordCtrl',
					resolve : {
						user : function() {
							return $scope.user;
						}
					}
				});
		modalInstance.result.then(function() {
			$scope.operationResult = Utils.buildSuccessOperationResult("User's info updated successfully");
		}, function() {
			console.info('Modal dismissed at: ', new Date());
		});
	}
}