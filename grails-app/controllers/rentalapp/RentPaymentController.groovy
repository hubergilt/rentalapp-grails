package rentalapp

/**
 * `rent_payments` is an append-only ledger (see rentaldb README) — the
 * generated scaffolding's create/edit screens are used as-is for recording
 * new/adjusted entries, with the list screen's built-in column sorting used
 * as the "filter by tenant / room / period" mechanism (click a column
 * header, or append e.g. ?tenant.id=3 to the URL).
 */
class RentPaymentController {
    static scaffold = RentPayment
}
