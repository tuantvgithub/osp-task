package com.example.springrabbitmqconsumer.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@id",
        scope = Product.class
)
public class Product implements Serializable {

    @JsonProperty
    private String name;

    @JsonProperty
    private Company company;

    public Product(){
    }

    public Product(String name){
        this.name = name;
    }

    public Product(String name, Company company){
        this.name = name;
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompany(Company company){
        this.company = company;
    }

    public Company getCompany(){
        return this.company;
    }
}
