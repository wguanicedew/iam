<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>

<o:iamHeader title="Registration Request Management" />

<body ng-app="registrationApp" class="ng-cloak">
	<div class="main-container"
		ng-controller="RequestManagementController as ctrl"
		ng-init="ctrl.listRequests()">
		
		<div class="panel panel-default">
			<div class="panel-heading">
				<span class="lead">List of Requests</span>
			</div>

			<div>
				<div class="alert alert-success" ng-show="showSuccessAlert">
					<button type="button" class="close"
						data-ng-click="switchBool('showSuccessAlert')">&times;</button>
					<strong>Done!</strong> {{textAlert}}
				</div>
			</div>

			<div>
				<div class="alert alert-danger" ng-show="showErrorAlert">
					<button type="button" class="close"
						data-ng-click="switchBool('showErrorAlert')">&times;</button>
					<strong>Error!</strong> {{textAlert}}
				</div>
			</div>

			<div class="tablecontainer">
				<table class="table table-hover">
					<thead>
						<tr>
							<th>UUID</th>
							<th>
								<div class="simil-link" ng-click="sortType = 'creationTime'; sortReverse = !sortReverse">
									CreationTime
									<span ng-show="sortType == 'creationTime' && !sortReverse" class="fa fa-caret-down"></span>
        							<span ng-show="sortType == 'creationTime' && sortReverse" class="fa fa-caret-up"></span>
								</div>
							</th>
							<th>
								<div class="simil-link" ng-click="sortType = 'status'; sortReverse = !sortReverse">
									Status
									<span ng-show="sortType == 'status' && !sortReverse" class="fa fa-caret-down"></span>
        							<span ng-show="sortType == 'status' && sortReverse" class="fa fa-caret-up"></span>
								</div>
							</th>
							<th>Given Name</th>
							<th>Family Name</th>
							<th>Username</th>
							<th>Email</th>
							<th width="20%"></th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="r in ctrl.list | orderBy:sortType:sortReverse">
							<td><span ng-bind="r.uuid"></span></td>
							<td><span
								ng-bind="r.creationTime | date:'dd/MM/yyyy HH:mm:ss'"></span></td>
							<td><span ng-bind="r.status"></span></td>
							<td><span ng-bind="r.givenname"></span></td>
							<td><span ng-bind="r.familyname"></span></td>
							<td><span ng-bind="r.username"></span></td>
							<td><span ng-bind="r.email"></span></td>
							<td>
								<button type="button" ng-click="ctrl.approveRequest(r.uuid)" ng-disabled="r.status != 'CONFIRMED'"
									class="btn btn-success custom-width">Approve</button>
								<button type="button" ng-click="ctrl.rejectRequest(r.uuid)" ng-disabled="r.status != 'CONFIRMED'"
									class="btn btn-danger custom-width">Reject</button>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		
		<div style="width: 200px; margin: auto;">
			<a class="btn btn-primary btn-block" style="align: left" href='/'>Back to Home Page</a>
		</div>
	</div>

	<script type="text/javascript" src="<c:url value='/webjars/angularjs/angular.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/angular-ui-bootstrap/ui-bootstrap.js'/>"></script>
		
	<script type="text/javascript" src="/resources/iam/js/registration_app.js"></script>
	<script type="text/javascript" src="/resources/iam/js/service/registration_service.js"></script>
	<script type="text/javascript" src="/resources/iam/js/controller/registration_controller.js"></script>
	
</body>
