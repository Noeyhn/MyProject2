package com.github.cupangclone.config;

import com.github.cupangclone.properties.DataSourceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DataSourceProperties.class)
@EnableJpaRepositories(
        basePackages = {"com.github.cupangclone.repository.roles", "com.github.cupangclone.repository.userPrincipal",
                        "com.github.cupangclone.repository.userPrincipalRoles", "com.github.cupangclone.repository.items"},
        entityManagerFactoryRef = "entityManagerFactoryBean1",
        transactionManagerRef = "tmJpa1",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {})
)
public class JpaConfig {

    private final DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource dataSource1() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        dataSource.setUsername(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());
        dataSource.setUrl(dataSourceProperties.getUrl());

        return dataSource;

    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean1(@Qualifier("dataSource1")DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.github.cupangclone.repository.roles", "com.github.cupangclone.repository.userPrincipal",
                "com.github.cupangclone.repository.userPrincipalRoles", "com.github.cupangclone.repository.items");

        JpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(adapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.use_sql_comments", "true");

        em.setJpaPropertyMap(properties);

        return em;

    }

    @Bean(name = "tmJpa1")
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource1")DataSource dataSource) {

        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean1(dataSource).getObject());

        return transactionManager;

    }

}
