<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>

<o:iamHeader title="Registration request submitted" />

<body>

	<div class="absolute-center is-responsive">

		<div id="logo-container"></div>

		<h1 class="text-center">
			Request submitted succesfully
		</h1>
		
		<div id="register-confirm-message">
			<p>Your Indigo registration request has been submitted successfully.</p>
			<p>An email with a confirmation link is being sent to the email address 
				provided in the registration form.  Check your mail!
			</p>
		</div>

		<div id="register-confirm-back-btn" class="row text-center">
			<a class="btn btn-primary" href='/login'>Back to Login Page</a>
		</div>

	</div>

</body>
