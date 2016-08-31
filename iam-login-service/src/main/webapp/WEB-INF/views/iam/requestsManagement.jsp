<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>

<o:iamHeader title="Registration Request Management" />

<body ng-app="registrationApp" class="ng-cloak">
	<div class="container"
		ng-controller="RequestManagementController as ctrl"
		ng-init="ctrl.listRequests()">
		
		<h2>List of Requests</h2>
	
		<div ng-show="ctrl.operationResult != null">
			<div class="alert" ng-class="{'alert-success': ctrl.operationResult=='ok', 'alert-danger': ctrl.operationResult=='err'}">
				<button class="close" ng-click="ctrl.operationResult=null" aria-label="close">&times;</button>
				{{ctrl.textAlert}}
			</div>
		</div>

		<div class="tablecontainer">
			<table class="table table-hover">
				<thead>
					<tr>
						<th>UUID</th>
						<th>
							<div class="btn-link text-nowrap" ng-click="ctrl.sortType = 'creationTime'; ctrl.sortReverse = !ctrl.sortReverse">
								CreationTime
								<span ng-show="ctrl.sortType == 'creationTime' && !ctrl.sortReverse" class="fa fa-caret-down"></span>
	      						<span ng-show="ctrl.sortType == 'creationTime' && ctrl.sortReverse" class="fa fa-caret-up"></span>
							</div>
						</th>
						<th>
							<div class="btn-link text-nowrap" ng-click="ctrl.sortType = 'status'; ctrl.sortReverse = !ctrl.sortReverse">
								Status
								<span ng-show="ctrl.sortType == 'status' && !ctrl.sortReverse" class="fa fa-caret-down"></span>
	      						<span ng-show="ctrl.sortType == 'status' && ctrl.sortReverse" class="fa fa-caret-up"></span>
							</div>
						</th>
						<th>Name</th>
						<th>Surname</th>
						<th>Username</th>
						<th>Email</th>
						<th width="20%"></th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="r in ctrl.list | orderBy:ctrl.sortType:ctrl.sortReverse">
						<td><span ng-bind="r.uuid"></span></td>
						<td><span ng-bind="r.creationTime | date:'dd/MM/yyyy HH:mm:ss'"></span></td>
						<td><span ng-bind="r.status"></span></td>
						<td><span ng-bind="r.givenname"></span></td>
						<td><span ng-bind="r.familyname"></span></td>
						<td><span ng-bind="r.username"></span></td>
						<td><span ng-bind="r.email"></span></td>
						<td>
							<button class="btn btn-success custom-width" type="button" ng-click="ctrl.approveRequest(r.uuid)" ng-disabled="r.status != 'CONFIRMED'">Approve</button>
							<button class="btn btn-danger custom-width" type="button" ng-click="ctrl.rejectRequest(r.uuid)" ng-disabled="r.status != 'CONFIRMED'">Reject</button>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>

	<div style="width: 200px; margin: auto;">
		<a class="btn btn-primary btn-block" style="align: left" href='/'>Back to Home Page</a>
	</div>

	<script type="text/javascript" src="<c:url value='/webjars/angularjs/angular.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/angular-ui-bootstrap/ui-bootstrap.js'/>"></script>
		
	<script type="text/javascript" src="/resources/iam/js/registration.app.js"></script>
	<script type="text/javascript" src="/resources/iam/js/service/registration.service.js"></script>
	<script type="text/javascript" src="/resources/iam/js/controller/registration.controller.js"></script>
	
</body>
