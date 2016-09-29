angular.module('dashboardApp').factory('gatewayErrorInterceptor',
		gatewayErrorInterceptor);

gatewayErrorInterceptor.inject = ['$q', '$rootScope'];

function gatewayErrorInterceptor($q, $rootScope) {
	var interceptor = {

		responseError: function(response) {

			// Bad Gateway error
			if (response.status == 502) {

				console.log("gateway error caught");
				$rootScope.openOfflineDialog();

			}

			return $q.reject(response);
		}
	};
	return interceptor;
}