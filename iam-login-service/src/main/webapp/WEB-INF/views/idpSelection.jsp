<%@ taglib
  prefix="c"
  uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>IDP selection</title>
<base href="${config.issuer}">
<link
  rel="stylesheet"
  href="<c:url value='/webjars/bootstrap/css/bootstrap.min.css'/>"></link>
</head>

<body
  ng-app="app"
  class="ng-cloak">

  <script
    type="text/ng-template"
    id="idpTemplate.html">
  <a>
      <img ng-show="match.model.imageUrl" ng-src="{{match.model.imageUrl}}" width="16">
      <span ng-bind-html="match.model.organizationName | uibTypeaheadHighlight:query"></span>
       (<span>{{match.model.entityId}}</span>) 
  </a>
  </script>

  <div class="container-fluid" ng-controller="idp-selection">
 
  <pre>Model: {{idpSelected | json}}</pre>
  
  <input
    type="text"
    ng-model="idpSelected"
    placeholder="Search for supported IDP"
    uib-typeahead="idpDesc as idpDesc.organizationName for idpDesc in lookupIdp($viewValue)"
    typeahead-loading="loadingIdps"
    typeahead-no-results="noResults"
    typeahead-min-length="2"
    typeahead-template-url="idpTemplate.html"
    class="form-control">

  <i
    ng-show="loadingIdps"
    class="glyphicon glyphicon-refresh"></i>
  <div ng-show="noResults">
    <i class="glyphicon glyphicon-remove"></i> No Results Found
  </div>
  

  </div>
  <script
    type="text/javascript"
    src="<c:url value='/webjars/angularjs/angular.min.js'/>"></script>


  <script
    type="text/javascript"
    src="<c:url value='/webjars/angular-ui-bootstrap/1.3.3/ui-bootstrap-tpls.min.js'/>"></script>

  <script type="text/javascript">
			angular
			  .module('app',['ui.bootstrap'])
        .controller(
					'idp-selection', function($scope, $http) { 
            
						  $scope.lookupIdp = function(val){

                if (val.length == 1) return {};
                
                var result = $http.get('/saml/idps', {params: {q: val}})
                .then(function(response){
                    return response.data;
                 }); 
                return result; 
						  }
					});
                 
		</script>

</body>