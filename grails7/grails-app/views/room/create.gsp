<html>
<head><meta name="layout" content="main" /><title>New Room</title></head>
<body>
<h1>New Room</h1>
<g:form controller="room" action="save">
    <g:render template="form" model="[room: room]" />
    <button type="submit" class="btn-primary">Create</button>
    <g:link action="index">Cancel</g:link>
</g:form>
</body>
</html>
