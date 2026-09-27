<g:hasErrors bean="${room}">
    <ul class="errors">
        <g:eachError bean="${room}" var="error">
            <li><g:message error="${error}" /></li>
        </g:eachError>
    </ul>
</g:hasErrors>

<div class="field">
    <label for="name">Name</label>
    <g:textField name="name" value="${room?.name}" required="" maxlength="45" />
</div>
<div class="field">
    <label for="floor">Floor</label>
    <g:textField name="floor" value="${room?.floor}" required="" maxlength="20" />
</div>
<div class="field">
    <label for="price">Price</label>
    <g:field type="number" name="price" step="0.01" min="0.01" value="${room?.price}" required="" />
</div>
<div class="field">
    <label for="status">Status</label>
    <g:select name="status" from="${['available', 'occupied', 'maintenance']}" value="${room?.status}" required="" />
</div>
