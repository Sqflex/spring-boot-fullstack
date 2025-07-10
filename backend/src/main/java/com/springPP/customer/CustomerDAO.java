package com.springPP.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer customerId);
    void insertCustomer(Customer customer);
    void deleteCustomerById(Integer customerId);
    void updateCustomer(Customer updatedCustomer);
    boolean existsPersonWithEmail(String email);
    boolean existsCustomerWithId(Integer customerId);
}
