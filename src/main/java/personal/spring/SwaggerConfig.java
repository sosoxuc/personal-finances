package personal.spring;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSwagger
public class SwaggerConfig {

    @Autowired
    private SpringSwaggerConfig springSwaggerConfig;

    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo()).includePatterns("/.*")
                .apiVersion("1.0 BETA").swaggerGroup("api")
                .pathProvider(new SwaggerPathProvider() {
                    @Override
                    protected String applicationPath() {
                        return "/personal-finances/rest";
                    }

                    @Override
                    protected String getDocumentationPath() {
                        return "/";
                    }
                });
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo("Personal FINANCES API",
                "RestAPI for Personal FINANCES", null, "", null, null);
        return apiInfo;
    }
}