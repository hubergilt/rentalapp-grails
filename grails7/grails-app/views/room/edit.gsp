<html>
<head><meta name="layout" content="main" /><title>Edit Room</title></head>
<body>
<h1>Edit Room</h1>
<g:form controller="room" action="update" id="${room?.id}" method="PUT">
    <g:render template="form" model="[room: room]" />
    <button type="submit" class="btn-primary">Save</button>
    <g:link action="show" id="${room?.id}">Cancel</g:link>
</g:form>
</body>
</html>
