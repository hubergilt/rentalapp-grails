<html>
<head><meta name="layout" content="main" /><title>Edit Security Deposit</title></head>
<body>
<h1>Edit Security Deposit</h1>
<g:form controller="securityDeposit" action="update" id="${securityDeposit?.id}" method="PUT">
    <g:render template="form" model="[securityDeposit: securityDeposit]" />
    <button type="submit" class="btn-primary">Save</button>
    <g:link action="show" id="${securityDeposit?.id}">Cancel</g:link>
</g:form>
</body>
</html>
