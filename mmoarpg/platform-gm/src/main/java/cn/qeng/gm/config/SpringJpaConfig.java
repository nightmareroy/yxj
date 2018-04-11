/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.dialect.MySQL5Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import cn.qeng.gm.core.Redis;
import cn.qeng.gm.core.RedisManager;

/**
 * Spring-data-JPA配置类.
 *
 * @author 小流氓(176543888@qq.com)
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:config.properties")
@EnableJpaRepositories(basePackages = "cn.qeng.gm")
class SpringJpaConfig {
	@Autowired
	private Environment env;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(env.getProperty("db.driverClass"));
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		dataSource.setJdbcUrl(env.getProperty("db.jdbcUrl"));
		dataSource.setUser(env.getProperty("db.user"));
		dataSource.setPassword(env.getProperty("db.password"));
		dataSource.setInitialPoolSize(Integer.valueOf(env.getProperty("db.initialPoolSize")));
		dataSource.setAcquireIncrement(Integer.valueOf(env.getProperty("db.acquireIncrement")));
		dataSource.setMinPoolSize(Integer.valueOf(env.getProperty("db.minPoolSize")));
		dataSource.setMaxPoolSize(Integer.valueOf(env.getProperty("db.maxPoolSize")));
		dataSource.setMaxIdleTime(Integer.valueOf(env.getProperty("db.maxIdleTime")));
		dataSource.setIdleConnectionTestPeriod(Integer.valueOf(env.getProperty("db.idleConnectionTestPeriod")));
		return dataSource;
	}

	@Bean
	public EntityManagerFactory entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setShowSql(true);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("cn.qeng.gm");
		factory.setDataSource(dataSource());
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory());
		return txManager;
	}

	@Bean
	public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", MySQL5Dialect.class.getName());

		HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
		factory.setEntityManagerFactory(emf);
		factory.setJpaProperties(properties);
		return factory;
	}

	@Bean(destroyMethod = "close")
	public RedisManager redisManager() {
		RedisManager redisManager = new RedisManager();
		{// 全局Redis
			String host = env.getProperty("global.redis.host", String.class);
			int port = env.getProperty("global.redis.port", int.class);
			int dbIndex = env.getProperty("global.redis.db", int.class);
			String password = env.getProperty("global.redis.password", String.class);
			redisManager.setGlobalRedis(new Redis(host, port, password, dbIndex));
		}

		{// 后台运维工具
			String host = env.getProperty("pig.redis.host", String.class);
			int port = env.getProperty("pig.redis.port", int.class);
			String password = env.getProperty("pig.redis.password", String.class);
			redisManager.setPigRedis(new Redis(host, port, password));
		}

		{// 消息Redis(聊天监控...)
			String host = env.getProperty("publish.redis.host", String.class);
			int port = env.getProperty("publish.redis.port", int.class);
			int dbIndex = env.getProperty("publish.redis.db", int.class);
			String password = env.getProperty("publish.redis.password", String.class);
			redisManager.setMsgRedis(new Redis(host, port, password, dbIndex));
		}
		return redisManager;
	}

	@Bean(destroyMethod = "close")
	public MongoClient mongoClient() {
		return new MongoClient(new MongoClientURI(env.getProperty("mongo.url", String.class)));
	}
}