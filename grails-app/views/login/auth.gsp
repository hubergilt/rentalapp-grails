<html>
<head>
    <meta name="layout" content="main" />
    <title>Sign in</title>
</head>
<body>
<div class="login-panel">
    <div class="login-brand">
        <img src="/images/rentalapp-logo.svg" alt="" class="login-logo" />
        <h1>${grailsApplication.config.getProperty('rentalapp.brand.appName')}</h1>
        <p>${grailsApplication.config.getProperty('rentalapp.brand.tagline')}</p>
    </div>

    <g:if test="${flash.message}">
        <div class="flash-message" role="alert">${flash.message}</div>
    </g:if>

    <g:form controller="login" action="authenticate" class="login-form">
        <div class="field">
            <label for="username">Username</label>
            <input type="text" name="username" id="username" autofocus="autofocus" required="required" />
        </div>
        <div class="field">
            <label for="password">Password</label>
            <input type="password" name="password" id="password" required="required" />
        </div>
        <button type="submit" class="btn-primary">Sign in</button>
    </g:form>
</div>
</body>
</html>
