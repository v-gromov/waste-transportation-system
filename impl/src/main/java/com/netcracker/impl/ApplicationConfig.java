package com.netcracker.impl;

import com.netcracker.api.Database;
import com.netcracker.api.Geo;
import com.netcracker.api.PointService;
import com.netcracker.impl.database.DatabaseImpl;
import com.netcracker.impl.geo.GeoImpl;
import com.netcracker.impl.services.point.PointServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


@Configuration
public class ApplicationConfig {

    @PostConstruct
    public void testMethod(){
        System.out.println("ApplicationConfig postconstruct");
    }

    @Bean(name = "geo")
    Geo getGeo(){
        return new GeoImpl();
    }

    @Bean(name = "pointService")
    PointService getPointService(){
        return new PointServiceImpl();
    }

    @Bean(name = "database")
    Database getDatabase(){
        return new DatabaseImpl();
    }
}