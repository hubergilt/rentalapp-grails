<html>
<head><meta name="layout" content="main" /><title>New Tenancy</title></head>
<body>
<h1>New Tenancy</h1>
<g:form controller="tenancy" action="save">
    <g:render template="form" model="[tenancy: tenancy, rooms: rooms, tenants: tenants]" />
    <button type="submit" class="btn-primary">Create</button>
    <g:link action="index">Cancel</g:link>
</g:form>
</body>
</html>
