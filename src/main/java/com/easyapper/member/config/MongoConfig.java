package com.easyapper.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

	@Bean
	MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
		return new MongoTransactionManager(dbFactory);
	}
	
	@Override
	public MongoClient mongoClient() {
		return new MongoClient("127.0.0.1", 27017);
	}

	@Override
	protected String getDatabaseName() {
		return "easy-apper-chat-app";
	}
	
}
