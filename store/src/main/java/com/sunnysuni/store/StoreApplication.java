package com.sunnysuni.store;

import com.sunnysuni.common.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(JpaConfig.class)
public class StoreApplication {
  public static void main(String[] args) {
    SpringApplication.run(StoreApplication.class, args);
  }
}
