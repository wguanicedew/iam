<%@ taglib prefix="t" tagdir="/WEB-INF/tags/iam"%>
<t:page title="Sign Acceptable Usage Policy">
  <h2 class="text-center">Sign Acceptable Usage Policy</h2>
  <form id="sign-aup-form" class="sign-aup-form form" action="/iam/aup/sign" method="post">
    <p>In order to proceed, you need to sign the Acceptable Usage Policy (AUP) for this organization:</p>
    <div class="form-group">
      <div class="aup-text">${aup.text}</div>
    </div>
    <div class="form-group sign-aup-btns">
      <input id="sign-aup-btn" class="btn btn-success" type="submit" value="I agree with the terms of this AUP">
    </div>
  </form>
</t:page>
