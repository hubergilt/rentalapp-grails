import rentalapp.WebConfig

// Explicit bean registration for WebConfig (the /css, /js, /images static
// resource handler mappings). grails-app/conf/spring/resources.groovy is
// always loaded by Grails as part of building the Spring context, unlike
// plain @Configuration classes under src/main/groovy, whose pickup via
// component-scan proved unreliable here. Spring only needs the bean to
// exist with type WebMvcConfigurer for its DelegatingWebMvcConfiguration to
// pick it up automatically — how it got registered doesn't matter.
beans = {
    webConfig(WebConfig)
}
