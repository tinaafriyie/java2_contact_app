
package test.java.com.contact;

import main.java.com.contact.model.Person;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

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
    void testCreate() throws SQLException {
        testPerson = new Person("TestLast", "TestFirst", "Tester");
        testPerson.setPhoneNumber("555-1234");
        testPerson.setEmailAddress("test@example.com");
        testPerson.setBirthDate(LocalDate.of(1995, 5, 15));
        
        Person created = personDAO.create(testPerson);
        assertNotNull(created.getIdperson());
        System.out.println("✅ Test 1 passed");
    }

    @Test
    @Order(2)
    void testFindById() throws SQLException {
        Optional<Person> found = personDAO.findById(testPerson.getIdperson());
        assertTrue(found.isPresent());
        assertEquals("TestLast", found.get().getLastname());
        System.out.println("✅ Test 2 passed");
    }

    @Test
    @Order(3)
    void testFindAll() throws SQLException {
        List<Person> persons = personDAO.findAll();
        assertTrue(persons.size() > 0);
        System.out.println("✅ Test 3 passed - Found " + persons.size() + " persons");
    }

    @Test
    @Order(4)
    void testUpdate() throws SQLException {
        testPerson.setPhoneNumber("555-9999");
        boolean updated = personDAO.update(testPerson);
        assertTrue(updated);
        System.out.println("✅ Test 4 passed");
    }

    @Test
    @Order(5)
    void testSearch() throws SQLException {
        List<Person> results = personDAO.searchByName("Test");
        assertTrue(results.size() > 0);
        System.out.println("✅ Test 5 passed");
    }

    @Test
    @Order(6)
    void testDelete() throws SQLException {
        boolean deleted = personDAO.delete(testPerson.getIdperson());
        assertTrue(deleted);
        System.out.println("✅ Test 6 passed");
    }
}