<%@ taglib prefix="t" tagdir="/WEB-INF/tags/iam"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<t:page title="An unexpected error occurred">
  <h2 class="text-center text-danger">${errorMessage }</h2>
  
  <c:if test="${exceptionMessage != null}">
    <div class="text-center" id="exception-message">
      <p>${exceptionMessage}</p>
    </div>
    <h4 class="text-center">Stack trace</h4>
    <textarea style="width: 100%;" rows="10" id="exception-stack-trace" readonly="readonly">
      ${exceptionStackTrace}
    </textarea>
  </c:if>
  
  <div id="register-confirm-back-btn" class="row text-center">
    <a class="btn btn-primary" href='/login'>Back to Login Page</a>
  </div>
</t:page>