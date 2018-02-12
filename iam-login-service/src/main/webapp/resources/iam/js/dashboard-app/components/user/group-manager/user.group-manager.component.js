(function() {
    'use strict';

    function AssignGroupManagerController() {

    }

    function RevokeGroupManagerController() {

    }

    function UserGroupManagerController(toaster, Utils, $uibModal, Authorities) {
        var self = this;

        self.$onInit = function() {
            console.log('UserGroupManagerController onInit');
            self.enabled = true;
        };

        self.isMe = function() { return Utils.isMe(self.user.id); };

        self.userIsVoAdmin = function() { return self.userCtrl.userIsVoAdmin(); };

        self.openAssignDialog = function() {

            var modalInstance = $uibModal.open({
                templateUrl: '/resources/iam/template/dashboard/user/group-manager/assign-group-manager-privileges.html',
                controller: 'AssignGroupManagerController',
                controllerAs: 'ctrl',
                resolve: { user: function() { return self.user; } }
            });

            modalInstance.result.then(function() {
                self.userCtrl.loadUser().then(function(user) {
                    toaster.pop({
                        type: 'success',
                        body: `User '${user.name.formatted}' is now a VO administrator.`
                    });
                });
            });
        };

        self.openRevokeDialog = function() {

            var modalInstance = $uibModal.open({
                templateUrl: '/resources/iam/template/dashboard/user/group-manager/revoke-group-manager-privileges.html',
                controller: 'RevokeGroupManagerController',
                controllerAs: 'ctrl',
                resolve: { user: function() { return self.user; } }
            });

            modalInstance.result.then(function() {
                self.userCtrl.loadUser().then(function(user) {
                    toaster.pop({
                        type: 'success',
                        body: `User '${user.name.formatted}' is no longer a VO administrator.`
                    });
                });
            });
        };
    }

    angular.module('dashboardApp').component('userGroupManager', {
        require: { userCtrl: '^user' },
        templateUrl: '/resources/iam/js/dashboard-app/components/user/group-manager/user.group-manager.component.html',
        bindings: { user: '=' },
        controller: [
            'toaster', 'Utils', '$uibModal', 'Authorities', UserGroupManagerController
        ]
    });
})();