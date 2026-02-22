package com.contact.service;

import com.contact.model.Person;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PersonService {

    Person create(Person person) throws SQLException;

    Optional<Person> findById(Integer id) throws SQLException;

    List<Person> findAll() throws SQLException;

    boolean update(Person person) throws SQLException;

    boolean delete(Integer id) throws SQLException;

    List<Person> searchByName(String searchTerm) throws SQLException;
}