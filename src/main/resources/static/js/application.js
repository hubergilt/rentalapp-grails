
// Inline security-deposit installments on the Tenancy show screen.
// Submits are done via fetch() against TenancyController#addDeposit /
// #removeDeposit, which render just the `_deposits` partial back, so the
// panel updates without a full page reload.
document.addEventListener('DOMContentLoaded', function () {
    var panel = document.getElementById('deposits-panel');
    if (!panel) return;

    function refreshWith(html) {
        panel.outerHTML = html;
        bind();
    }

    function bind() {
        var panel = document.getElementById('deposits-panel');
        if (!panel) return;

        var form = panel.querySelector('#add-deposit-form');
        if (form) {
            form.addEventListener('submit', function (e) {
                e.preventDefault();
                var data = new FormData(form);
                fetch(form.action, { method: 'POST', body: data })
                    .then(function (r) { return r.text(); })
                    .then(refreshWith)
                    .catch(function () { alert('Could not add installment.'); });
            });
        }

        panel.querySelectorAll('.remove-deposit').forEach(function (btn) {
            btn.addEventListener('click', function () {
                if (!confirm('Remove this installment?')) return;
                fetch(btn.dataset.url, { method: 'DELETE' })
                    .then(function (r) { return r.text(); })
                    .then(refreshWith)
                    .catch(function () { alert('Could not remove installment.'); });
            });
        });
    }

    bind();
});
