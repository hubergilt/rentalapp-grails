<html>
<head><meta name="layout" content="main" /><title>Security Deposits</title></head>
<body>
<h1>Security Deposits <small>(${securityDepositCount ?: 0})</small></h1>

<g:link controller="securityDeposit" action="create" class="btn-primary">+ New Security Deposit</g:link>

<table class="list">
    <thead>
        <tr><th>Tenancy</th><th>Amount</th><th>Paid date</th><th></th></tr>
    </thead>
    <tbody>
        <g:each in="${securityDepositList}" var="securityDeposit">
            <tr>
                <td><g:link action="show" id="${securityDeposit.id}">${securityDeposit.tenancy}</g:link></td>
                <td><g:formatNumber number="${securityDeposit.amount}" minFractionDigits="2" maxFractionDigits="2" /></td>
                <td><g:formatDate date="${securityDeposit.paidDate}" format="yyyy-MM-dd" /></td>
                <td>
                    <g:link action="edit" id="${securityDeposit.id}">Edit</g:link>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>

<g:paginate total="${securityDepositCount ?: 0}" />
</body>
</html>
