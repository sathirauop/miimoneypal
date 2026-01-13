package com.sathira.miimoneypal;

import org.springframework.boot.SpringApplication;

public class TestMiiMoneyPalApplication {

    public static void main(String[] args) {
        SpringApplication.from(MiiMoneyPalApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
