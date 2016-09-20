'use strict';

angular.module('dashboardApp').controller('ErrorController', ErrorController);

ErrorController.$inject = ['$scope', '$state', '$window'];

function ErrorController($scope, $state, $window) {

	var errorCtrl = this;
	errorCtrl.name = "ErrorController";
	errorCtrl.error = $state.params.error;

	if (errorCtrl.error.status == '401') {

		console.log("Session expired!");
		$window.location.href = "/dashboard/expiredsession";
	}
}
