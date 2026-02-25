package com.sunnysuni.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EntityScan(basePackages = "com.sunnysuni.common.entity")
@EnableJpaRepositories(basePackages = "com.sunnysuni.common.repository")
public class JpaConfig {
}
