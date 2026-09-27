<html>
<head><meta name="layout" content="main" /><title>Security Deposit #${securityDeposit?.id}</title></head>
<body>
<h1>Security Deposit #${securityDeposit?.id}</h1>

<dl>
    <dt>Tenancy</dt><dd><g:link controller="tenancy" action="show" id="${securityDeposit?.tenancy?.id}">${securityDeposit?.tenancy}</g:link></dd>
    <dt>Amount</dt><dd><g:formatNumber number="${securityDeposit?.amount}" minFractionDigits="2" maxFractionDigits="2" /></dd>
    <dt>Paid date</dt><dd><g:formatDate date="${securityDeposit?.paidDate}" format="yyyy-MM-dd" /></dd>
    <dt>Remarks</dt><dd>${securityDeposit?.remarks}</dd>
    <dt>Created</dt><dd>${securityDeposit?.createdAt}</dd>
    <dt>Updated</dt><dd>${securityDeposit?.updatedAt}</dd>
</dl>

<div style="margin-top:1.5rem; display:flex; gap:0.75rem;">
    <g:link action="edit" id="${securityDeposit?.id}" class="btn-primary">Edit</g:link>
    <g:form action="delete" id="${securityDeposit?.id}" method="DELETE">
        <button type="submit" class="btn-danger" onclick="return confirm('Delete this security deposit?')">Delete</button>
    </g:form>
    <g:link action="index">Back to list</g:link>
</div>
</body>
</html>
