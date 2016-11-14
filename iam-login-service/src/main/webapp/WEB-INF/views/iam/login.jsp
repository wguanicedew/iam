<%@ taglib
  prefix="authz"
  uri="http://www.springframework.org/security/tags"%>
<%@ taglib
  prefix="c"
  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib
  prefix="o"
  tagdir="/WEB-INF/tags"%>
<%@ taglib
  prefix="spring"
  uri="http://www.springframework.org/tags"%>

<c:url
  var="loginUrl"
  value="/login" />

<o:iamHeader title="Log In" />

<body class="ng-cloak">
  
  <div class="row">

    <div class="absolute-center is-responsive">
        <div id="logo-container">
          <a href="/"></a>
        </div>

        <div id="login-error">
          <c:if test="${accessDeniedError != null}">
            <div class="alert alert-danger">Access denied: ${accessDeniedError}</div>
          </c:if>

          <c:if test="${ param.externalAuthenticationError != null }">
            <div class="alert alert-danger">
              <strong>External authentication error</strong>
              <div>${param.externalAuthenticationError}</div>
            </div>
          </c:if>
          
          <c:if test="${ param.error != null }">
            <div class="alert alert-danger">
              <spring:message code="login.error" />
            </div>
          </c:if>
          
          <c:if test="${resetPasswordMessage != null}">
            <div class="alert alert-${resetPasswordStyle}">
              ${resetPasswordMessage} 
            </div>
          </c:if>
        </div>

        <form
          id="login-form"
          action="${loginUrl}"
          method="post">

          <div class="form-group">
            <div class="input-group">
              <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span> <input
                class="form-control"
                type="text"
                placeholder="Username"
                autocorrect="off"
                autocapitalize="off"
                autocomplete="off"
                spellcheck="false"
                value="<c:out value="${ login_hint }" />"
                id="username"
                name="username">
            </div>
          </div>

          <div class="form-group">
            <div class="input-group">
              <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span> <input
                class="form-control"
                type="password"
                placeholder="Password"
                autocorrect="off"
                autocapitalize="off"
                autocomplete="off"
                spellcheck="false"
                id="password"
                name="password">
            </div>
          </div>

          <div>
            <input
              type="hidden"
              name="${_csrf.parameterName}"
              value="${_csrf.token}" /> <input
              type="submit"
              class="btn btn-primary btn-block"
              value="Login"
              name="submit"
              class="form-control">
          </div>
        </form>

        <div id="login-external-authn">
		          <c:if test="${loginPageConfiguration.googleEnabled}">
		            <div id="google-login" class="ext-authn-login-button">
		              <a
		                class='btn btn-block btn-social btn-google'
		                href="/openid_connect_login"> <i class="fa fa-google"></i> Sign in with Google
		              </a>
		            </div>
		          </c:if>

					<c:if test="${loginPageConfiguration.samlEnabled}">
						<div id="saml-login" ng-controller="idp-selection-modal-ctrl" class="ext-authn-login-button">
							<c:url var="samlLoginUrl" value="/saml/login" />
							<a class="btn btn-block btn-default" ng-click="open()"> Sign in with SAML</a>
						</div>
					</c:if>
				</div>
        
				<c:if test="${loginPageConfiguration.registrationEnabled}">
					<div id="login-registration">
					  <a class="btn btn-success btn-block" href="/register">Register a new account</a>
					</div>
          <div id="forgot-password" ng-controller="ForgotPasswordModalController">
            <a class="btn btn-link btn-block" ng-click="open()">Forgot your password?</a>
          </div>
				</c:if>	
    	</div>
  	</div>
  	
	<script type="text/javascript" src="<c:url value='/webjars/angularjs/angular.min.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/angularjs/angular-animate.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/webjars/angular-ui-bootstrap/ui-bootstrap-tpls.min.js'/>"></script>
	<script type="text/javascript" src="/resources/iam/js/iam-login-app.js"></script>

	<script type="text/javascript" src="/resources/iam/js/passwordreset.app.js"></script>
	<script type="text/javascript" src="/resources/iam/js/service/passwordreset.service.js"></script>
	<script type="text/javascript" src="/resources/iam/js/controller/passwordreset.controller.js"></script>
	
	<script type="text/javascript">
		angular.element(document).ready(function() {
			angular.bootstrap(document, ['iam-login-app', 'passwordResetApp']);
		});
	</script>
	
</body>
