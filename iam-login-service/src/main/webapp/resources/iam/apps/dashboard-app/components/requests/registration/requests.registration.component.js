/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function() {
    'use strict';

    function RejectRequest($uibModalInstance, RegistrationRequestService, request, userFullName) {
        var self = this;

        self.request = request;
        self.userFullName = userFullName;

        self.cancel = function() {
            $uibModalInstance.dismiss('Dismissed');
        };

        self.reject = function() {
            RegistrationRequestService.rejectRequest(self.request).then(function(r) {
                $uibModalInstance.close(r);
            }).catch(function(r) {
                toaster.pop({
                    type: 'error',
                    body: r.statusText
                });
            });
        };

    }

    function RegistrationRequests(Utils, $scope, $rootScope, $uibModal, RegistrationRequestService, filterFilter, toaster) {
        var self = this;

        self.loaded = false;
        self.filter = "";
        self.filtered = [];
        self.busy = false;
        self.itemsPerPage = 10;
        self.currentPage = 1;

        self.$onInit = function() {
            self.api = {};
            self.api.load = loadPendingRequests;
            self.parentCb({ $API: self.api });

            if (Utils.isAdmin()){
                loadPendingRequests();
            }

        };

        self.resetFilter = function() {
            self.filter = "";
        }

        function errorHandler(res) {

        }

        function listRequestSuccess(res) {
            self.filtered = self.requests = res.data;
            updatePageCounters();
            updateRootScopeCounters(res);
            self.busy = false;
            self.loaded = true;
            self.reqCount = self.filtered.length;
        }

        function updateRootScopeCounters(res) {
            $rootScope.pendingRegistrationRequests(res.data);
        }

        function updatePageCounters() {

            self.pageLeft = ((self.currentPage - 1) * self.itemsPerPage) + 1;
            self.pageRight = Math.min(self.currentPage * self.itemsPerPage, self.filtered.length);

        }

        $scope.$watch('$ctrl.filter', function() {
            filterRequests();
        });

        function loadPendingRequests() {
            return RegistrationRequestService.listPending().then(listRequestSuccess, errorHandler);
        }

        function filterRequests() {
            self.filtered = filterFilter(self.requests, function(request) {

                if (!self.filter) {
                    return true;
                }

                var query = self.filter.toLowerCase();

                if (request.familyname.toLowerCase().indexOf(query) != -1) {
                    return true;
                }
                if (request.givenname.toLowerCase().indexOf(query) != -1) {
                    return true;
                }
                if (request.username.toLowerCase().indexOf(query) != -1) {
                    return true;
                }
                if (request.email.toLowerCase().indexOf(query) != -1) {
                    return true;
                }
                if (request.notes.toLowerCase().indexOf(query) != -1) {
                    return true;
                }
                return false;
            });

            if (self.filtered) {
                updatePageCounters();
            }
        }

        self.pageChanged = function() {
            updatePageCounters();
        };

        function approveSuccess(res) {
            loadPendingRequests().then(function(res) {
                toaster.pop({
                    type: 'success',
                    body: "Request approved"
                });
            });
        }

        function rejectSuccess(res) {
            loadPendingRequests().then(function(res) {
                toaster.pop({
                    type: 'success',
                    body: "Request rejected"
                });
            });
        }

        function decisionErrorHandler(res) {
            toaster.pop({
                type: 'error',
                body: r.statusText
            });
            self.busy = false;
        }

        self.approve = function(req) {
            self.busy = true;
            RegistrationRequestService.approveRequest(req).then(approveSuccess, decisionErrorHandler);
        };

        self.reject = function(req) {
            var modal = $uibModal.open({
                templateUrl: '/resources/iam/apps/dashboard-app/components/requests/reject-request.dialog.html',
                controller: RejectRequest,
                controllerAs: '$ctrl',
                resolve: {
                    request: req,
                    userFullName: function() {
                        return `${req.givenname} ${req.familyname}`;
                    }
                }
            });

            modal.result.then(rejectSuccess, decisionErrorHandler);
        };
    }

    angular
        .module('dashboardApp')
        .component('registrationRequests', registrationRequests());

    function registrationRequests() {
        return {
            bindings: {
                parentCb: '&'
            },
            templateUrl: "/resources/iam/apps/dashboard-app/components/requests/registration/requests.registration.component.html",
            controller: RegistrationRequests,
            controllerAs: '$ctrl'
        };
    }
}());