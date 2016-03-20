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
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cn.gy.test.configs.constants.ConstantObj;
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
        jpaVendorAdapter.setGenerateDdl(Boolean.valueOf(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "hibernate.generateddl")));
        jpaVendorAdapter.setShowSql(Boolean.valueOf(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "hibernate.show_sql")));
        
        entityFactory.setJpaVendorAdapter(jpaVendorAdapter);
        entityFactory.setPackagesToScan(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "scandomainpackage_basic"));

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "hibernate.dialect"));
        jpaProperties.put("hibernate.format_sql", ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "hibernate.format_sql"));
//        jpaProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        entityFactory.setJpaProperties(jpaProperties);
        
        entityFactory.afterPropertiesSet();
        entityFactory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        return entityFactory;
     }
	
    @Bean
    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
//        dataSource.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:xe");
//        dataSource.setUsername("onstar");
//        dataSource.setPassword("onstar");
//        return dataSource;
    	JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
    	dsLookup.setResourceRef(true);
    	return dsLookup.getDataSource(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "jndiname_basic"));
    }
    
    @Bean
	public JdbcTemplate jdbcTemplate(){
		JdbcTemplate jt=new JdbcTemplate();
		jt.setDataSource(dataSource());
		return jt;
	}

	
	/********************************realtime db********************************************/
	
	@Bean
	@Qualifier(value = "realtimetx")
	public PlatformTransactionManager transactionManager_realtime() {
		EntityManagerFactory factory = entityManagerFactory_realtime().getObject();
		return new JpaTransactionManager(factory);
	}
	
	@Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory_realtime() {
        LocalContainerEntityManagerFactoryBean entityFactory = new LocalContainerEntityManagerFactoryBean();
        entityFactory.setDataSource(dataSource_realtime());
        
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(Boolean.valueOf(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "hibernate.generateddl")));
        jpaVendorAdapter.setShowSql(Boolean.valueOf(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "hibernate.show_sql")));
        
        entityFactory.setJpaVendorAdapter(jpaVendorAdapter);
        entityFactory.setPackagesToScan(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "scandomainpackage_realtime"));

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.dialect", ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "hibernate.dialect"));
        jpaProperties.put("hibernate.format_sql", ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "hibernate.format_sql"));
//        jpaProperties.put("hibernate.hbm2ddl.auto", ConfigFileUtils.getPropertyValue(ConstantStr.CONFIG_FILE,"hibernate.hbm2ddl.auto"));
        entityFactory.setJpaProperties(jpaProperties);
        
        entityFactory.afterPropertiesSet();
        entityFactory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        return entityFactory;
     }
	
    @Bean
    public DataSource dataSource_realtime() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
//        dataSource.setUrl("jdbc:oracle:thin:@127.0.0.1:1521:xe");
//        dataSource.setUsername("onstar1");
//        dataSource.setPassword("onstar1");
//        return dataSource;
    	JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
    	dsLookup.setResourceRef(true);
    	return dsLookup.getDataSource(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "jndiname_realtime"));
    }
    
    @Bean
	public JdbcTemplate jdbcTemplate_realtime(){
		JdbcTemplate jt=new JdbcTemplate();
		jt.setDataSource(dataSource_realtime());
		return jt;
	}
    
    /**
     * thread pool 
     * @return
     * @Author YanGu@shanghaionstar.com
     * 2015年10月14日
     */
    @Bean
    public ThreadPoolExecutor threadPoolforJms(){
    	ThreadPoolExecutor threadPoolforJms =  new ThreadPoolExecutor(
    			Integer.valueOf(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "thread.pool.corePoolSize")), 
    			Integer.valueOf(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "thread.pool.maximumPoolSize")), 
    			Long.valueOf(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "thread.pool.keepAliveTime")), 
    			TimeUnit.MINUTES, 
    			new LinkedBlockingQueue<Runnable>(Integer.valueOf(ConfigFileUtils.getPropertyValue(ConstantObj.CONFIG_FILE[0], "thread.pool.workQueue"))), 
    			new DaemonThreadFactory(""), 
    			new CallerRunsPolicy());
    	
    	return threadPoolforJms;
    }
}
