<html>
<head><meta name="layout" content="main" /><title>Tenants</title></head>
<body>
<h1>Tenants <small>(${tenantCount ?: 0})</small></h1>

<g:form controller="tenant" action="index" method="GET" class="filters">
    <div class="field">
        <label for="q">Search</label>
        <input type="text" id="q" name="q" value="${q}" placeholder="name or national id" />
    </div>
    <button type="submit">Filter</button>
    <g:link controller="tenant" action="index" class="btn-primary">Reset</g:link>
    <span style="flex:1"></span>
    <g:link controller="tenant" action="create" class="btn-primary">+ New Tenant</g:link>
</g:form>

<table class="list">
    <thead>
        <tr><th>Paternal surname</th><th>Maternal surname</th><th>First name(s)</th><th>National ID</th><th></th></tr>
    </thead>
    <tbody>
        <g:each in="${tenantList}" var="tenant">
            <tr>
                <td><g:link action="show" id="${tenant.id}">${tenant.paternalSurname}</g:link></td>
                <td>${tenant.maternalSurname}</td>
                <td>${tenant.firstNames}</td>
                <td>${tenant.nationalId}</td>
                <td>
                    <g:link action="edit" id="${tenant.id}">Edit</g:link>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>

<g:paginate total="${tenantCount ?: 0}" />
</body>
</html>
