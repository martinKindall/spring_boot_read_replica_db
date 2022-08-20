package com.codigo_morsa.read_replica.config

import com.codigo_morsa.read_replica.data.TransactionType
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.hibernate.jpa.HibernatePersistenceProvider
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.orm.hibernate5.HibernateTransactionManager
import org.springframework.orm.hibernate5.LocalSessionFactoryBean
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaDialect
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.*
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy
open class DatabaseConfig {

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)
    }

    @Bean
    fun primaryDataSourceConfig(): HikariConfig {
        return HikariDataSource(datasourceConfig("jdbc:mysql://localhost:3306/testdb"))
    }

    fun datasource(): DataSource {
        return HikariDataSource(primaryDataSourceConfig())
    }

    fun readOnlyDataSource(databaseUrl: String): DataSource {
        val config = datasourceConfig(databaseUrl)
        config.isReadOnly = true
        return HikariDataSource(config)
    }

    fun datasourceConfig(databaseUrl: String): HikariConfig {
        val config = HikariConfig()

        config.jdbcUrl = databaseUrl
        config.driverClassName = "com.mysql.cj.jdbc.Driver"
        config.username = "root"
        config.password = "pass1234"

        config.minimumIdle = 1
        config.maximumPoolSize = 10
        config.connectionTimeout = 30000

        return config
    }

    @Bean
    @Primary
    fun actualDataSource(): TransactionRoutingDataSource {
        val routingDataSource = TransactionRoutingDataSource()
        val dataSourceMap: MutableMap<Any, Any> = HashMap()
        dataSourceMap[TransactionType.WRITE] = datasource()
        dataSourceMap[TransactionType.READ] = datasource()
        routingDataSource.setTargetDataSources(dataSourceMap)
        return routingDataSource
    }

    @Bean
    @Primary
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.persistenceUnitName = javaClass.simpleName
        entityManagerFactoryBean.persistenceProvider = HibernatePersistenceProvider()
        entityManagerFactoryBean.dataSource = actualDataSource()
        entityManagerFactoryBean.setPackagesToScan("com.codigo_morsa.read_replica.*")
        val vendorAdapter = HibernateJpaVendorAdapter()
        val jpaDialect: HibernateJpaDialect = vendorAdapter.jpaDialect
        jpaDialect.setPrepareConnection(true)   // https://stackoverflow.com/a/68078228/6398014
        entityManagerFactoryBean.jpaVendorAdapter = vendorAdapter
        entityManagerFactoryBean.setJpaProperties(additionalProperties())
        return entityManagerFactoryBean
    }

    protected fun additionalProperties(): Properties {
        val properties = Properties()
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
        properties.setProperty("hibernate.id.new_generator_mappings", "true")
        properties.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy")
        properties.setProperty("hibernate.implicit_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy")
        return properties
    }

    @Bean
    fun sessionFactory(): LocalSessionFactoryBean {
        val sessionFactory = LocalSessionFactoryBean()
        sessionFactory.setDataSource(actualDataSource())
        sessionFactory.setPackagesToScan("com.codigo_morsa.read_replica.*")
        sessionFactory.hibernateProperties = additionalProperties()
        return sessionFactory
    }

    @Bean
    fun transactionManager(): HibernateTransactionManager {
        val transactionManager = HibernateTransactionManager()
        transactionManager.sessionFactory = sessionFactory().`object`
        return transactionManager
    }

    @Bean
    fun transactionTemplate(): TransactionTemplate {
        return TransactionTemplate(transactionManager())
    }

    class TransactionRoutingDataSource: AbstractRoutingDataSource() {

        override fun determineCurrentLookupKey(): Any {
            return if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                logger.info("its a read")
                TransactionType.READ
            }
            else {
                logger.info("its a write")
                TransactionType.WRITE
            }
        }
    }
}