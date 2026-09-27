<html>
<head><meta name="layout" content="main" /><title>Rent Payment #${rentPayment?.id}</title></head>
<body>
<h1>Rent Payment #${rentPayment?.id}</h1>

<dl>
    <dt>Tenant</dt><dd>${rentPayment?.tenant ?: '—'}</dd>
    <dt>Room</dt><dd>${rentPayment?.room ?: '—'}</dd>
    <dt>Period</dt><dd><g:formatDate date="${rentPayment?.period}" format="yyyy-MM-dd" /></dd>
    <dt>Amount</dt><dd><g:formatNumber number="${rentPayment?.amount}" minFractionDigits="2" maxFractionDigits="2" /></dd>
    <dt>Payment date</dt><dd><g:formatDate date="${rentPayment?.paymentDate}" format="yyyy-MM-dd" /></dd>
    <dt>Remarks</dt><dd>${rentPayment?.remarks}</dd>
    <dt>Created</dt><dd>${rentPayment?.createdAt}</dd>
    <dt>Updated</dt><dd>${rentPayment?.updatedAt}</dd>
</dl>

<div style="margin-top:1.5rem; display:flex; gap:0.75rem;">
    <g:link action="edit" id="${rentPayment?.id}" class="btn-primary">Edit</g:link>
    <g:form action="delete" id="${rentPayment?.id}" method="DELETE">
        <button type="submit" class="btn-danger" onclick="return confirm('Delete this rent payment?')">Delete</button>
    </g:form>
    <g:link action="index">Back to list</g:link>
</div>
</body>
</html>
