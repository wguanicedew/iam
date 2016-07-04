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

<o:header title="Log In" />
<c:url
  var="loginUrl"
  value="/login" />

<div class="row-fluid">
  <div class="absolute-center is-responsive">
    <div class="col-sm-12 col-md-10 col-md-offset-1">

      <div id="logo-container"></div>
      <c:if test="${accessDeniedError != null}">
        <div class="alert alert-error">
            Access denied: ${accessDeniedError}
        </div>
      </c:if>
      
      <c:if test="${userNotFoundError != null}">
        <div class="alert alert-error">
          <strong>Unknown user</strong><br>
            ${externalAuthenticationToken.userInfo.name} is not a registered
            user in this organisation. Identity information:
            <ul>
              <li><strong>Account issuer</strong>: ${externalAuthenticationToken.issuer}</li>
              <li><strong>Account subject</strong>: ${externalAuthenticationToken.sub}</li>
              <li><strong>Email</strong>: ${externalAuthenticationToken.userInfo.email}</li>
            </ul>
        </div>    
      </c:if>
      
      <c:if test="${ param.externalAuthenticationError != null }">
        <div class="alert alert-error">
          <strong>External authentication error</strong>
          <div>${param.externalAuthenticationError}</div>
        </div>
      </c:if>
      
      <c:if test="${ param.error != null }">
        <div class="alert alert-error">
          <spring:message code="login.error" />
        </div>
      </c:if>
      
      <div class="well">
        <form
          id="login-form"
          action="${loginUrl}"
          method="post">
          <div>
            <div class="input-prepend input-block-level">
              <span class="add-on"><i class="icon-user"></i></span> <input
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
          <div>
            <div class="input-prepend input-block-level">
              <span class="add-on"><i class="icon-lock"></i></span> <input
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
              class="btn"
              value="Login"
              name="submit">
          </div>
        </form>
      </div>
      <div class="social-authn">
        <c:if test="${loginPageConfiguration.googleEnabled}">
          <a
            class='btn btn-google'
            href="/openid_connect_login"> <i class="fa fa-google"></i> |
            Sign in with Google
          </a>

        </c:if>

        <c:if test="${loginPageConfiguration.githubEnabled}">
          Github is enabled
        </c:if>
      </div>
    </div>
    <div>
      <c:if test="${loginPageConfiguration.samlEnabled}">
        <c:url
          var="samlLoginUrl"
          value="/saml/login" />
          
          Or <a href="${samlLoginUrl}">Login with your SAML Identity
          Provider</a>.
        
      </c:if>
      
      <div style="text-align: center; padding-top: 5mm;">
      	<a href="/registration/add">Register new account</a>
      </div>
      
    </div>
  </div>
</div>

<script
  type="text/javascript"
  src="resources/js/lib/jquery.min.js"></script>
<script
  type="text/javascript"
  src="resources/js/lib/jquery.cookie.js"></script>
<script
  type="text/javascript"
  src="resources/js/lib/jquery.query.js"></script>
<script
  type="text/javascript"
  src="resources/js/lib/purl.js"></script>
<script
  type="text/javascript"
  src="resources/bootstrap2/js/bootstrap.min.js"></script>
<script
  type="text/javascript"
  src="resources/js/lib/underscore-min.js"></script>