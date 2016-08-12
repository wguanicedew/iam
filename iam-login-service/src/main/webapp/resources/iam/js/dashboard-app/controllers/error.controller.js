'use strict';

angular.module('dashboardApp').controller('ErrorController', ErrorController);

ErrorController.$inject = ['$scope', '$state'];

function ErrorController($scope, $state) {

	var ec = this;
	ec.name = "ErrorController";
	
	console.log($state.params);
	
	ec.errCode = $state.params.errCode ? $state.params.errCode : "500";
	ec.errMessage = $state.params.errMessage ? $state.params.errMessage : "Unknown error";
	
}
