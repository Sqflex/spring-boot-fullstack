package com.springPP.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);
        Mockito.when(resultSet.getInt("id")).thenReturn(1);
        Mockito.when(resultSet.getString("name")).thenReturn("John");
        Mockito.when(resultSet.getString("email")).thenReturn("123@b.by");
        Mockito.when(resultSet.getInt("age")).thenReturn(22);

        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        Customer expected = new Customer(
                1, "John", "123@b.by", 22
        );

        assertThat(actual).isEqualTo(expected);
    }
}