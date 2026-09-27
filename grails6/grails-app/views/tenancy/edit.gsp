<html>
<head><meta name="layout" content="main" /><title>Edit Tenancy</title></head>
<body>
<h1>Edit Tenancy #${tenancy?.id}</h1>
<g:form controller="tenancy" action="update" id="${tenancy?.id}" method="PUT">
    <g:render template="form" model="[tenancy: tenancy, rooms: rooms, tenants: tenants]" />
    <button type="submit" class="btn-primary">Save</button>
    <g:link action="show" id="${tenancy?.id}">Cancel</g:link>
</g:form>
</body>
</html>
