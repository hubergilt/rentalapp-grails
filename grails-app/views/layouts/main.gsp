<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title><g:layoutTitle default="${grailsApplication.config.getProperty('rentalapp.brand.appName')}" /></title>
    <link rel="icon" href="/images/rentalapp-logo.svg" type="image/svg+xml" />
    <link rel="stylesheet" href="/css/application.css" />
    <g:layoutHead />
</head>
<body class="theme-${grailsApplication.config.getProperty('rentalapp.brand.theme') ?: 'slate'}">
    <g:if test="${session.rentalappUser}">
        <header class="app-header">
            <div class="brand">
                <img src="/images/rentalapp-logo.svg" alt="" class="brand-logo" />
                <div>
                    <span class="brand-name">${grailsApplication.config.getProperty('rentalapp.brand.appName')}</span>
                    <span class="brand-tagline">${grailsApplication.config.getProperty('rentalapp.brand.tagline')}</span>
                </div>
            </div>
            <nav class="app-nav">
                <g:link controller="tenant" action="index">Tenants</g:link>
                <g:link controller="room" action="index">Rooms</g:link>
                <g:link controller="tenancy" action="index">Tenancies</g:link>
                <g:link controller="rentPayment" action="index">Rent Payments</g:link>
                <g:link controller="securityDeposit" action="index">Deposits</g:link>
                <g:link controller="currentTenancy" action="index">Current Occupancy</g:link>
            </nav>
            <div class="app-user">
                <span>${session.rentalappUser}</span>
                <g:link controller="login" action="logout" class="logout-link">Log out</g:link>
            </div>
        </header>
    </g:if>

    <main class="app-content">
        <g:if test="${flash.message}">
            <div class="flash-message" role="alert">${flash.message}</div>
        </g:if>
        <g:layoutBody />
    </main>

    <footer class="app-footer">
        <span>${grailsApplication.config.getProperty('rentalapp.brand.appName')} &middot; data lives in <code>rentaldb</code>, schema managed by Flyway</span>
    </footer>

    <script src="/js/application.js"></script>
</body>
</html>
