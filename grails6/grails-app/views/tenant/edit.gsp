<html>
<head><meta name="layout" content="main" /><title>Edit Tenant</title></head>
<body>
<h1>Edit Tenant</h1>
<g:form controller="tenant" action="update" id="${tenant?.id}" method="PUT">
    <g:render template="form" model="[tenant: tenant]" />
    <button type="submit" class="btn-primary">Save</button>
    <g:link action="show" id="${tenant?.id}">Cancel</g:link>
</g:form>
</body>
</html>
