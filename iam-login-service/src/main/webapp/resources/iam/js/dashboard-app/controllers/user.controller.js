'use strict';

angular.module('dashboardApp').controller('UserController', UserController);

UserController.$inject = [ '$scope', '$rootScope', '$state', '$uibModal', '$filter', 'filterFilter', 'Utils', 'scimFactory', 'ModalService', 'ResetPasswordService', 'RegistrationRequestService' ];

function UserController($scope, $rootScope, $state, $uibModal, $filter, filterFilter, Utils, scimFactory, ModalService, ResetPasswordService, RegistrationRequestService) {

	var user = this;

	console.log("User ID: ", $state.params.id);

	user.id = $state.params.id;

	user.userInfo = {};

	// methods
	user.getIndigoUserInfo = getIndigoUserInfo;
	user.showSshKeyValue = showSshKeyValue;
	user.showCertValue = showCertValue;
	user.openAddGroupDialog = openAddGroupDialog;
	user.openAddOIDCAccountDialog = openAddOIDCAccountDialog;
	user.openAddSshKeyDialog = openAddSshKeyDialog;
	user.openAddSamlAccountDialog = openAddSamlAccountDialog;
	user.openAddX509CertificateDialog = openAddX509CertificateDialog;
	user.openEditUserDialog = openEditUserDialog;
	user.deleteGroup = deleteGroup;
	user.deleteOidcAccount = deleteOidcAccount;
	user.deleteSshKey = deleteSshKey;
	user.deleteX509Certificate = deleteX509Certificate;
	user.deleteSamlId = deleteSamlId;
	user.setActive = setActive;
	
	// password reset
	user.doPasswordReset = doPasswordReset;
	user.sendResetMail = sendResetMail;

	user.getIndigoUserInfo();

	function getIndigoUserInfo() {

		$rootScope.userLoadingProgress = 0;
		console.log("progress", $rootScope.userLoadingProgress);
	
		
		user.loadingModal = $uibModal
		.open({
			animation: false,
			templateUrl : '/resources/iam/template/dashboard/user/loading-modal.html'
		});

		user.loadingModal.opened.then(function() {

			$rootScope.userLoadingProgress = 50;

			scimFactory.getUser(user.id).then(function(response) {
				
				user.userInfo = response.data;
				console.log("Added indigoUserInfo: ", user.userInfo);

				$rootScope.userLoadingProgress = 100;
				console.log("progress", $rootScope.userLoadingProgress);
				console.log("dismissing");
				user.loadingModal.dismiss("Cancel");
				
			}, function(error) {
				
				user.loadingModal.dismiss("Error");
				$state.go("error", {
					"error": error
				});
			});

		});

	}

	function doPasswordReset() {
		
		ResetPasswordService.forgotPassword(user.userInfo.emails[0].value).then(
			function(response) {
				ModalService.showModal({}, {
					closeButtonText: null,
					user: user.userInfo.name.formatted,
					actionButtonText: 'OK',
					headerText: 'Passoword reset requested',
					bodyText: `A password reset link has just been sent to your e-mail address`
				});
			}, function(error) {
				user.textAlert = error.data.error_description || error.data.detail;
				user.operationResult = 'err';
			});
	}

	function sendResetMail() {
		
		var modalOptions = {
				closeButtonText: 'Cancel',
				user: user.userInfo.name.formatted,
				actionButtonText: 'Send password reset e-mail',
				headerText: 'Password Reset',
				bodyText: `Are you sure you want to send the reset password link to ${user.userInfo.name.formatted}?`	
			};
				
			ModalService.showModal({}, modalOptions).then(
				function (){
					user.doPasswordReset();
				});
	}

	function openEditUserDialog() {

		var modalInstance = $uibModal
				.open({
					templateUrl : '/resources/iam/template/dashboard/user/edituser.html',
					controller : 'EditUserController',
					controllerAs : 'editUserCtrl',
					resolve : {
						user : function() {
							console.log(user.userInfo);
							return user.userInfo;
						}
					}
				});
		modalInstance.result.then(function() {
			getIndigoUserInfo();
			user.textAlert = `User's info updated successfully`;
			user.operationResult = 'ok';
		}, function() {
			console.info('Modal dismissed at: ', new Date());
		});
	}
	
	function openAddGroupDialog() {
		var modalInstance = $uibModal
				.open({
					templateUrl : '/resources/iam/template/dashboard/user/addusergroup.html',
					controller : 'AddUserGroupController',
					controllerAs : 'addGroupCtrl',
					resolve : {
						user : function() {
							return user.userInfo;
						}
					}
				});
		modalInstance.result.then(function() {
			console.info("Added group");
			getIndigoUserInfo();
			user.textAlert = `User's memberships updated successfully`;
			user.operationResult = 'ok';
		}, function() {
			console.info('Modal dismissed at: ', new Date());
		});
	}

	function openAddOIDCAccountDialog() {
		var modalInstance = $uibModal.open({
			templateUrl : '/resources/iam/template/dashboard/user/addoidc.html',
			controller : 'AddOIDCAccountController',
			controllerAs : 'addOidcCtrl',
			resolve : {
				user : function() {
					return user.userInfo;
				}
			}
		});
		modalInstance.result.then(function() {
			console.info("Added oidc account");
			getIndigoUserInfo();
			user.textAlert = `User's Open ID Account created successfully`;
			user.operationResult = 'ok';
		}, function() {
			console.info('Modal dismissed at: ', new Date());
		});
	}

	function openAddSshKeyDialog() {
		var modalInstance = $uibModal.open({
			templateUrl : '/resources/iam/template/dashboard/user/addsshkey.html',
			controller : 'AddSshKeyController',
			controllerAs : 'addSshKeyCtrl',
			resolve : {
				user : function() {
					return user.userInfo;
				}
			}
		});
		modalInstance.result.then(function() {
			console.info("Added ssh key");
			getIndigoUserInfo();
			user.textAlert = `User's SSH Key added successfully`;
			user.operationResult = 'ok';
		}, function(error) {
			console.info('Modal dismissed at: ', new Date());
		});
	}

	function openAddSamlAccountDialog() {
		var modalInstance = $uibModal
				.open({
					templateUrl : '/resources/iam/template/dashboard/user/addsamlaccount.html',
					controller : 'AddSamlAccountController',
					controllerAs : 'addSamlAccountCtrl',
					resolve : {
						user : function() {
							return user.userInfo;
						}
					}
				});
		modalInstance.result.then(function() {
			console.info("Added saml account");
			getIndigoUserInfo();
			user.textAlert = `User's SAML Account created successfully`;
			user.operationResult = 'ok';
		}, function() {
			console.info('Modal dismissed at: ', new Date());
		});
	}

	function openAddX509CertificateDialog() {
		var modalInstance = $uibModal
				.open({
					templateUrl : '/resources/iam/template/dashboard/user/addx509certificate.html',
					controller : 'AddX509CertificateController',
					controllerAs : 'addX509CertCtrl',
					resolve : {
						user : function() {
							return user.userInfo;
						}
					}
				});
		modalInstance.result.then(function() {
			console.info("Added x509 certificate");
			getIndigoUserInfo();
			user.textAlert = `User's x509 Certificate added successfully`;
			user.operationResult = 'ok';
		}, function() {
			console.info('Modal dismissed at: ', new Date());
		});
	}

	function deleteGroup(group) {
		
		var modalOptions = {
			closeButtonText: 'Cancel',
			user: user.userInfo.name.formatted,
			actionButtonText: 'Remove user from group',
			headerText: 'Remove membership',
			bodyText: `Are you sure you want to remove user memebership to '${group.display}'?`	
		};
			
		ModalService.showModal({}, modalOptions).then(
			function (){
				scimFactory.removeUserFromGroup(group.value, user.userInfo.id,
						user.userInfo.meta.location, user.userInfo.name.formatted)
					.then(function(response) {
						console.log("Deleted: ", group);
						getIndigoUserInfo();
						user.textAlert = `Group ${group.display} membership removed successfully`;
						user.operationResult = 'ok';
					}, function(error) {
						user.textAlert = error.data.error_description || error.data.detail;
						user.operationResult = 'err';
					});
			});
	}

	function showSshKeyValue(value) {

		ModalService.showModal({}, {
			closeButtonText: null,
			user: user.userInfo.name.formatted,
			actionButtonText: 'OK',
			headerText: 'SSH Key value',
			bodyText: `${value}`
		});
	}

	function showCertValue(cert) {

		ModalService.showModal({}, {
			closeButtonText: null,
			user: user.userInfo.name.formatted,
			actionButtonText: 'OK',
			headerText: 'x509 Certificate value',
			bodyText: `${cert.value}`
		});
	}

	function deleteOidcAccount(oidcId) {

		var summary = oidcId.issuer + " - " + oidcId.subject;
		
		var modalOptions = {
				closeButtonText: 'Cancel',
				user: user.userInfo.name.formatted,
				actionButtonText: 'Remove Open ID Account',
				headerText: 'Remove Open ID Account',
				bodyText: `Are you sure you want to remove the Open ID Account?`,
				bodyDetail: `${summary}`
			};
				
			ModalService.showModal({}, modalOptions).then(
				function (){
					scimFactory.removeOpenIDAccount(user.userInfo.id, oidcId.issuer,
							oidcId.subject)
						.then(function(response) {
							console.log("Removed: ", oidcId.issuer, oidcId.subject);
							getIndigoUserInfo();
							user.textAlert = `Open ID Account [${summary}] has been removed successfully`;
							user.operationResult = 'ok';
						}, function(error) {
							user.textAlert = error.data.error_description || error.data.detail;
							user.operationResult = 'err';
						});
				});
	}

	function deleteSshKey(sshKey) {
		
		var modalOptions = {
				closeButtonText: 'Cancel',
				user: user.userInfo.name.formatted,
				actionButtonText: 'Remove ssh key',
				headerText: 'Remove ssh key',
				bodyText: `Are you sure you want to remove the ssh key [${sshKey.display}]?`	
			};
				
			ModalService.showModal({}, modalOptions).then(
				function (){
					scimFactory.removeSshKey(user.userInfo.id, sshKey.fingerprint)
						.then(function(response) {
							console.log("Removed: ", sshKey.display, sshKey.fingerprint);
							getIndigoUserInfo();
							user.textAlert = `Ssh key [${sshKey.display}] has been removed successfully`;
							user.operationResult = 'ok';
						}, function(error) {
							user.textAlert = error.data.error_description || error.data.detail;
							user.operationResult = 'err';
						});
				});
	}

	function deleteX509Certificate(x509cert) {

		var modalOptions = {
				closeButtonText: 'Cancel',
				user: user.userInfo.name.formatted,
				actionButtonText: 'Remove x509 Certificate',
				headerText: 'Remove x509 Certificate',
				bodyText: `Are you sure you want to remove this x509 Certificate?`,
				bodyDetail: `${x509cert.display}`	
			};
				
			ModalService.showModal({}, modalOptions).then(
				function (){
					scimFactory.removeX509Certificate(user.userInfo.id, x509cert.value)
						.then(function(response) {
							console.log("Removed: ", x509cert.display);
							getIndigoUserInfo();
							user.textAlert = `X509 Certificate [${x509cert.display}] has been removed successfully`;
							user.operationResult = 'ok';
						}, function(error) {
							user.textAlert = error.data.error_description || error.data.detail;
							user.operationResult = 'err';
						});
				});
	}

	function deleteSamlId(samlId) {

		var summary = samlId.idpId + ", " + samlId.userId;
		
		var modalOptions = {
				closeButtonText: 'Cancel',
				user: user.userInfo.name.formatted,
				actionButtonText: 'Remove SAML Account',
				headerText: 'Remove SAML account',
				bodyText: `Are you sure you want to remove this SAML Account?`,
				bodyDetail: `${summary}`	
			};
				
			ModalService.showModal({}, modalOptions).then(
				function (){
					scimFactory.removeSamlId(user.userInfo.id, samlId)
						.then(function(response) {
							console.log("Removed: ", samlId.idpId, samlId.userId);
							getIndigoUserInfo();
							user.textAlert = `SAML account [${summary}] has been removed successfully`;
							user.operationResult = 'ok';
						}, function(error) {
							user.textAlert = error.data.error_description || error.data.detail;
							user.operationResult = 'err';
						});
				});
	}

	function setActive(status) {

		var modalOptions = {};
		
		if (status) {
			
			modalOptions = {
				closeButtonText: 'Cancel',
				actionButtonText: 'Enable user',
				headerText: 'Enable ' + user.userInfo.name.formatted,
				bodyText: `Are you sure you want to enable '${user.userInfo.name.formatted}'?`	
			};
		} else {
			
			modalOptions = {
				closeButtonText: 'Cancel',
				actionButtonText: 'Disable user',
				headerText: 'Disable ' + user.userInfo.name.formatted,
				bodyText: `Are you sure you want to disable '${user.userInfo.name.formatted}'?`	
			};
		}
		
		ModalService.showModal({}, modalOptions).then(
			function (){
				scimFactory.setUserActiveStatus(user.userInfo.id, status)
					.then(function(response) {
						console.log("Set active as: ", status);
						getIndigoUserInfo();
						user.textAlert = `User ${user.userInfo.name.formatted} status successfully set to ${action}`;
						user.operationResult = 'ok';
					}, function(error) {
						user.textAlert = error.data.error_description || error.data.detail;
						user.operationResult = 'err';
					});
			});
	}
}