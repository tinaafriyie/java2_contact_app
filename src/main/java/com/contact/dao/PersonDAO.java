package com.contact.dao;

import com.contact.model.Person;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PersonDAO {
    Person createPerson(Person person) throws SQLException;
    Optional<Person> findById(Integer id) throws SQLException;
    List<Person> findAll() throws SQLException;
    boolean updatePerson(Person person) throws SQLException;
    boolean deletePerson(Integer id) throws SQLException;
    List<Person> searchPersonByName(String searchTerm) throws SQLException;
}