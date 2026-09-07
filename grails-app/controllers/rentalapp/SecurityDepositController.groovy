package rentalapp

/**
 * Standalone list/show/create/edit/delete for security deposit installments,
 * for when you want to browse/audit the whole ledger directly rather than
 * through a specific tenancy. Day-to-day editing of installments happens
 * inline on the Tenancy show/edit screen instead — see TenancyController's
 * addDeposit/removeDeposit actions and tenancy/_deposits.gsp.
 */
class SecurityDepositController {
    static scaffold = SecurityDeposit
}
