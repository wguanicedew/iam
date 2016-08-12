'use strict';

angular.module('dashboardApp').controller('HomeController', HomeController);

HomeController.$inject = ['$http', '$location', '$uibModal', '$filter', 'scimFactory'];

function HomeController($http, $location, $uibModal, $filter, scimFactory) {

	var ctrl = this;
	
	ctrl.userInfo = {};

	ctrl.getUserInfo = getUserInfo;
	ctrl.clickToOpen = clickToOpen;
	
	getUserInfo();
	
	function getUserInfo() {
		scimFactory.getMe()
			.then(function(response) {
					console.log(response.data);
					ctrl.userInfo = response.data;
				},function(error) {
					$state.go("error", { "errCode": error.status, "errMessage": error.statusText });
				});
	};

	function clickToOpen() {
		var modalInstance = $uibModal.open({
			templateUrl: '/resources/iam/template/dashboard/addusergroup.html',
			controller: 'AddUserGroupController',
			controllerAs: 'ctrl',
			resolve: {
				user: function () {
	                return ctrl.userInfo;
	            }
	        }
		});
		modalInstance.result.then(function(addedGroup) {
			console.info(addedGroup);
			getUserInfo();
		}, function () {
			console.info('Modal dismissed at: ' + new Date());
		});
	};
	
	function showSshKeyValue(value) {
		alert(value);
	};
}