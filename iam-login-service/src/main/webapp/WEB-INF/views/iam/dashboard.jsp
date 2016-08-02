<%@ taglib prefix="authz"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<o:iamHeader title="INDIGO IAM | User Dashboard" />

<body class="skin-blue" ng-app="ngRouteDashboard">
	<div class="wrapper" ng-controller="HomeController">

		<header class="main-header"> </header>

		<aside class="main-sidebar">
			<mainsidebar />
		</aside>

        <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper" ui-view>
        </div>
        <!-- /.content-wrapper -->
	</div>

	<footer class="main-footer">
		<div class="pull-right hidden-xs">
			<b>Version</b> 0.3.0
		</div>
		<strong> Copyright &copy; <a
			href="https://www.indigo-datacloud.eu/the_project">The INDIGO
				Project</a>.
		</strong> All rights reserved.
	</footer>

	<script type="text/javascript"
		src="<c:url value='/webjars/jquery/jquery.min.js'/>"></script>

	<script type="text/javascript"
		src="<c:url value='/webjars/angularjs/angular.min.js'/>"></script>
	
	<script type="text/javascript"
		src="<c:url value='/webjars/angularjs/angular-animate.js'/>"></script>

    <script type="text/javascript"
        src="<c:url value='/webjars/angular-ui-router/angular-ui-router.min.js'/>"></script>

	<script type="text/javascript"
		src="<c:url value='/webjars/angularjs/angular-resource.min.js'/>"></script>

	<script type="text/javascript"
		src="<c:url value='/webjars/angular-ui-bootstrap/ui-bootstrap-tpls.min.js'/>"></script>

	<script type="text/javascript" src="/resources/iam/js/adminLTE.js"></script>

	<script type="text/javascript" src="/resources/iam/js/dashboard-app.js"></script>
</body>