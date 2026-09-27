<html>
<head><meta name="layout" content="main" /><title>Rent Payments</title></head>
<body>
<h1>Rent Payments <small>(${rentPaymentCount ?: 0})</small></h1>

<g:link controller="rentPayment" action="create" class="btn-primary">+ New Rent Payment</g:link>

<table class="list">
    <thead>
        <tr><th>Tenant</th><th>Room</th><th>Period</th><th>Amount</th><th>Payment date</th><th></th></tr>
    </thead>
    <tbody>
        <g:each in="${rentPaymentList}" var="rentPayment">
            <tr>
                <td><g:link action="show" id="${rentPayment.id}">${rentPayment.tenant ?: '—'}</g:link></td>
                <td>${rentPayment.room ?: '—'}</td>
                <td><g:formatDate date="${rentPayment.period}" format="yyyy-MM-dd" /></td>
                <td><g:formatNumber number="${rentPayment.amount}" minFractionDigits="2" maxFractionDigits="2" /></td>
                <td><g:formatDate date="${rentPayment.paymentDate}" format="yyyy-MM-dd" /></td>
                <td>
                    <g:link action="edit" id="${rentPayment.id}">Edit</g:link>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>

<g:paginate total="${rentPaymentCount ?: 0}" />
</body>
</html>
