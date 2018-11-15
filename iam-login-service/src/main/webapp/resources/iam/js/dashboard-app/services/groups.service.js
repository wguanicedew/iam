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
'use strict'

angular.module('dashboardApp').factory('GroupsService', GroupsService);

GroupsService.$inject = [ '$q', '$rootScope', '$http', '$httpParamSerializer' ];

function GroupsService($q, $rootScope, $http, $httpParamSerializer) {
    var service = {
        getGroupsFilteredAndSortBy : getGroupsFilteredAndSortBy,
        getGroupsFilteredBy : getGroupsFilteredBy,
        getGroupsSortBy : getGroupsSortBy,
        getGroupsCount : getGroupsCount,
        getGroups : getGroups
    };

    var urlGroups = "/iam/group/search";

    return service;

    function doGet(queryStr) {
        var url = urlGroups + '?' + queryStr;
        return $http.get(url);
    }

    function getGroupsFilteredAndSortBy(startIndex, count, filter, sortBy, sortDir) {
        console.debug("getGroupsFilteredAndSortBy: ", startIndex, count, filter, sortBy, sortDir);
        return doGet($httpParamSerializer({
            'startIndex' : startIndex,
            'count' : count,
            'filter' : filter,
            'sortBy' : sortBy,
            'sortDirection': sortDir
        }));
    }

    function getGroupsFilteredBy(startIndex, count, filter) {
        console.debug("getGroupsFilteredBy: ", startIndex, count, filter);
        return doGet($httpParamSerializer({
            'startIndex' : startIndex,
            'count' : count,
            'filter' : filter
        }));
    }

    function getGroupsSortBy(startIndex, count, sortBy, sortDir) {
        console.debug("getGroupsSortBy: ", startIndex, count, sortBy, sortDir);
        return doGet($httpParamSerializer({
            'startIndex' : startIndex,
            'count' : count,
            'sortBy' : sortBy,
            'sortDirection': sortDir
        }));
    }

    function getGroups(startIndex, count) {
        console.debug("getGroups: ", startIndex, count);
        return doGet($httpParamSerializer({
            'startIndex' : startIndex,
            'count' : count
        }));
    }

    function getGroupsCount() {
        console.debug("getGroupsCount");
        return doGet($httpParamSerializer({
            'count' : 0
        }));
    }
}