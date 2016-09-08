'use strict';

angular.module('dashboardApp').controller('CommonController', CommonController);

CommonController.$inject = ['$scope', '$rootScope', '$state', 'Utils', 'scimFactory', 'RegistrationRequestService'];

function CommonController($scope, $rootScope, $state, Utils, scimFactory, RegistrationRequestService) {

	var commonCtrl = this;
	commonCtrl.name = "CommonController";

	$rootScope.loggedUser = { 
		info: getUserInfo(), 
		auth: getUserAuthorities(),
		isAdmin: Utils.isAdmin()
	}
	
	scimFactory.getMe().then(function(response) {
		console.log(response.data);
		$rootScope.loggedUser.me = response.data;
	}, function(error) {
		$state.go("error", {
			"error": error
		});
	});
	
	RegistrationRequestService.listPending().then(function(response) {
		console.log(response.data);
		$rootScope.loggedUser.pendingRequests = response.data;
	}, function(error) {
		$state.go("error", {
			"error": error
		});
	});
	
	scimFactory.getUsers(1, 1).then(function(response) {
		console.log(response.data);
		$rootScope.loggedUser.totUsers = response.data.totalResults;
	}, function(error) {
		$state.go("error", {
			"error": error
		});
	});

	scimFactory.getGroups(1, 1).then(function(response) {
		console.log(response.data);
		$rootScope.loggedUser.totGroups = response.data.totalResults;
	}, function(error) {
		$state.go("error", {
			"error": error
		});
	});
}
