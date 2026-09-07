<html>
<head><meta name="layout" content="main" /><title>Tenancy #${tenancy?.id}</title></head>
<body>
<h1>Tenancy #${tenancy?.id}</h1>

<dl>
    <dt>Tenant</dt><dd><g:link controller="tenant" action="show" id="${tenancy?.tenant?.id}">${tenancy?.tenant}</g:link></dd>
    <dt>Room</dt><dd>${tenancy?.room?.name}</dd>
    <dt>Start date</dt><dd><g:formatDate date="${tenancy?.startDate}" format="yyyy-MM-dd" /></dd>
    <dt>End date</dt><dd>${tenancy?.endDate ? g.formatDate(date: tenancy.endDate, format: 'yyyy-MM-dd') : 'Still active'}</dd>
    <dt>Deposit refund date</dt><dd>${tenancy?.depositRefundDate ?: '—'}</dd>
    <dt>Deposit refund amount</dt><dd>${tenancy?.depositRefundAmount ?: '—'}</dd>
</dl>

<g:render template="deposits" model="[tenancy: tenancy, deposits: deposits]" />

<div style="margin-top:1.5rem; display:flex; gap:0.75rem;">
    <g:link action="edit" id="${tenancy?.id}" class="btn-primary">Edit</g:link>
    <g:form action="delete" id="${tenancy?.id}" method="DELETE">
        <button type="submit" class="btn-danger" onclick="return confirm('Delete this tenancy?')">Delete</button>
    </g:form>
    <g:link action="index">Back to list</g:link>
</div>
</body>
</html>
