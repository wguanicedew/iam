'use strict'

angular.module('passwordResetApp').controller('ResetPasswordController', ResetPasswordController);

ResetPasswordController.$inject = ['$scope', 'ResetPasswordService']

function ResetPasswordController($scope, ResetPasswordService){
	var vm = this;
	vm.password;
	vm.passwordrepeat;
	vm.resetkey;
	vm.operationResult;
	vm.textAlert;
	
	vm.submit = submit;
	vm.reset = reset;
	
	
	
	
	function submit(){
		ResetPasswordService.changePassword(vm.resetKey, vm.password).then(
			function(response){
				vm.operationResult = 'ok';
				vm.reset();
			}, function(data){
				vm.operationResult = 'err';
				if (data.status == -1 ){
					vm.textAlert = 'Could not enstabilish a connection to the IAM server. Are you online?'
				} else {
					vm.textAlert = data.statusText+': '+data.data;
				}
			}
		);
	};
	
	function reset(){
		vm.password = '';
		vm.passwordrepeat = '';
		$scope.changePasswdForm.$setPristine();
	};
};


angular.module('passwordResetApp').controller('ForgotPasswordController', ForgotPasswordController);

ForgotPasswordController.$inject = ['$scope', '$uibModalInstance', 'ResetPasswordService']

function ForgotPasswordController($scope, $uibModalInstance, ResetPasswordService){

	$scope.email;
	
	$scope.submit = function(){
		ResetPasswordService.forgotPassword($scope.email).then(
				function(response){
					$scope.operationResult = 'ok';
				}, function(data) {
					$scope.operationResult = 'err';
					if (data.status == -1 ){
						$scope.textAlert = 'Could not enstabilish a connection to the IAM server. Are you online?'
					} else {
						$scope.textAlert = data.statusText+': '+data.data;
					}
				});
		$scope.reset();
	}
	
	$scope.dismiss = function() {
		$uibModalInstance.close();
	};
	
	$scope.reset = function(){
		$scope.email = '';
		$scope.forgotPasswordForm.$setPristine();
	}
};


angular.module('passwordResetApp').controller('ForgotPasswordModalController', ForgotPasswordModalController);

ForgotPasswordModalController.$inject = ['$scope', '$uibModal'];

function ForgotPasswordModalController($scope, $uibModal){
	$scope.open = function() {
		$uibModal.open({
			templateUrl : "/resources/iam/template/forgotPassword.html",
			controller : "ForgotPasswordController",
			size : '600px',
			animation : true
		});
	};
};