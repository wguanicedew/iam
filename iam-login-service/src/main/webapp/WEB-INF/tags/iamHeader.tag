<%@attribute name="title" required="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>

<base href="${config.issuer}">

<meta charset="utf-8">
<title>${config.topbarTitle}-${title}</title>
<meta
  name="viewport"
  content="width=device-width, initial-scale=1.0">

<link
  rel="stylesheet"
  href="<c:url value='/webjars/bootstrap/css/bootstrap.min.css'/>"></link>
  
<link
  rel="stylesheet"
  href="resources/iam/css/iam.css"></link>
    
<link
  href="resources/font-awesome/css/font-awesome.css"
  rel="stylesheet">
<link
  href="resources/bootstrap-social/bootstrap-social.css"
  rel="stylesheet">
</head>


  