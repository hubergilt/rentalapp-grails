<div id="deposits-panel">
    <h2>Security deposit installments</h2>

    <g:each in="${deposits}" var="deposit">
        <div class="deposit-row">
            <span><g:formatDate date="${deposit.paidDate}" format="yyyy-MM-dd" /> — ${deposit.amount}</span>
            <button type="button" class="remove-deposit btn-danger"
                    data-url="${g.createLink(controller: 'tenancy', action: 'removeDeposit', params: [tenancyId: tenancy.id, depositId: deposit.id])}">
                Remove
            </button>
        </div>
    </g:each>

    <g:if test="${!deposits}">
        <p><em>No installments recorded yet.</em></p>
    </g:if>

    <p class="deposit-total">Total paid: ${tenancy.totalDepositPaid}</p>

    <form id="add-deposit-form" action="${g.createLink(controller: 'tenancy', action: 'addDeposit')}" method="post">
        <input type="hidden" name="tenancyId" value="${tenancy.id}" />
        <div class="filters">
            <div class="field">
                <label for="amount">Amount</label>
                <input type="number" step="0.01" min="0.01" name="amount" required="required" />
            </div>
            <div class="field">
                <label for="paidDate">Paid on</label>
                <input type="date" name="paidDate" required="required" />
            </div>
            <button type="submit" class="btn-primary">Add installment</button>
        </div>
    </form>
</div>
