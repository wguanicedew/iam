'use strict';

angular.module('ngRouteDashboard', [ 'ui.router', 'ui.bootstrap', 'ui.bootstrap.tpls' ])

.controller('HomeController',
		function($scope, $http) {
			$scope.name = "HomeController";
			
			$http.get('/scim/Me').success(function(user) {
				$scope.userInfo = user;
			});
			$scope.showSshKeyValue = function(value) {
				alert(value);
			};
		})

.controller('GroupsController', function($scope, $http) {
	$scope.name = "GroupsController";
	
	$http.get('/scim/Groups').success(function(groups){
		$scope.groups = groups;
	});
})

.controller('UsersController', function($scope, $http) {
	$scope.name = "UsersController";
	
	$http.get('/scim/Users').success(function(users){
		$scope.users = users;
	});
})

.config(function($stateProvider, $urlRouterProvider){
	
	$urlRouterProvider.otherwise('/home');
	$stateProvider
	.state('home',{
		url: '/home',
		templateUrl:'/resources/iam/template/home.html',
		controller: 'HomeController'
	})
	.state('users',{
		url: '/users',
		templateUrl:'/resources/iam/template/users.html',
		controller: 'UsersController'
	})
	.state('groups',{
		url: '/groups',
		templateUrl:'/resources/iam/template/groups.html',
		controller: 'GroupsController'
	});
})

.directive('sidebar', function() {
	return {
		restrict : 'C',
		compile : function(tElement, tAttrs, transclude) {
			// Enable sidebar tree view controls
			$.AdminLTE.tree(tElement);
		}
	};
})

.directive('header', function() {
	return {
		restrict : 'E',
		templateUrl : '/resources/iam/template/header.html',
		compile : function(tElement, tAttrs, transclude) {
			$.AdminLTE.pushMenu($(tElement).find('.sidebar-toggle'));
		}
	};
})

.directive('mainsidebar', function() {
	return {
		restrict : 'E',
		templateUrl : '/resources/iam/template/nav.html',
		compile : function(tElement, tAttrs, transclude) {
		}
	};
})

.directive(
		'box',
		function() {
			return {
				restrict : 'C',
				compile : function(tElement, tAttr, transclude) {
					var _this = this;
					$(tElement).find(
							this.boxWidgetOptions.boxWidgetSelectors.collapse)
							.click(function(e) {
								e.preventDefault();
								_this.collapse($(this));
							});
					$(tElement).find(
							this.boxWidgetOptions.boxWidgetSelectors.remove)
							.click(function(e) {
								e.preventDefault();
								_this.remove($(this));
							});
				},
				collapse : function(element) {
					// Find the box parent
					var box = element.parents(".box").first();
					// Find the body and the footer
					var bf = box.find(".box-body, .box-footer");
					if (!box.hasClass("collapsed-box")) {
						// Convert minus into plus
						element.children(".fa-minus").removeClass("fa-minus")
								.addClass("fa-plus");
						bf.slideUp(300, function() {
							box.addClass("collapsed-box");
						});
					} else {
						// Convert plus into minus
						element.children(".fa-plus").removeClass("fa-plus")
								.addClass("fa-minus");
						bf.slideDown(300, function() {
							box.removeClass("collapsed-box");
						});
					}
				},
				remove : function(element) {
					// Find the box parent
					var box = element.parents(".box").first();
					box.slideUp();
				},
				boxWidgetOptions : {
					boxWidgetIcons : {
						// The icon that triggers the collapse event
						collapse : 'fa fa-minus',
						// The icon that trigger the opening event
						open : 'fa fa-plus',
						// The icon that triggers the removing event
						remove : 'fa fa-times'
					},
					boxWidgetSelectors : {
						//Remove button selector
						remove : '[data-widget="remove"]',
						//Collapse button selector
						collapse : '[data-widget="collapse"]'
					}
				}

			}
		});