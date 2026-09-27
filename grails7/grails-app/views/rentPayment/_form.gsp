<g:hasErrors bean="${rentPayment}">
    <ul class="errors">
        <g:eachError bean="${rentPayment}" var="error">
            <li><g:message error="${error}" /></li>
        </g:eachError>
    </ul>
</g:hasErrors>

<div class="field">
    <label for="tenant">Tenant</label>
    <g:select name="tenant.id" from="${rentalapp.Tenant.list()}" optionKey="id" value="${rentPayment?.tenant?.id}" noSelection="['': '- none -']" />
</div>
<div class="field">
    <label for="room">Room</label>
    <g:select name="room.id" from="${rentalapp.Room.list()}" optionKey="id" value="${rentPayment?.room?.id}" noSelection="['': '- none -']" />
</div>
<div class="field">
    <label for="period">Period</label>
    <g:datePicker name="period" precision="day" value="${rentPayment?.period}" noSelection="['': '']" />
</div>
<div class="field">
    <label for="amount">Amount</label>
    <g:field type="number" name="amount" step="0.01" min="0.01" value="${rentPayment?.amount}" required="" />
</div>
<div class="field">
    <label for="paymentDate">Payment date</label>
    <g:datePicker name="paymentDate" precision="day" value="${rentPayment?.paymentDate}" noSelection="['': '']" />
</div>
<div class="field">
    <label for="remarks">Remarks</label>
    <g:textField name="remarks" value="${rentPayment?.remarks}" required="" maxlength="100" />
</div>
