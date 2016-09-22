'use strict';

angular.module('dashboardApp').controller('HomeController', HomeController);

HomeController.$inject = [ '$state', 'Utils', 'scimFactory', 'ModalService' ];

function HomeController($state, Utils, scimFactory, ModalService) {

	if (Utils.isAdmin()) {
		console.log("User is admin: redirecting to his page ");
		$state.go("user", { 
			id: getUserInfo().sub 
		});
		return;
	}

	var home = this;
	
	home.user = Utils.getLoggedUser();
	home.isAdmin = Utils.isAdmin();
	home.isUser = Utils.isUser();
	
	scimFactory.getMe().then(function(response) {
		home.user.me = response.data;
	}, function(error) {
		home.textAlert = error.data.error_description || error.data.detail;
		home.operationResult = 'err';
	});

	home.showSshKeyValue = showSshKeyValue;
	home.showCertValue = showCertValue;

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

}