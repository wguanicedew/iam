angular.module('dashboardApp').factory("scimFactory", [ '$http', function($http) {

	var urlBase = '/scim';
	var scimFactory = {};

	scimFactory.getUsers = function(startIndex, count) {
		return $http.get(urlBase + '/Users?startIndex=' + startIndex + '&count=' + count);
	};

	scimFactory.getGroups = function(startIndex, count) {
		return $http.get(urlBase + '/Groups?startIndex=' + startIndex + '&count=' + count);
	};

	scimFactory.getUser = function(id) {
		return $http.get(urlBase + '/Users/' + id);
	};

	scimFactory.getGroup = function(id) {
		return $http.get(urlBase + '/Groups/' + id);
	};

	scimFactory.getMe = function() {
		return $http.get(urlBase + '/Me');
	};

	scimFactory.createGroup = function(group) {
		var config = {
			headers : {
				'Accept' : 'application/scim+json',
				'Content-Type' : 'application/scim+json'
			}
		}
		return $http.post(urlBase + '/Groups', group, config);
	};
	
	scimFactory.deleteGroup = function(id) {
		return $http.delete(urlBase + '/Groups/' + id);
	};
	
	scimFactory.patchAddUserToGroup = function(group, user) {
		console.info("Patch userId/groupId: ", user.id, group.id);
		
		var config = {
				headers : {
					'Accept' : 'application/scim+json',
					'Content-Type' : 'application/scim+json'
				}
			}
		var patchRequest = {
			
				schemas: ["urn:ietf:params:scim:schemas:core:2.0:Group"],
				operations: [{
					op: "add",
					path: "members",
					value: {
						display: user.name.formatted,
						value: user.id,
						$ref: user.meta.location
					}
				}]
			
		}
		console.log(patchRequest);
		return $http.patch(urlBase + '/Groups/' + group.id, patchRequest, config);
	};

	return scimFactory;
} ]);
