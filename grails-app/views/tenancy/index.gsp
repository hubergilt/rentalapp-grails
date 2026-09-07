<html>
<head><meta name="layout" content="main" /><title>Tenancies</title></head>
<body>
<h1>Tenancies <small>(${tenancyCount ?: 0})</small></h1>

<g:form controller="tenancy" action="index" method="GET" class="filters">
    <div class="field">
        <label for="status">Status</label>
        <g:select name="status" from="${['active', 'ended']}" value="${status}" noSelection="['': 'All']" />
    </div>
    <div class="field">
        <label for="roomId">Room</label>
        <g:select name="roomId" from="${rooms}" optionKey="id" optionValue="name" value="${roomId}" noSelection="['': 'All']" />
    </div>
    <div class="field">
        <label for="tenantId">Tenant</label>
        <g:select name="tenantId" from="${tenants}" optionKey="id" optionValue="selectLabel" value="${tenantId}" noSelection="['': 'All']" />
    </div>
    <button type="submit">Filter</button>
    <g:link controller="tenancy" action="index" class="btn-primary">Reset</g:link>
    <span style="flex:1"></span>
    <g:link controller="tenancy" action="create" class="btn-primary">+ New Tenancy</g:link>
</g:form>

<table class="list">
    <thead><tr><th>Tenant</th><th>Room</th><th>Start</th><th>End</th><th>Status</th><th>Deposit paid</th></tr></thead>
    <tbody>
        <g:each in="${tenancyList}" var="tenancy">
            <tr>
                <td><g:link action="show" id="${tenancy.id}">${tenancy.tenant}</g:link></td>
                <td>${tenancy.room?.name}</td>
                <td><g:formatDate date="${tenancy.startDate}" format="yyyy-MM-dd" /></td>
                <td>${tenancy.endDate ? g.formatDate(date: tenancy.endDate, format: 'yyyy-MM-dd') : '—'}</td>
                <td>${tenancy.isActive() ? 'Active' : 'Ended'}</td>
                <td>${tenancy.totalDepositPaid}</td>
            </tr>
        </g:each>
    </tbody>
</table>

<g:paginate total="${tenancyCount ?: 0}" />
</body>
</html>
