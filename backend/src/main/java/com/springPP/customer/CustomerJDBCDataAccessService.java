package com.springPP.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDAO {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var query = "SELECT id, name, email, age FROM customer";
        return jdbcTemplate.query(query, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        String query = "SELECT id, name, email, age FROM customer WHERE id = ?";
        return jdbcTemplate.query(query, customerRowMapper, customerId)
                .stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var query = """
                insert into customer(name, email, age)
                VALUES(?, ?, ?);
                """;
        var result = jdbcTemplate.update(query, customer.getName(), customer.getEmail(), customer.getAge());
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        String query = "DELETE FROM customer WHERE id = ?";
        int result = this.jdbcTemplate.update(query, Long.valueOf(customerId));
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        String query = "update customer set name = ?, email = ?, age = ? where id = ?";
        jdbcTemplate.update(query, updatedCustomer.getName(), updatedCustomer.getEmail(), updatedCustomer.getAge(), updatedCustomer.getId());
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        String query = "SELECT count(id) FROM customer WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, email);
        return count != 0 && count > 0;
    }

    @Override
    public boolean existsCustomerWithId(Integer customerId) {
        String query = "SELECT count(id) FROM customer WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, customerId);
        return count != null && count > 0;
    }
}
