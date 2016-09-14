'use strict';

angular.module('dashboardApp').controller('HomeController', HomeController);

HomeController.$inject = [ '$state', 'Utils', 'scimFactory' ];

function HomeController($state, Utils, scimFactory) {

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
		$state.go("error", {
			"error": error
		});
	});

	home.showSshKeyValue = showSshKeyValue;
	home.showCertValue = showCertValue;

	function showSshKeyValue(value) {
		alert(value);
	}

	function showCertValue(cert) {
		alert(cert.value);
	}

}