<html>
<head><meta name="layout" content="main" /><title>Edit Rent Payment</title></head>
<body>
<h1>Edit Rent Payment</h1>
<g:form controller="rentPayment" action="update" id="${rentPayment?.id}" method="PUT">
    <g:render template="form" model="[rentPayment: rentPayment]" />
    <button type="submit" class="btn-primary">Save</button>
    <g:link action="show" id="${rentPayment?.id}">Cancel</g:link>
</g:form>
</body>
</html>
