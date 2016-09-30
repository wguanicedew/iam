angular.module('dashboardApp').factory('sessionExpiredInterceptor',
		sessionExpiredInterceptor);

sessionExpiredInterceptor.inject = ['$q', '$window'];

function sessionExpiredInterceptor($q, $window) {
	var interceptor = {
		responseError : function(response) {

			// Session Expired error
			if (response.status == 401) {

				console.log("Session expired caught");
				$window.location.href = "/dashboard/expiredsession";

			}

			return $q.reject(response);
		}
	};
	return interceptor;
}