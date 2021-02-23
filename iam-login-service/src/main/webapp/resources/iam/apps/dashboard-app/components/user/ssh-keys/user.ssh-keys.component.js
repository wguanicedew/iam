(function () {
    'use strict';

    angular
        .module('dashboardApp')
        .component('userSshKeys', userSshKeys());

    function ManageSshKeyController($scope, scimFactory, $uibModalInstance, user, key, successHandler){
        var self = this;
        self.key = key;
        self.enabled  = true;
        self.user = user;
        self.successHandler = successHandler;
        self.error = undefined;
        self.cancel = cancel;
        self.reset = reset;
        self.addKey = addKey;
        self.removeKey = removeKey;

        self.keyVal = {};

        function cancel(){
            $uibModalInstance.dismiss('Dismissed');
        }

        function reset() {
            self.keyVal = {
                display : '',
                primary : false,
                value : '',
            };

            self.error = undefined;
        }

        function removeKey(){
            self.error = undefined;
            self.enabled = false;
            scimFactory.removeSshKey(self.user.id, self.key).then(function(res){
                $uibModalInstance.close();
                self.successHandler(res.data, 'SSH Key removed succesfully');
                self.enabled=true;
            }).catch(function(err){
                console.error(err);
                self.error = err;
                self.enabled =true;
            });
        }

        function addKey(){
            self.error = undefined;
            self.enabled = false;
            scimFactory.addSshKey(self.user.id, self.keyVal).then(function(res){
                $uibModalInstance.close();
                self.successHandler(res.data, 'SSH Key added succesfully');
                self.enabled=true;
            }).catch(function(err){
                console.error(err);
                self.error = err;
                self.enabled =true;
            });
        }

    }

    function UserSshKeysController(toaster, $uibModal, scimFactory, Utils) {
        var self = this;

        self.getKeys = getKeys;
        self.hasKeys = hasKeys;
        self.indigoUser = indigoUser;
        self.canManageKeys = canManageKeys;
        self.openAddKeyDialog = openAddKeyDialog;
        self.openRemoveKeyDialog = openRemoveKeyDialog;
        self.successHandler = successHandler;

        self.$onInit = function () {
            console.log('UserSshKeysController onInit');
        };

        function successHandler(res, msg){
            self.userCtrl.loadUser().then(function(r){
                toaster.pop({
                    type: 'success',
                    body: msg
                });
            });
        }
        
        function openAddKeyDialog(){
            $uibModal.open({
                templateUrl: '/resources/iam/apps/dashboard-app/components/user/ssh-keys/ssh-key.add.dialog.html',
                controller: ManageSshKeyController,
                controllerAs: '$ctrl',
                resolve: {
                    action: function () {
                        return 'add';
                    },
                    user: function () {
                        return self.user;
                    },
                    key: undefined,
                    successHandler: function () {
                        return self.successHandler;
                    }
                }
            });
        }

        function openRemoveKeyDialog(key){
            $uibModal.open({
                templateUrl: '/resources/iam/apps/dashboard-app/components/user/ssh-keys/ssh-key.remove.dialog.html',
                controller: ManageSshKeyController,
                controllerAs: '$ctrl',
                resolve: {
                    user: function () {
                        return self.user;
                    },
                    key: function(){
                        return key;
                    },
                    successHandler: function () {
                        return self.successHandler;
                    }
                }
            });
        }

        function indigoUser() {
            return self.user['urn:indigo-dc:scim:schemas:IndigoUser'];
        }

        function hasKeys(){
            var keys = getKeys();

            if (keys) {
                return keys.length > 0;
            }

            return false;
        }

        function getKeys(){
            if (self.indigoUser()){
                return self.indigoUser().sshKeys;
            }

            return undefined;
        }

        function canManageKeys() {
            return Utils.isAdmin() || Utils.isMe(self.user.id);
        }
    }

    function userSshKeys() {
        return {
            templateUrl: "/resources/iam/apps/dashboard-app/components/user/ssh-keys/user.ssh-keys.component.html",
            require: {
                userCtrl: '^user'
            },
            bindings: {
                user: "<"
            },
            controller: ['toaster', '$uibModal', 'scimFactory','Utils',
                UserSshKeysController
            ],
            controllerAs: '$ctrl'
        };
    }

})();