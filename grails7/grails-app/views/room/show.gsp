<html>
<head><meta name="layout" content="main" /><title>Room #${room?.id}</title></head>
<body>
<h1>${room?.name}</h1>

<dl>
    <dt>Floor</dt><dd>${room?.floor}</dd>
    <dt>Price</dt><dd><g:formatNumber number="${room?.price}" minFractionDigits="2" maxFractionDigits="2" /></dd>
    <dt>Status</dt><dd>${room?.status}</dd>
    <dt>Created</dt><dd>${room?.createdAt}</dd>
    <dt>Updated</dt><dd>${room?.updatedAt}</dd>
</dl>

<h2>Tenancies</h2>
<table class="list">
    <thead><tr><th>Tenant</th><th>Start</th><th>End</th></tr></thead>
    <tbody>
        <g:each in="${room?.tenancies}" var="t">
            <tr>
                <td><g:link controller="tenancy" action="show" id="${t.id}">${t.tenant}</g:link></td>
                <td><g:formatDate date="${t.startDate}" format="yyyy-MM-dd" /></td>
                <td>${t.endDate ? g.formatDate(date: t.endDate, format: 'yyyy-MM-dd') : '—'}</td>
            </tr>
        </g:each>
    </tbody>
</table>

<div style="margin-top:1.5rem; display:flex; gap:0.75rem;">
    <g:link action="edit" id="${room?.id}" class="btn-primary">Edit</g:link>
    <g:form action="delete" id="${room?.id}" method="DELETE">
        <button type="submit" class="btn-danger" onclick="return confirm('Delete this room?')">Delete</button>
    </g:form>
    <g:link action="index">Back to list</g:link>
</div>
</body>
</html>
