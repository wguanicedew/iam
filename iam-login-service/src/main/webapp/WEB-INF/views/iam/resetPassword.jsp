<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>

<o:iamHeader title="IAM Reset Password" />

<body ng-app="passwordResetApp">

	<div class="absolute-center is-responsive">
		<div class="col-sm-12 col-md-10 col-md-offset-1" style="width: 500px">

			<div id="logo-container"></div>

			<c:choose>
				<c:when test="${message != null}">
					<div class="alert alert-danger">Error: ${message}</div>
				</c:when>
				
				<c:otherwise>
					<form id="changePasswdForm" action="/iam/password-reset" method="post" ng-controller="ResetPasswordController as ctrl">
						<input type="hidden" name="reset-key" id="reset-key" value="${resetKey}">
						<div class="form-group">
							<div class="input-group">
								<span class="input-group-addon">
									<i class="glyphicon glyphicon-lock"></i>
								</span>
								<input class="form-control" type="password" placeholder="Password" 
									   autocorrect="off" autocapitalize="off" autocomplete="off" 
									   spellcheck="false" id="password" name="password" ng-model=ctrl.password>
							</div>
							<div class="input-group">
								<span class="input-group-addon">
									<i class="glyphicon glyphicon-lock"></i>
								</span>
								<input class="form-control" type="password" placeholder="Repeat Password" 
									   autocorrect="off" autocapitalize="off" autocomplete="off" 
									   spellcheck="false" id="password-repeat" name="password-repeat" ng-model=ctrl.password-repeat>
							</div>
						</div>
		
						<div>
							<input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" /> <input type="submit"
								class="btn btn-primary btn-block" value="Save" name="submit"
								class="form-control">
						</div>
					</form>
				</c:otherwise>
			</c:choose>

			<div style="width: 200px; margin: auto;">
				<a class="btn btn-primary btn-block" style="align: center"
					href='/login'>Back to Login Page</a>
			</div>

		</div>
	</div>

</body>
