package com.springPP.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDAO {
    //static db
    private static List<Customer> customers;

    static {
        customers = new ArrayList<>();

        Customer alex = new Customer(1, "Alex", "@gmail.com", 25);
        customers.add(alex);
        Customer anakin = new Customer(2, "Anakin", "123@gmail.com", 39);
        customers.add(anakin);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        customers.removeIf(c -> c.getId().equals(customerId));
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        customers.add(updatedCustomer);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers.stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public boolean existsCustomerWithId(Integer customerId) {
        return customers.stream()
                .anyMatch(c -> c.getId().equals(customerId));
    }
}
