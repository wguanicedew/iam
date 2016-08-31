<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>

<o:iamHeader title="Registration Request Management" />

<body ng-app="registrationApp" class="ng-cloak">
	<div class="container" ng-controller="RequestManagementController as ctrl" ng-init="ctrl.init()">

		<h2>List of Pending Requests</h2>
	
		<div ng-show="ctrl.operationResult != null">
			<div class="alert" ng-class="{'alert-success': ctrl.operationResult=='ok', 'alert-danger': ctrl.operationResult=='err'}">
				<button class="close" ng-click="ctrl.operationResult=null" aria-label="close">&times;</button>
				{{ctrl.textAlert}}
			</div>
		</div>

		<div class="panel-group" id="accordion">
			<div class="panel panel-default" ng-repeat="r in ctrl.list | orderBy:ctrl.sortType:ctrl.sortReverse">
				<div class="panel-heading">
					<h4 class="panel-title">
						<button class="btn btn-link" data-toggle="collapse" data-parent="#accordion" data-target="#collapse_{{$index}}" title="Click for more details">
							{{r.creationTime | date:'dd/MM/yyyy HH:mm:ss'}}: {{r.givenname}} {{r.familyname}} adds a registration request
						</button>
						<button type="button" class="btn btn-default btn-sm">
							<span class="glyphicon glyphicon-ok-sign" ng-click="ctrl.approveRequest(r.uuid)" title="Approve request"> OK</span>
						</button>
						<button type="button" class="btn btn-default btn-sm">
							<span class="glyphicon glyphicon-remove-sign" ng-click="ctrl.rejectRequest(r.uuid)" title="Reject request"> DEL</span>						
						</button>
					</h4>
				</div>
				<div id="collapse_{{$index}}" class="panel-collapse collapse">
					<div class="panel-body">
						<div class="row">
							<label class="control-label col-sm-2" for="status_{{$index}}">Current Status</label>
							<span class="col-sm-10" id="status_{{$index}}">{{r.status}}</span>
						</div>
						<div class="row">
							<label class="control-label col-sm-2" for="name_{{$index}}">Name</label>
							<span class="col-sm-10" id="name_{{$index}}">{{r.givenname}} {{r.familyname}}</span>
						</div>
						<div class="row">
							<label class="control-label col-sm-2" for="username_{{$index}}">Username</label>
							<span class="col-sm-10" id="username_{{$index}}">{{r.username}}</span>
						</div>
						<div class="row">
							<label class="control-label col-sm-2" for="email_{{$index}}">Email address</label>
							<span class="col-sm-10" id="email_{{$index}}">{{r.email}}</span>
						</div>
						<div class="row">
							<label class="control-label col-sm-2" for="notes_{{$index}}">Notes</label>
							<span class="col-sm-10" id="notes_{{$index}}">{{r.notes}}</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div style="width: 200px; margin: auto;">
		<a class="btn btn-primary btn-block" style="align: left" href='/'>Back to Home Page</a>
	</div>

	<script type="text/javascript" src="<c:url value='/webjars/angularjs/angular.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/angular-ui-bootstrap/ui-bootstrap.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/jquery/jquery.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/bootstrap/bootstrap.min.js'/>"></script>
		
	<script type="text/javascript" src="/resources/iam/js/registration.app.js"></script>
	<script type="text/javascript" src="/resources/iam/js/service/registration.service.js"></script>
	<script type="text/javascript" src="/resources/iam/js/controller/registration.controller.js"></script>
	
</body>
