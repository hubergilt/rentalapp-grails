<html>
<head><meta name="layout" content="main" /><title>Current Occupancy</title></head>
<body>
<h1>Current Occupancy <small>(${currentTenancyCount ?: 0})</small></h1>
<p><em>Read-only — reflects the <code>current_tenancies</code> database view (one row per room, including vacant ones). There is nothing to create, edit, or delete here.</em></p>

<g:form controller="currentTenancy" action="index" method="GET" class="filters">
    <div class="field">
        <label for="q">Search</label>
        <input type="text" id="q" name="q" value="${q}" placeholder="room name or tenant name" />
    </div>
    <button type="submit">Filter</button>
    <g:link controller="currentTenancy" action="index">Reset</g:link>
</g:form>

<table class="list">
    <thead><tr><th>Room</th><th>Floor</th><th>Status</th><th>Tenant</th><th>Since</th></tr></thead>
    <tbody>
        <g:each in="${currentTenancyList}" var="row">
            <tr>
                <td><g:link action="show" id="${row.roomId}">${row.name}</g:link></td>
                <td>${row.floor}</td>
                <td>${row.status}</td>
                <td>${row.occupied ? row.tenantFullName : '— vacant —'}</td>
                <td>${row.startDate ? g.formatDate(date: row.startDate, format: 'yyyy-MM-dd') : '—'}</td>
            </tr>
        </g:each>
    </tbody>
</table>

<g:paginate total="${currentTenancyCount ?: 0}" />
</body>
</html>
