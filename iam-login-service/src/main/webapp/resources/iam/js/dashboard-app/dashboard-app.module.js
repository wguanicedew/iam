'use strict';

angular.module('dashboardApp', [ 'ui.router', 'ui.bootstrap',
		'ui.bootstrap.tpls', 'ui.select', 'ngCookies', 'ngSanitize',
		'relativeDate', 'ngResource' ]);

angular.module('dashboardApp').run(
		function($window, $rootScope, $state, $stateParams, $q, $uibModal, Utils,
				scimFactory, RegistrationRequestService) {
	

	// Offline dialog
	$rootScope.closeOfflineDialog = function() {

		console.log("into: closeOfflineDialog");

		if ($rootScope.offlineDialog) {

			console.log("Closing offline dialog");
			$rootScope.offlineDialog.dismiss("Back online");
			$rootScope.offlineDialog = undefined;
		}
	}
	
	$rootScope.openOfflineDialog = function() {
		
		if (!$rootScope.offlineDialog) {
			
			console.log("Opening offline dialog");
			$rootScope.offlineDialog = $uibModal
				.open({
					animation : false,
					backdrop  : 'static',
					keyboard  : false,
					templateUrl: "noConnectionTemplate.html"
				});
		}
	}

	// logged user
	$rootScope.loggedUser = Utils.getLoggedUser();
	$rootScope.isRegistrationEnabled = Utils.isRegistrationEnabled();

	var promises = [];

	var getMePromise = scimFactory.getMe();
	promises.push(getMePromise);

	var getUserCountPromise = scimFactory.getUsers(1,1);
	promises.push(getUserCountPromise);
	
	var getGroupCountPromise = scimFactory.getGroups(1,1);
	promises.push(getGroupCountPromise);

	if ($rootScope.isRegistrationEnabled){
		var getPendingRequestsPromise = RegistrationRequestService.listPending();
		promises.push(getPendingRequestsPromise);
	}

	$q.all(promises).then(function(data){
		getMePromise.then(function(response) {
			console.log(response);
			$rootScope.loggedUser.me = response.data;
		});

		getUserCountPromise.then(function(response){
			console.log(response.data);
			$rootScope.loggedUser.totUsers = response.data.totalResults;
		});
		
		getGroupCountPromise.then(function(response) {
			console.log(response.data);
			$rootScope.loggedUser.totGroups = response.data.totalResults;
		});

		if ($rootScope.isRegistrationEnabled){
			getPendingRequestsPromise.then(function(response) {
				console.log(response.data);
				$rootScope.loggedUser.pendingRequests = response.data;
			});
		}

	}, function(err){
		console.error(error);		
	});

	// ctrl+R refresh
	$rootScope.reload = function() {
		$window.location.reload();
	}

	// refresh last state loaded
	$rootScope.refresh = function() {

		$rootScope.closeOfflineDialog();
		$state.transitionTo($state.current, $stateParams, {
			reload : true,
			inherit : false,
			notify : true
		});
	}
});