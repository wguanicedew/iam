'use strict';

angular.module('dashboardApp').controller('UserController', UserController);

UserController.$inject = [ '$scope', '$rootScope', '$state', '$uibModal', '$filter', 'filterFilter', 'Utils', 'scimFactory', 'ModalService', 'ResetPasswordService', 'RegistrationRequestService' ];

function UserController($scope, $rootScope, $state, $uibModal, $filter, filterFilter, Utils, scimFactory, ModalService, ResetPasswordService, RegistrationRequestService) {

	var user = this;

	console.log("User ID: ", $state.params.id);

	user.id = $state.params.id;
	
	user.groups = [];
	user.oGroups = [];

	user.userInfo = {};
	user.filteredRequestsList = [];
	user.requestsList = [];

	// methods
	user.getIndigoUserInfo = getIndigoUserInfo;
	user.getAllGroups = getAllGroups;
	user.getNotMemberGroups = getNotMemberGroups;
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
	
	// history requests
	user.buildFilteredList = buildFilteredList;
	user.listRequests = listRequests;

	user.getIndigoUserInfo();
	user.getAllGroups(1, 10);
	user.listRequests();

	function getIndigoUserInfo() {
		scimFactory.getUser(user.id).then(function(response) {
			user.userInfo = response.data;
			console.log("Added indigoUserInfo: ", user.userInfo);
		}, function(error) {
			$state.go("error", {
				"error": error
			});
		});
	}

	function doPasswordReset() {
		
		ResetPasswordService.forgotPassword(user.userInfo.emails[0].value).then(
			function(response) {
				ModalService.showModal({}, {
					closeButtonText: null,
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
				actionButtonText: 'Send password reset e-mail?',
				headerText: 'Password Reset',
				bodyText: `Are you sure you want to send the reset password link to the user?`	
			};
				
			ModalService.showModal({}, modalOptions).then(
				function (){
					user.doPasswordReset();
				});
	}

	function getAllGroups(startIndex, count) {

		scimFactory
				.getGroups(startIndex, count)
				.then(
						function(response) {

							angular.forEach(response.data.Resources, function(
									group) {
								user.groups.push(group);
							});
							user.groups = $filter('orderBy')(user.groups,
									"displayName", false);

							if (response.data.totalResults > (response.data.startIndex + response.data.itemsPerPage)) {
								user.getAllGroups(startIndex + count, count);
							} else {
								user.oGroups = getNotMemberGroups();
							}
						}, function(error) {
							$state.go("error", {
								"error": error
							});
						});
	}

	function getNotMemberGroups() {

		return user.groups.filter(function(group) {
			for ( var i in user.userInfo.groups) {
				if (group.id === user.userInfo.groups[i].value) {
					return false;
				}
			}
			return true;
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
		user.oGroups = getNotMemberGroups();
		var modalInstance = $uibModal
				.open({
					templateUrl : '/resources/iam/template/dashboard/user/addusergroup.html',
					controller : 'AddUserGroupController',
					controllerAs : 'addGroupCtrl',
					resolve : {
						user : function() {
							return user.userInfo;
						},
						oGroups : function() {
							return user.oGroups;
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
			actionButtonText: 'Remove user from group',
			headerText: 'Remove membership?',
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
			actionButtonText: 'OK',
			headerText: 'SSH Key value',
			bodyText: `${value}`
		});
	}

	function showCertValue(cert) {

		ModalService.showModal({}, {
			closeButtonText: null,
			actionButtonText: 'OK',
			headerText: 'x509 Certificate value',
			bodyText: `${cert.value}`
		});
	}

	function deleteOidcAccount(oidcId) {

		var summary = oidcId.issuer + ", " + oidcId.subject;
		
		var modalOptions = {
				closeButtonText: 'Cancel',
				actionButtonText: 'Remove Open ID Account',
				headerText: 'Remove Open ID Account?',
				bodyText: `Are you sure you want to remove the Open ID Account [${summary}]?`	
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
				actionButtonText: 'Remove ssh key',
				headerText: 'Remove ssh key?',
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
				actionButtonText: 'Remove x509 Certificate',
				headerText: 'Remove ssh key?',
				bodyText: `Are you sure you want to remove the x509 Certificate [${x509cert.display}]?`	
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
				actionButtonText: 'Remove SAML Account',
				headerText: 'Remove SAML account?',
				bodyText: `Are you sure you want to remove SAML Account [${summary}]?`	
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

		var headerMsg = status ? 'Enable user?' : 'Disable user?';
		var action = status ? 'enable' : 'disable';
		
		var modalOptions = {
				closeButtonText: 'Cancel',
				actionButtonText: 'Change user status',
				headerText: headerMsg,
				bodyText: `Are you sure you want to ${action} '${user.userInfo.name.formatted}'?`	
			};
				
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
	
	function buildFilteredList() {
		
		user.filteredRequestsList = filterFilter(user.requestsList, {'accountId': user.userInfo.id }); ;
	}
	
	function listRequests() {
		RegistrationRequestService.listRequests().then(
			function(result) {
				console.log(result);
				user.requestsList = result.data;
				user.buildFilteredList();
			},
			function(errResponse) {
				user.textAlert = errResponse.data.error_description || errResponse.data.detail;
				user.operationResult = 'err';
			})
	};
}