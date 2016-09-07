'use strict';

angular.module('dashboardApp').controller('TopbarController', TopbarController);

TopbarController.$inject = ['$scope', '$state', 'Utils', 'scimFactory'];

function TopbarController($scope, $state, Utils, scimFactory) {

	var topbarCtrl = this;
	topbarCtrl.name = "TopbarController";
	
	topbarCtrl.user = Utils.getLoggedUser();
	
	scimFactory.getMe().then(function(response) {
		topbarCtrl.user.me = response.data;
	}, function(error) {
		$state.go("error", {
			"error": error
		});
	});
}
