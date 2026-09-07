<g:hasErrors bean="${tenant}">
    <ul class="errors">
        <g:eachError bean="${tenant}" var="error">
            <li><g:message error="${error}" /></li>
        </g:eachError>
    </ul>
</g:hasErrors>

<div class="field">
    <label for="firstNames">First name(s)</label>
    <g:textField name="firstNames" value="${tenant?.firstNames}" required="" />
</div>
<div class="field">
    <label for="paternalSurname">Paternal surname</label>
    <g:textField name="paternalSurname" value="${tenant?.paternalSurname}" required="" />
</div>
<div class="field">
    <label for="maternalSurname">Maternal surname</label>
    <g:textField name="maternalSurname" value="${tenant?.maternalSurname}" required="" />
</div>
<div class="field">
    <label for="nationalId">National ID</label>
    <g:textField name="nationalId" value="${tenant?.nationalId}" required="" maxlength="8" />
</div>
