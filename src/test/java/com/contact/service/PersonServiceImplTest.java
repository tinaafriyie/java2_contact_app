package com.contact.service;

import com.contact.dao.PersonDAO;
import com.contact.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class PersonServiceImplTest {

    private FakePersonDAO fakeDao;
    private PersonService service;

    @BeforeEach
    void setUp() {
        fakeDao = new FakePersonDAO();
        service = new PersonServiceImpl(fakeDao);
    }

    @Test
    void create_shouldThrow_whenFirstnameMissing() {
        Person p = new Person();
        p.setLastname("Doe");
        p.setNickname("JD");

        assertThrows(IllegalArgumentException.class, () -> service.create(p));
    }

    @Test
    void create_shouldThrow_whenEmailInvalid() {
        Person p = basePerson("Doe", "John", "JD");
        p.setEmailAddress("not-an-email");

        assertThrows(IllegalArgumentException.class, () -> service.create(p));
    }

    @Test
    void create_shouldThrow_whenPhoneInvalid() {
        Person p = basePerson("Doe", "John", "JD");
        p.setPhoneNumber("abc###"); 

        assertThrows(IllegalArgumentException.class, () -> service.create(p));
    }

    @Test
    void create_shouldThrow_whenDuplicateFirstnameLastname() throws SQLException {
        Person p1 = basePerson("Doe", "John", "JD");
        service.create(p1);

        Person p2 = basePerson("Doe", "John", "Johnny");
        assertThrows(IllegalStateException.class, () -> service.create(p2));
    }

    @Test
    void update_shouldThrow_whenIdMissing() {
        Person p = basePerson("Doe", "John", "JD");
        p.setIdperson(null);

        assertThrows(IllegalArgumentException.class, () -> service.update(p));
    }

    @Test
    void delete_shouldThrow_whenIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> service.delete(0));
        assertThrows(IllegalArgumentException.class, () -> service.delete(-1));
        assertThrows(IllegalArgumentException.class, () -> service.delete(null));
    }

    @Test
    void searchByName_whenEmpty_returnsAll() throws SQLException {
        service.create(basePerson("Doe", "John", "JD"));
        service.create(basePerson("Smith", "Ana", "AS"));

        List<Person> result = service.searchByName("   ");
        assertEquals(2, result.size());
    }

    @Test
    void searchByName_delegatesToDao() throws SQLException {
        service.create(basePerson("Doe", "John", "JD"));
        service.create(basePerson("Smith", "Ana", "AS"));

        List<Person> result = service.searchByName("doe");
        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).getLastname());
    }

    
    private static Person basePerson(String last, String first, String nick) {
        Person p = new Person();
        p.setLastname(last);
        p.setFirstname(first);
        p.setNickname(nick);
        p.setAddress("Somewhere 123");
        p.setEmailAddress("test@example.com");
        p.setPhoneNumber("+32 123 456 789");
        p.setBirthDate(LocalDate.of(2000, 1, 1));
        return p;
    }

    
    private static class FakePersonDAO implements PersonDAO {

        private final AtomicInteger idGen = new AtomicInteger(1);
        private final Map<Integer, Person> store = new LinkedHashMap<>();

        @Override
        public Person createPerson(Person person) {
            int id = idGen.getAndIncrement();
            Person copy = copyOf(person);
            copy.setIdperson(id);
            store.put(id, copy);
            return copyOf(copy);
        }

        @Override
        public Optional<Person> findById(Integer id) {
            if (id == null) return Optional.empty();
            Person p = store.get(id);
            return Optional.ofNullable(p == null ? null : copyOf(p));
        }

        @Override
        public List<Person> findAll() {
            return store.values().stream().map(FakePersonDAO::copyOf).toList();
        }

        @Override
        public boolean updatePerson(Person person) {
            if (person == null || person.getIdperson() == null) return false;
            if (!store.containsKey(person.getIdperson())) return false;
            store.put(person.getIdperson(), copyOf(person));
            return true;
        }

        @Override
        public boolean deletePerson(Integer id) {
            if (id == null) return false;
            return store.remove(id) != null;
        }

        @Override
        public List<Person> searchPersonByName(String searchTerm) {
            String q = (searchTerm == null) ? "" : searchTerm.trim().toLowerCase();
            if (q.isEmpty()) return findAll();

            return store.values().stream()
                    .filter(p -> contains(p.getFirstname(), q)
                              || contains(p.getLastname(), q)
                              || contains(p.getNickname(), q))
                    .map(FakePersonDAO::copyOf)
                    .toList();
        }

        private static boolean contains(String s, String qLower) {
            return s != null && s.toLowerCase().contains(qLower);
        }

        private static Person copyOf(Person p) {
            Person c = new Person();
            c.setIdperson(p.getIdperson());
            c.setLastname(p.getLastname());
            c.setFirstname(p.getFirstname());
            c.setNickname(p.getNickname());
            c.setPhoneNumber(p.getPhoneNumber());
            c.setAddress(p.getAddress());
            c.setEmailAddress(p.getEmailAddress());
            c.setBirthDate(p.getBirthDate());
            return c;
        }
    }
}