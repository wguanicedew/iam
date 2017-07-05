(function () {
    'use strict';

    function AddRemoveCertificateController($scope, scimFactory, $uibModalInstance, action, cert, user, successHandler) {
        var self = this;
        self.enabled = true;
        self.action = action;
        self.cert = cert;
        self.user = user;
        self.error = undefined;
        self.successHandler = successHandler;

        self.certVal = {};

        self.doAdd = function () {
            self.error = undefined;
            self.enabled = false;
            scimFactory.addX509Certificate(self.user.id, self.certVal).then(
                function (response) {
                    $uibModalInstance.close(response.data);
                    self.successHandler(`Certificate ${self.certVal.label} added succesfully`);
                    self.enabled = true;
                }).catch(function (error) {
                    console.error(error);
                    self.error = error;
            });
        };

        self.doRemove = function () {
            self.error = undefined;
            self.enabled = false;
            scimFactory.removeX509Certificate(self.user.id, self.cert)
                .then(function (response) {
                    $uibModalInstance.close(response.data);
                    self.successHandler(`Certificate ${self.cert.subjectDn} removed succesfully`);
                    self.enabled = true;
                }).catch(function (error) {
                    console.error(error);
                    self.error = error;
                });
        };

        self.cancel = function () {
            $uibModalInstance.dismiss('Dismissed');
        };

        self.reset = function () {
            self.certVal = {
                label: "",
                primary: false,
                pemEncodedCertificate: "",
            };
            self.error = undefined;
        };

        self.certLabelValid = function() {
            return $scope.certLabel.$dirty && $scope.certLabel.$valid;
        };
    }

    function LinkCertificateController(AccountLinkingService, $uibModalInstance, action, cert) {

        var self = this;

        self.enabled = true;
        self.action = action;
        self.actionUrl = '/iam/account-linking/X509';
        self.cert = cert;

        self.doLink = function () {
            self.enabled = false;
            document.getElementById("link-certificate-form").submit();
        };

        self.doUnlink = function () {
            self.enabled = false;
            AccountLinkingService.unlinkX509Certificate(self.cert).then(function (response) {
                $uibModalInstance.close(response);
            }).catch(function (error) {
                console.error(error);
            });
        };

        self.cancel = function () {
            $uibModalInstance.dismiss('Dismissed');
        };
    }

    function UserX509Controller(toaster, $uibModal, ModalService, scimFactory) {
        var self = this;

        self.indigoUser = function () {
            return self.user['urn:indigo-dc:scim:schemas:IndigoUser'];
        };

        self.$onInit = function () {
            console.log('UserX509Controller onInit');
            self.enabled = true;
        };

        self.isVoAdmin = function () {
            return self.userCtrl.isVoAdmin();
        };

        self.isMe = function () {
            return self.userCtrl.isMe();
        };

        self.getCertificates = function () {
            if (self.indigoUser()) {
                return self.indigoUser().certificates;
            }

            return undefined;
        };

        self.getUserCertSubject = function () {
            return getUserX509CertficateSubject();
        };

        self.getUserCertIssuer = function () {
            return getUserX509CertficateIssuer();
        };

        self.hasCertificates = function () {
            var certs = self.getCertificates();
            if (certs) {
                return certs.length > 0;
            }

            return false;
        };

        self.handleSuccess = function (msg) {
            self.enabled = true;
            self.userCtrl.loadUser().then(function (user) {
                toaster.pop({
                    type: 'success',
                    body: msg
                });
            });
        };

        self.openAddCertificateDialog = function () {
            var modalInstance = $uibModal.open({
                templateUrl: '/resources/iam/js/dashboard-app/components/user/x509/cert.add.dialog.html',
                controller: AddRemoveCertificateController,
                controllerAs: '$ctrl',
                resolve: {
                    action: function () {
                        return 'add';
                    },
                    user: function () {
                        return self.user;
                    },
                    cert: undefined,
                    successHandler: function () {
                        return self.handleSuccess;
                    }
                }
            });
        };

        self.openRemoveCertificateDialog = function (cert) {
            var modalInstance = $uibModal.open({
                templateUrl: '/resources/iam/js/dashboard-app/components/user/x509/cert.remove.dialog.html',
                controller: AddRemoveCertificateController,
                controllerAs: '$ctrl',
                resolve: {
                    action: function () {
                        return 'remove';
                    },
                    user: function () {
                        return self.user;
                    },
                    cert: function () {
                        return cert;
                    },
                    successHandler: function () {
                        return self.handleSuccess;
                    }
                }
            });
        };

        self.openLinkCertificateDialog = function () {
            var modalInstance = $uibModal.open({
                templateUrl: '/resources/iam/js/dashboard-app/components/user/x509/cert.link.dialog.html',
                controller: LinkCertificateController,
                controllerAs: '$ctrl',
                resolve: {
                    action: function () {
                        return 'link';
                    },
                    cert: {
                        subjectDn: self.getUserCertSubject(),
                        issuerDn: self.getUserCertIssuer()
                    }
                }
            });

            modalInstance.result.then(self.handleSuccess);
        };

        self.openUnlinkCertificateDialog = function (certificate) {
            var modalInstance = $uibModal.open({
                templateUrl: '/resources/iam/js/dashboard-app/components/user/x509/cert.link.dialog.html',
                controller: LinkCertificateController,
                controllerAs: '$ctrl',
                resolve: {
                    action: function () {
                        return 'unlink';
                    },
                    cert: function () {
                        return certificate;
                    }
                }
            });

            modalInstance.result.then(self.handleSuccess);
        };
    }

    angular.module('dashboardApp').component('userX509', {
        require: {
            userCtrl: '^user'
        },
        bindings: {
            user: '='
        },
        templateUrl: '/resources/iam/js/dashboard-app/components/user/x509/user.x509.component.html',
        controller: [
            'toaster', '$uibModal', 'ModalService', 'scimFactory', UserX509Controller
        ]
    });
})();