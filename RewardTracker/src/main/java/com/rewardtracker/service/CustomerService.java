package com.rewardtracker.service;

import com.rewardtracker.model.Customer;
import com.rewardtracker.repository.CustomerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepo customerRepo;

    public String createCustomer(Customer customer) {
        Customer existingCustomer = customerRepo.findByEmail(customer.getEmail());
        if (existingCustomer != null) {
            logger.warn("Customer with email {} already exists", customer.getEmail());
            return "Customer with email " + customer.getEmail() + " already exists.";
        }
        customerRepo.save(customer);
        logger.info("Customer created successfully with email {}", customer.getEmail());
        return "Customer created successfully!";
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        Optional<Customer> customer = customerRepo.findById(id);
        if (customer.isPresent()) {
            logger.info("Customer found with ID {}", id);
        } else {
            logger.warn("Customer with ID {} not found", id);
        }
        return customer;
    }


    public Optional<Customer> updateCustomer(Long id, Customer customerDetails) {
        return customerRepo.findById(id).map(customer -> {
            customer.setName(customerDetails.getName());
            customer.setEmail(customerDetails.getEmail());
            customer.setPassword(customerDetails.getPassword());
            return customerRepo.save(customer);
        });
    }

    public boolean deleteCustomer(Long id) {
        return customerRepo.findById(id).map(customer -> {
            // Remove related customer transactions first
            customer.getSpentDetails().clear();  // ✅ Clears the list in JPA
            customerRepo.save(customer);  // ✅ Update to reflect the change

            customerRepo.delete(customer);  // ✅ Now delete the customer
            return true;
        }).orElse(false);
    }

}
