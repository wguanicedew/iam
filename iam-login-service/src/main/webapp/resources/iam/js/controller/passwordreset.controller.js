'use strict'

angular.module('passwordResetApp').controller('ResetPasswordController', ResetPasswordController);

ResetPasswordController.$inject = ['$scope']

function ResetPasswordController($scope){
	var vm = this;
	vm.password;
	vm.password-repeat;
	
};


angular.module('passwordResetApp').controller('ForgotPasswordController', ForgotPasswordController);

ForgotPasswordController.$inject = ['$scope', '$uibModalInstance', 'ResetPasswordService']

function ForgotPasswordController($scope, $uibModalInstance, ResetPasswordService){

	$scope.email;
	
	$scope.submit = function(){
		ResetPasswordService.forgotPassword($scope.email).then(
				function(response) {
					$scope.textAlert = "Success";
					$scope.showSuccessAlert = true;
					$scope.showErrorAlert = false;
					$scope.reset();
					return response.data;
				},
				function(errResponse) {
					$scope.textAlert = errResponse.data.error_description
							|| errResponse.data.detail;
					$scope.showErrorAlert = true;
					$scope.showSuccessAlert = false;
					return $q.reject(errResponse);
				}
		);
	}
	
	$scope.dismiss = function() {
		$uibModalInstance.close();
	};
	
	// switch flag
	$scope.switchBool = function(value) {
		$scope[value] = !$scope[value];
	};
};


angular.module('passwordResetApp').controller('ForgotPasswordModalController', ForgotPasswordModalController);

ForgotPasswordModalController.$inject = ['$scope', '$uibModal'];

function ForgotPasswordModalController($scope, $uibModal){
	$scope.open = function() {
		$uibModal.open({
			templateUrl : "/resources/iam/template/forgotPassword.html",
			controller : "ForgotPasswordController",
			size : 'lg',
			animation : true
		});
	};
};