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

<o:iamHeader title="Register" />

<script type="text/javascript" src="<c:url value='/webjars/angularjs/angular.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/angularjs/angular-animate.js'/>"></script>
<script type="text/javascript" src="<c:url value='/webjars/angular-ui-bootstrap/ui-bootstrap-tpls.min.js'/>"></script>

<body ng-app="registrationApp">

<div ng-include src="'resources/iam/apps/registration/registration.html'">
</div>

<script type="text/javascript" src="/resources/iam/apps/registration/registration.app.js"></script>
<script type="text/javascript" src="/resources/iam/apps/registration/registration.controller.js"></script>
<script type="text/javascript" src="/resources/iam/apps/registration/registration.directive.js"></script>
<script type="text/javascript" src="/resources/iam/apps/registration/registration.service.js"></script>
<script type="text/javascript" src="/resources/iam/apps/registration/authn-info.service.js"></script>
</body>