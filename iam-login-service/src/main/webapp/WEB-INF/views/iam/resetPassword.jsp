<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>

<o:iamHeader title="IAM Reset Password" />

<body ng-app="passwordResetApp" class="ng-cloak">

	<div class="absolute-center is-responsive">
		<div>

			<div id="logo-container"></div>

			<c:choose>				
				<c:when test="${errorMessage != null}">
					<div id="reset-password-error">
						<div class="alert alert-danger">Error: ${errorMessage}</div>
					</div>
				</c:when>
				
				<c:otherwise>
					<div ng-controller="ResetPasswordController as ctrl">
						<h1 class="text-center" 
							ng-show="ctrl.operationResult != 'ok'">
							Set your account password
						</h1>
						<h1 class="text-center" 
							ng-show="ctrl.operationResult == 'ok'">
							Your password has been reset succesfully! 
						</h1>


    		        <div ng-show="ctrl.operationResult != null">  
							<div class="alert alert-danger alert-dismissable" ng-show="ctrl.operationResult == 'err'">
								{{ctrl.textAlert}}
							</div>
              
							<div class="row text-center" ng-show="ctrl.operationResult == 'ok'">
								<a class="btn btn-primary" href='/login'>Back to Login Page</a>
							</div>
						</div>
						
						<form 
							class="reset-password-form"
							id="changePasswdForm" name="changePasswdForm" 
							ng-submit="ctrl.submit()" ng-show="ctrl.operationResult != 'ok'">
						
							<input type="hidden" 
								name="resetkey" 
								value="${resetKey}" 
								id="resetkey" 
								ng-init="ctrl.resetKey='${resetKey}'">
						
							<div class="form-group">
								<label class="control-label" for="password">Password</label>
								
								<div ng-class="{'has-error': changePasswdForm.password.$dirty && changePasswdForm.password.$invalid, 'has-success': changePasswdForm.password.$dirty && !changePasswdForm.password.$invalid}">
									<input class="form-control" type="password" placeholder="Password" 
									   autocorrect="off" autocapitalize="off" autocomplete="off" 
									   spellcheck="false" id="password" name="password" ng-model="ctrl.password"
									   required ng-minlength="6">
									<span class="help-block" ng-show="changePasswdForm.password.$dirty && changePasswdForm.password.$error.required">Please provide a password</span> 
									<span class="help-block" ng-show="changePasswdForm.password.$dirty && changePasswdForm.password.$error.minlength">The password must be at least 6 characters long</span> 
								</div>
							</div>
						
							<div class="form-group">
								<label class="control-label" for="passwordrepeat">Confirm password</label>
								<div ng-class="{'has-error': changePasswdForm.passwordrepeat.$dirty && changePasswdForm.passwordrepeat.$invalid, 'has-success': changePasswdForm.passwordrepeat.$dirty && !changePasswdForm.passwordrepeat.$invalid}">
									<input class="form-control" type="password" placeholder="Confirm password" 
										   autocorrect="off" autocapitalize="off" autocomplete="off" 
										   spellcheck="false" id="passwordrepeat" name="passwordrepeat" ng-model="ctrl.passwordrepeat"
										   required iam-password-check="ctrl.password">
									<span class="help-block" ng-show="changePasswdForm.passwordrepeat.$dirty && changePasswdForm.passwordrepeat.$error.required">Please confirm your password</span> 
									<span class="help-block" ng-show="changePasswdForm.passwordrepeat.$dirty && changePasswdForm.passwordrepeat.$error.iamPasswordCheck"> Passwords do not match</span> 
								</div>
							</div>
		
							<div class="form-group">
								<div>
									<input type="submit" class="btn btn-primary" value="Save" name="submit" ng-disabled="changePasswdForm.$invalid">
								</div>
							</div>
						</form>
					</div>
					
				</c:otherwise>
			</c:choose>

		</div>
	</div>
	
	<script type="text/javascript" src="<c:url value='/webjars/angularjs/angular.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/angularjs/angular-animate.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/angular-ui-bootstrap/ui-bootstrap-tpls.min.js'/>"></script>
	
	<script type="text/javascript" src="/resources/iam/js/passwordreset.app.js"></script>
	<script type="text/javascript" src="/resources/iam/js/service/passwordreset.service.js"></script>
	<script type="text/javascript" src="/resources/iam/js/controller/passwordreset.controller.js"></script>
	<script type="text/javascript" src="/resources/iam/js/directive/passwordreset.directive.js"></script>

</body>
