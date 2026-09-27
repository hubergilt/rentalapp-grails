package rentalapp

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Explicitly registers /css/**, /js/**, /images/** as static resource
 * mappings onto src/main/resources/static/{css,js,images}. Spring Boot
 * normally does this automatically for anything under
 * src/main/resources/static — but Grails' own front controller/UrlMappings
 * can end up intercepting those paths first, producing a 404 ("No mapping
 * for GET /images/...") instead of falling through to the static handler.
 * Registering the mapping explicitly here guarantees it, regardless of
 * whatever ordering Grails' own dispatcher wiring uses.
 */
@Configuration
class WebConfig implements WebMvcConfigurer {

    void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler('/css/**')
                .addResourceLocations('classpath:/static/css/')
        registry.addResourceHandler('/js/**')
                .addResourceLocations('classpath:/static/js/')
        registry.addResourceHandler('/images/**')
                .addResourceLocations('classpath:/static/images/')
    }
}
