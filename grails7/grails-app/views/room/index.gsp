<html>
<head><meta name="layout" content="main" /><title>Rooms</title></head>
<body>
<h1>Rooms <small>(${roomCount ?: 0})</small></h1>

<g:link controller="room" action="create" class="btn-primary">+ New Room</g:link>

<table class="list">
    <thead>
        <tr><th>Name</th><th>Floor</th><th>Price</th><th>Status</th><th></th></tr>
    </thead>
    <tbody>
        <g:each in="${roomList}" var="room">
            <tr>
                <td><g:link action="show" id="${room.id}">${room.name}</g:link></td>
                <td>${room.floor}</td>
                <td><g:formatNumber number="${room.price}" minFractionDigits="2" maxFractionDigits="2" /></td>
                <td>${room.status}</td>
                <td>
                    <g:link action="edit" id="${room.id}">Edit</g:link>
                </td>
            </tr>
        </g:each>
    </tbody>
</table>

<g:paginate total="${roomCount ?: 0}" />
</body>
</html>
