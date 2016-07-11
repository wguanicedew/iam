<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Registration Request Management</title>
<base href="<c:url value='/'/>">

<link rel="stylesheet"
	href="<c:url value='/webjars/bootstrap/css/bootstrap.min.css'/>"></link>
  
<link href="<c:url value='/resources/css/registration_app.css' />"
	rel="stylesheet"></link>
</head>

<body ng-app="registrationApp" class="ng-cloak">
	<div class="main-container" ng-controller="RegistrationController as ctrl">
		<div class="panel panel-default">
              <div class="panel-heading">
              	<span class="lead">List of Requests</span>
              </div>
              
              <div>
		    	<div class="alert alert-success" ng-show="showSuccessAlert">
		        	<button type="button" class="close" data-ng-click="switchBool('showSuccessAlert')">×</button> 
		        	<strong>Done!</strong> {{textAlert}}
		        </div>
			</div>
			
			<div>
		    	<div class="alert alert-danger" ng-show="showErrorAlert">
		        	<button type="button" class="close" data-ng-click="switchBool('showErrorAlert')">×</button> 
		        	<strong>Error!</strong> {{textAlert}}
		        </div>
			</div>
              
              <div class="tablecontainer">
                  <table class="table table-hover">
                      <thead>
                          <tr>
                              <th>UUID</th>
                              <th>CreationTime</th>
                              <th>Status</th>
                              <th>Given Name</th>
                              <th>Family Name</th>
                              <th>Username</th>
                              <th>Email</th>
                              <th width="20%"></th>
                          </tr>
                      </thead>
                      <tbody>
                          <tr ng-repeat="r in ctrl.list">
                              <td><span ng-bind="r.uuid"></span></td>
                              <td><span ng-bind="r.creationTime | date:'dd/MM/yyyy HH:mm:ss'"></span></td>
                              <td><span ng-bind="r.status"></span></td>
                              <td><span ng-bind="r.givenname"></span></td>
                              <td><span ng-bind="r.familyname"></span></td>
                              <td><span ng-bind="r.username"></span></td>
                              <td><span ng-bind="r.email"></span></td> 
                              <td>
                              	<button type="button" ng-click="ctrl.approveRequest(r.uuid)" class="btn btn-success custom-width">Approve</button>  
                              	<button type="button" ng-click="ctrl.rejectRequest(r.uuid)" class="btn btn-danger custom-width">Reject</button>
                              </td>
                          </tr>
                      </tbody>
                  </table>
              </div>
          </div>
	</div>
	
	
	
	<script type="text/javascript" src="<c:url value='/webjars/angularjs/angular.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/angular-ui-bootstrap/ui-bootstrap.js'/>"></script>
	<script src="<c:url value='/resources/js/registration_app.js' />"></script>
	<script src="<c:url value='/resources/js/service/registration_service.js' />"></script>
	<script src="<c:url value='/resources/js/controller/registration_controller.js' />"></script>
</body>
</html>