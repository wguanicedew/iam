<%@ taglib
  prefix="c"
  uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib
  prefix="o"
  tagdir="/WEB-INF/tags"%>
<%@ taglib
  prefix="spring"
  uri="http://www.springframework.org/tags"%>

<o:header title="Log In with SAML IdP" />
<o:topbar />
<div class="container-fluid main">
  <h1>
    <spring:message code="login.login_with_saml" />
  </h1>
  <c:if test="${ param.error != null }">
    <div class="alert alert-error">
      <spring:message code="login.error" />
    </div>
  </c:if>

  <div class="row-fluid">
    <div class="span6 offset1 well">
      <form
        action="${idpDiscoReturnURL}"
        method="get">

        <c:forEach
          var="idp"
          items="${idps}">
          <div>
            <c:if test="${idp.imageUrl != null}">
              <img alt="${idp.organizationName}" src="${idp.imageUrl }"/>
            </c:if>
            <input
              type="radio"
              id="'idp_' + ${idp.entityId}"
              name="${idpDiscoReturnParam}"
              value="${idp.entityId}" />
              <label for="'idp_' + ${idp}">${idp.organizationName}</label>            
          </div>
        </c:forEach>
        <input type="submit" value="Go!"/>
      </form>
    </div>
  </div>
</div>

<o:footer />