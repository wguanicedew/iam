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
'use strict';

angular.module('dashboardApp').controller('AddSubGroupController', AddSubGroupController);

AddSubGroupController.$inject = [ '$scope', '$rootScope', '$uibModalInstance', 'Utils', 'scimFactory', 'group' ];

function AddSubGroupController($scope, $rootScope, $uibModalInstance, Utils, scimFactory, group) {
	
	var addSubGroupCtrl = this;

	addSubGroupCtrl.parent = group;
	addSubGroupCtrl.addSubGroup = addSubGroup;
	addSubGroupCtrl.resetGroup = resetGroup;
	addSubGroupCtrl.cancel = cancel;
	
	addSubGroupCtrl.subgroup = {};

	function resetGroup() {

		addSubGroupCtrl.subgroup.displayName = "";
		addSubGroupCtrl.enabled = true;

	}

	addSubGroupCtrl.resetGroup();

	function addSubGroup() {

		addSubGroupCtrl.enabled = false;

		addSubGroupCtrl.subgroup.schemas = ["urn:ietf:params:scim:schemas:core:2.0:Group", "urn:indigo-dc:scim:schemas:IndigoGroup"];
		addSubGroupCtrl.subgroup["urn:indigo-dc:scim:schemas:IndigoGroup"] = {
				"parentGroup": {
					display: addSubGroupCtrl.parent.displayName,
					value: addSubGroupCtrl.parent.id,
					$ref: addSubGroupCtrl.parent.meta.location
				}
		}
		
		console.info(addSubGroupCtrl.subgroup);

		scimFactory.createGroup(addSubGroupCtrl.subgroup).then(function(response) {
			$rootScope.loggedUser.totGroups = $rootScope.loggedUser.totGroups + 1;
			$uibModalInstance.close(response.data);
			addSubGroupCtrl.enabled = true;
		}, function(error) {
			console.error('Error creating group', error);
			$scope.operationResult = Utils.buildErrorOperationResult(error);
			addSubGroupCtrl.enabled = true;
		});
	}

	function cancel() {
		$uibModalInstance.dismiss("cancel");
	}
}