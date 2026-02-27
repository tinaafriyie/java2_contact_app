package com.contact.dao;

import com.contact.model.Person;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonDAOTest {
    private static PersonDAO personDAO;
    private static Person testPerson;

    @BeforeAll
    static void setUp() {
        personDAO = new PersonDAOImpl();
        System.out.println("🧪 Starting tests...");
    }

    @Test
    @Order(1)
    @DisplayName("Should create person with all fields")
    void testCreate() throws SQLException {
        testPerson = new Person("TestLast", "TestFirst", "Tester");
        testPerson.setPhoneNumber("555-1234");
        testPerson.setEmailAddress("test@example.com");
        testPerson.setBirthDate(LocalDate.of(1995, 5, 15));
        
        Person created = personDAO.createPerson(testPerson);
        
        assertThat(created.getIdperson()).isNotNull();
        assertThat(created.getLastname()).isEqualTo("TestLast");
        System.out.println("✅ Test 1 passed");
    }

    @Test
    @Order(2)
    @DisplayName("Should find person by ID")
    void testFindById() throws SQLException {
        Optional<Person> found = personDAO.findById(testPerson.getIdperson());
        
        assertThat(found).isPresent();
        assertThat(found.get().getLastname()).isEqualTo("TestLast");
        System.out.println("✅ Test 2 passed");
    }

    @Test
    @Order(3)
    @DisplayName("Should find all persons")
    void testFindAll() throws SQLException {
        List<Person> persons = personDAO.findAll();
        
        assertThat(persons).isNotEmpty();
        System.out.println("✅ Test 3 passed - Found " + persons.size() + " persons");
    }

    @Test
    @Order(4)
    @DisplayName("Should update person")
    void testUpdate() throws SQLException {
        testPerson.setPhoneNumber("555-9999");
        boolean updated = personDAO.updatePerson(testPerson);
        
        assertThat(updated).isTrue();
        
        Optional<Person> updatedPerson = personDAO.findById(testPerson.getIdperson());
        assertThat(updatedPerson).isPresent();
        assertThat(updatedPerson.get().getPhoneNumber()).isEqualTo("555-9999");
        System.out.println("✅ Test 4 passed");
    }

    @Test
    @Order(5)
    @DisplayName("Should search persons by name")
    void testSearch() throws SQLException {
        List<Person> results = personDAO.searchPersonByName("Test");
        
        assertThat(results).isNotEmpty();
        assertThat(results)
            .extracting(Person::getLastname)
            .contains("TestLast");
        System.out.println("✅ Test 5 passed");
    }

    @Test
    @Order(6)
    @DisplayName("Should delete person")
    void testDelete() throws SQLException {
        boolean deleted = personDAO.deletePerson(testPerson.getIdperson());
        
        assertThat(deleted).isTrue();
        
        Optional<Person> deletedPerson = personDAO.findById(testPerson.getIdperson());
        assertThat(deletedPerson).isEmpty();
        System.out.println("✅ Test 6 passed");
    }
}