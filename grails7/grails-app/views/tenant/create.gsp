<html>
<head><meta name="layout" content="main" /><title>New Tenant</title></head>
<body>
<h1>New Tenant</h1>
<g:form controller="tenant" action="save">
    <g:render template="form" model="[tenant: tenant]" />
    <button type="submit" class="btn-primary">Create</button>
    <g:link action="index">Cancel</g:link>
</g:form>
</body>
</html>
