<g:hasErrors bean="${tenancy}">
    <ul class="errors">
        <g:eachError bean="${tenancy}" var="error">
            <li><g:message error="${error}" /></li>
        </g:eachError>
    </ul>
</g:hasErrors>

<div class="field">
    <label for="tenant">Tenant</label>
    <g:select name="tenant.id" from="${tenants}" optionKey="id" optionValue="selectLabel"
              value="${tenancy?.tenant?.id}" noSelection="['': '-- choose a tenant --']" required="" />
</div>
<div class="field">
    <label for="room">Room</label>
    <g:select name="room.id" from="${rooms}" optionKey="id" optionValue="name"
              value="${tenancy?.room?.id}" noSelection="['': '-- choose a room --']" required="" />
</div>
<div class="field">
    <label for="startDate">Start date</label>
    <g:field type="date" name="startDate" value="${tenancy?.startDate ? g.formatDate(date: tenancy.startDate, format: 'yyyy-MM-dd') : ''}" required="" />
</div>
<div class="field">
    <label for="endDate">End date <small>(leave blank while active)</small></label>
    <g:field type="date" name="endDate" value="${tenancy?.endDate ? g.formatDate(date: tenancy.endDate, format: 'yyyy-MM-dd') : ''}" />
</div>
<div class="field">
    <label for="depositRefundDate">Deposit refund date</label>
    <g:field type="date" name="depositRefundDate" value="${tenancy?.depositRefundDate ? g.formatDate(date: tenancy.depositRefundDate, format: 'yyyy-MM-dd') : ''}" />
</div>
<div class="field">
    <label for="depositRefundAmount">Deposit refund amount</label>
    <g:field type="number" step="0.01" name="depositRefundAmount" value="${tenancy?.depositRefundAmount}" />
</div>
