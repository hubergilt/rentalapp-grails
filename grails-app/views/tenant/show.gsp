<html>
<head><meta name="layout" content="main" /><title>Tenant #${tenant?.id}</title></head>
<body>
<h1>${tenant?.firstNames} ${tenant?.paternalSurname} ${tenant?.maternalSurname}</h1>

<dl>
    <dt>National ID</dt><dd>${tenant?.nationalId}</dd>
    <dt>Created</dt><dd>${tenant?.createdAt}</dd>
    <dt>Updated</dt><dd>${tenant?.updatedAt}</dd>
</dl>

<h2>Tenancies</h2>
<table class="list">
    <thead><tr><th>Room</th><th>Start</th><th>End</th><th>Status</th></tr></thead>
    <tbody>
        <g:each in="${tenant?.tenancies}" var="t">
            <tr>
                <td><g:link controller="tenancy" action="show" id="${t.id}">${t.room?.name}</g:link></td>
                <td><g:formatDate date="${t.startDate}" format="yyyy-MM-dd" /></td>
                <td>${t.endDate ? g.formatDate(date: t.endDate, format: 'yyyy-MM-dd') : '—'}</td>
                <td>${t.isActive() ? 'Active' : 'Ended'}</td>
            </tr>
        </g:each>
    </tbody>
</table>

<div style="margin-top:1.5rem; display:flex; gap:0.75rem;">
    <g:link action="edit" id="${tenant?.id}" class="btn-primary">Edit</g:link>
    <g:form action="delete" id="${tenant?.id}" method="DELETE">
        <button type="submit" class="btn-danger" onclick="return confirm('Delete this tenant?')">Delete</button>
    </g:form>
    <g:link action="index">Back to list</g:link>
</div>
</body>
</html>
