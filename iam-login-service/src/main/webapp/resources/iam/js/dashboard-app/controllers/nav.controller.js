'use strict';

angular.module('dashboardApp').controller('NavController', NavController);

NavController.$inject = [ '$scope', '$rootScope', 'Utils', 'scimFactory' ];

function NavController($scope, $rootScope, Utils, scimFactory) {

	var navCtrl = this;

	navCtrl.user = Utils.getLoggedUser();
	
	scimFactory.getMe().then(function(response) {
		navCtrl.user.me = response.data;
	}, function(error) {
		$state.go("error", {
			"error": error
		});
	});

	
	navCtrl.isAdmin = Utils.isAdmin();

	if (navCtrl.isAdmin) {
		
		navCtrl.data = {};
		navCtrl.data.totUsers = 0;
		navCtrl.data.totGroups = 0;
		
		scimFactory.getUsers(1, 1).then(function(response) {
			console.log(response.data);
			navCtrl.data.totUsers = response.data.totalResults;
		}, function(error) {
			console.error(error);
			navCtrl.data.totUsers = "?";
		});
		
		scimFactory.getGroups(1, 1).then(function(response) {
			console.log(response.data);
			navCtrl.data.totGroups = response.data.totalResults;
		}, function(error) {
			console.error(error);
			navCtrl.data.totGroups = "?";
		});
		
		navCtrl.data.totRequests = "?";
	}

}