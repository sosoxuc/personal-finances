package personal.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import personal.security.SecurityInterceptor;

import javax.sql.DataSource;
import java.util.Properties;

import static personal.spring.ConfigUtil.getConfig;

@Configuration
@Profile("dev")
@EnableWebMvc
@EnableCaching
@EnableTransactionManagement
@ComponentScan(basePackages = "personal")
public class SpringDevConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureContentNegotiation(
            ContentNegotiationConfigurer configurer) {

        configurer.favorPathExtension(Boolean.TRUE)
                .ignoreAcceptHeader(Boolean.TRUE).useJaf(Boolean.FALSE)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }

    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        super.configureDefaultServletHandling(configurer);
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(this.restDataSource());
        em.setPackagesToScan(new String[] { "personal" });
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.ejb.naming_strategy",
                "org.hibernate.cfg.ImprovedNamingStrategy");
        jpaProperties.put("hibernate.id.new_generator_mappings", "true");
        em.setJpaProperties(jpaProperties);
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter() {
            {
                String dialect = getConfig("spring.jpa.database-platform");
                this.setDatabasePlatform(dialect);

                String showSql = getConfig("spring.jpa.show-sql");
                if (showSql != null) {
                    this.setShowSql(showSql.equals("true"));
                }

                String generateDdl = getConfig("spring.jpa.hibernate.ddl-auto");
                if (showSql != null) {
                    this.setGenerateDdl(generateDdl.equals("update"));
                }
            }
        };

        em.setJpaVendorAdapter(vendorAdapter);
        return em;
    }

    @Bean
    public DataSource restDataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        String driver = getConfig("spring.datasource.driverClassName");
        dataSource.setDriverClassName(driver);

        String url = getConfig("spring.datasource.url");
        dataSource.setUrl(url);

        String username = getConfig("spring.datasource.username");
        dataSource.setUsername(username);

        String password = getConfig("spring.datasource.password");
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                this.entityManagerFactoryBean().getObject());

        return transactionManager;
    }
}
