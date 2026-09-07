<html>
<head><meta name="layout" content="main" /><title>Current Occupancy — Room ${currentTenancy?.name}</title></head>
<body>
<h1>Room ${currentTenancy?.name}</h1>
<p><em>Read-only view of <code>current_tenancies</code>.</em></p>
<dl>
    <dt>Floor</dt><dd>${currentTenancy?.floor}</dd>
    <dt>Status</dt><dd>${currentTenancy?.status}</dd>
    <dt>Tenant</dt><dd>${currentTenancy?.occupied ? currentTenancy.tenantFullName : '— vacant —'}</dd>
    <dt>Tenancy start date</dt><dd>${currentTenancy?.startDate ? g.formatDate(date: currentTenancy.startDate, format: 'yyyy-MM-dd') : '—'}</dd>
</dl>
<g:link controller="room" action="show" id="${currentTenancy?.roomId}">View room record</g:link>
&middot;
<g:link action="index">Back to list</g:link>
</body>
</html>
