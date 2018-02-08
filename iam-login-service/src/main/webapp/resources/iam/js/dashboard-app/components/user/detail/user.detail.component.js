(function() {
    'use strict';

    function UserDetailController(Utils, AupService) {
        var self = this;

        self.$onInit = function() {
            console.log('userDetailController onInit');
            console.log('userDetailController self.aup: ', self.aup);
        };

        self.isVoAdmin = function() {
            return Utils.userIsVoAdmin(self.user);
        };

        self.aupIsEnabled = function() {
            return self.aup !== null;
        };

        self.indigoUser = function() {
            return self.user['urn:indigo-dc:scim:schemas:IndigoUser'];
        };

        self.hasAupSignatureTime = function() {
            return self.user.aupSignature !== null;
        };
    }

    angular.module('dashboardApp').component('userDetail', {
        templateUrl: "/resources/iam/js/dashboard-app/components/user/detail/user.detail.component.html",
        bindings: { user: '=', aup: '=' },
        controller: ['Utils', 'AupService', UserDetailController]
    });
})();