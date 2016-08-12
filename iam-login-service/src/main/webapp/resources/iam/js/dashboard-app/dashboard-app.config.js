angular.module('dashboardApp').config(function($stateProvider, $urlRouterProvider) {

	$urlRouterProvider.otherwise('/home');
	$stateProvider.state('home', {
		url : '/home',
		views: {
		      'content' : {
		        templateUrl: '/resources/iam/template/dashboard/home.html',
		        controller: 'HomeController',
		        controllerAs: 'home'
		      }
		    }
	}).state('users', {
		url : '/users',
		views: {
		      'content' : {
		        templateUrl: '/resources/iam/template/dashboard/users.html',
		        controller: 'UsersController',
		        controllerAs: 'uc'
		      }
		    }
	}).state('groups', {
		url : '/groups',
		views: {
		      'content' : {
		        templateUrl: '/resources/iam/template/dashboard/groups.html',
		        controller: 'GroupsController',
		        controllerAs: 'gc'
		      }
		    }
	}).state('error', {
		url : '/error',
		params: {
			errCode: null,
			errMessage: null
		},
		views: {
		      'content' : {
		        templateUrl: '/resources/iam/template/dashboard/error.html',
		        controller: 'ErrorController',
		        controllerAs: 'ec'
		      }
		    }
	});
});