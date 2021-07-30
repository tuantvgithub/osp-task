package com.example.springrabbitmqproducer;

import com.example.springrabbitmqproducer.model.Company;
import com.example.springrabbitmqproducer.model.Product;
import com.example.springrabbitmqproducer.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SpringRabbitmqProducerApplication implements CommandLineRunner {

	@Autowired
	private Producer producer;

	@Override
	public void run(String... args) throws Exception {
		Product iphone = new Product("Iphone 12");
		Product mac = new Product("Macbook");
		List<Product> products = new ArrayList<Product>(Arrays.asList(iphone, mac));
		Company apple = new Company("Apple", products);

		iphone.setCompany(apple);
		mac.setCompany(apple);

		this.producer.sendCompany(apple);
		this.producer.sendProduct(mac);

	}

	public static void main(String[] args) {
		SpringApplication.run(SpringRabbitmqProducerApplication.class, args);
	}
}
