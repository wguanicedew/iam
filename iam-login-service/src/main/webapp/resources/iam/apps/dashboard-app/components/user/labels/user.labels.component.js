(function () {
    'use strict';

    angular
        .module('dashboardApp')
        .component('userLabels', userLabels());


    function UserLabelsController(toaster, $uibModal, AccountLabelsService) {
        var self = this;

        self.$onInit = function () {
            console.log('UserLabelsController onInit');
            console.log('UserLabelsController self.labels: ', self.labels);
        };

    }

    function userLabels() {
        return {
            templateUrl: "/resources/iam/apps/dashboard-app/components/user/labels/user.labels.component.html",
            bindings: {
                user: "<",
                labels: "="
            },
            controller: ['toaster', '$uibModal', 'AccountLabelsService',
                UserLabelsController
            ],
            controllerAs: '$ctrl'
        };
    }

})();