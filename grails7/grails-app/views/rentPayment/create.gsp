<html>
<head><meta name="layout" content="main" /><title>New Rent Payment</title></head>
<body>
<h1>New Rent Payment</h1>
<g:form controller="rentPayment" action="save">
    <g:render template="form" model="[rentPayment: rentPayment]" />
    <button type="submit" class="btn-primary">Create</button>
    <g:link action="index">Cancel</g:link>
</g:form>
</body>
</html>
