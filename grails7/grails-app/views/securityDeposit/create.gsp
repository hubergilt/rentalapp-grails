<html>
<head><meta name="layout" content="main" /><title>New Security Deposit</title></head>
<body>
<h1>New Security Deposit</h1>
<g:form controller="securityDeposit" action="save">
    <g:render template="form" model="[securityDeposit: securityDeposit]" />
    <button type="submit" class="btn-primary">Create</button>
    <g:link action="index">Cancel</g:link>
</g:form>
</body>
</html>
