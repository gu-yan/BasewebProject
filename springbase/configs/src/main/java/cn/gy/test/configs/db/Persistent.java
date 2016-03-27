package cn.gy.test.configs.db;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cn.gy.test.configs.constants.ConstantBasic;
import cn.gy.test.configs.fileutil.ConfigFileUtils;
import cn.gy.test.configs.thread.DaemonThreadFactory;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

@Configuration
@EnableTransactionManagement
public class Persistent {

	@Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator(){
        return new HibernateExceptionTranslator();
    }
	
	/********************************basic db********************************************/
	@Bean
	@Qualifier(value = "basictx")
	public PlatformTransactionManager transactionManager() {
		EntityManagerFactory factory = entityManagerFactory().getObject();
		return new JpaTransactionManager(factory);
	}
	
	@Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityFactory = new LocalContainerEntityManagerFactoryBean();
        entityFactory.setDataSource(dataSource());
        
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(Boolean.valueOf(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "hibernate.generateddl")));
        jpaVendorAdapter.setShowSql(Boolean.valueOf(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "hibernate.showsql")));
        
        entityFactory.setJpaVendorAdapter(jpaVendorAdapter);
        entityFactory.setPackagesToScan(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "scandomainpackage.basic"));

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "hibernate.dialect"));
        jpaProperties.put("hibernate.format_sql", ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "hibernate.formatsql"));
        jpaProperties.put("hibernate.hbm2ddl.auto", ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "hibernate.hbm2ddl.auto"));
        entityFactory.setJpaProperties(jpaProperties);
        
        entityFactory.afterPropertiesSet();
        entityFactory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        return entityFactory;
     }
	
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "datasource.driveclass"));
        dataSource.setUrl(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "datasource.url"));
        dataSource.setUsername(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "datasource.username"));
        dataSource.setPassword(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "datasource.password"));
        return dataSource;
    }
    
    @Bean
	public JdbcTemplate jdbcTemplate(){
		JdbcTemplate jt=new JdbcTemplate();
		jt.setDataSource(dataSource());
		return jt;
	}

    /**
     * thread pool 
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolforJms(){
    	ThreadPoolExecutor threadPoolforJms =  new ThreadPoolExecutor(
    			Integer.valueOf(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "thread.pool.corePoolSize")), 
    			Integer.valueOf(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "thread.pool.maximumPoolSize")), 
    			Long.valueOf(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "thread.pool.keepAliveTime")), 
    			TimeUnit.MINUTES, 
    			new LinkedBlockingQueue<Runnable>(Integer.valueOf(ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "thread.pool.workQueue"))), 
    			new DaemonThreadFactory(""), 
    			new CallerRunsPolicy());
    	
    	return threadPoolforJms;
    }
}
