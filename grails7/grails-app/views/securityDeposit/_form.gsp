<g:hasErrors bean="${securityDeposit}">
    <ul class="errors">
        <g:eachError bean="${securityDeposit}" var="error">
            <li><g:message error="${error}" /></li>
        </g:eachError>
    </ul>
</g:hasErrors>

<div class="field">
    <label for="tenancy">Tenancy</label>
    <g:select name="tenancy.id" from="${rentalapp.Tenancy.list()}" optionKey="id" value="${securityDeposit?.tenancy?.id}" required="" />
</div>
<div class="field">
    <label for="amount">Amount</label>
    <g:field type="number" name="amount" step="0.01" min="0.01" value="${securityDeposit?.amount}" required="" />
</div>
<div class="field">
    <label for="paidDate">Paid date</label>
    <g:datePicker name="paidDate" precision="day" value="${securityDeposit?.paidDate}" noSelection="['': '']" />
</div>
<div class="field">
    <label for="remarks">Remarks</label>
    <g:textField name="remarks" value="${securityDeposit?.remarks}" maxlength="100" />
</div>
