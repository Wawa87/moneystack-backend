package com.wawa87.moneystack.service.system.user.dao;

import com.wawa87.moneystack.user.dao.UserDAOImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;

public class UserDAOImplTest {
    private DataSource dataSource;
    private UserDAOImpl userDAO;

    @BeforeEach
    public void prepareConnection() {
        this.userDAO = new UserDAOImpl(this.dataSource);
    }

    @AfterEach
    public void endConnection() {
    }

//    @Test
//    public void testSave() {
//        // Create the test User.
//        User user = new User();
//        user.setUsername("testuser");
//
//        ArrayList<String> emails = new ArrayList<>();
//        emails.add("testuser@email1.com");
//        emails.add("testuser@email2.com");
//        emails.add("testuser@email3.com");
//        user.setEmails(emails);
//
//        user.setFirstName("Test");
//        user.setLastName("User");
//        user.setPhoneNumber("+16195554321");
//
//        // Insert the test User and confirm id generation of new record.
//        Optional<User> rsUser = this.userDAO.save(user);
//        Assertions.assertTrue((rsUser.isPresent()));
//        Assertions.assertNotNull(rsUser.get().getId());
//    }
//
//    @Test
//    public void testFindById() {
//        // Create the test user.
//        User user = new User();
//        user.setUsername("testuser");
//
//        ArrayList<String> emails = new ArrayList<>();
//        emails.add("testuser@email1.com");
//        emails.add("testuser@email2.com");
//        emails.add("testuser@email3.com");
//        user.setEmails(emails);
//
//        user.setFirstName("Test");
//        user.setLastName("User");
//        user.setPhoneNumber("+16195554321");
//
//        user.setPasswordHash("passhash");
//
//        // Save the test user and update the reference object with field values from insert (id, createAt).
//        user = (this.userDAO.save(user)).get();
//
//        // Test the findById() method.
//        Optional<User> user1 = this.userDAO.findById(user.getId());
//
//        Assertions.assertTrue(user1.isPresent());
//        Assertions.assertEquals("testuser", user1.get().getUsername());
//        Assertions.assertEquals("testuser@email1.com", user1.get().getEmails().get(0));
//        Assertions.assertEquals("testuser@email2.com", user1.get().getEmails().get(1));
//        Assertions.assertEquals("testuser@email3.com", user1.get().getEmails().get(2));
//        Assertions.assertEquals("Test", user1.get().getFirstName());
//        Assertions.assertEquals("User", user1.get().getLastName());
//        Assertions.assertEquals("+16195554321", user1.get().getPhoneNumber());
//        Assertions.assertEquals("passhash", user1.get().getPasswordHash());
//    }
//
//    @Test
//    public void testFindByUsername() {
//        // Create the test user.
//        User user = new User();
//        user.setUsername("testuser");
//
//        ArrayList<String> emails = new ArrayList<>();
//        emails.add("testuser@email1.com");
//        emails.add("testuser@email2.com");
//        emails.add("testuser@email3.com");
//        user.setEmails(emails);
//
//        user.setFirstName("Test");
//        user.setLastName("User");
//        user.setPhoneNumber("+16195554321");
//
//        user.setPasswordHash("passhash");
//
//        // Save the test user and update the reference object with field values from insert (id, createAt).
//        user = (this.userDAO.save(user)).get();
//
//        // Test the findById() method.
//        Optional<User> user1 = this.userDAO.findByUsername(user.getUsername());
//
//        Assertions.assertTrue(user1.isPresent());
//        Assertions.assertEquals("testuser", user1.get().getUsername());
//        Assertions.assertEquals("testuser@email1.com", user1.get().getEmails().get(0));
//        Assertions.assertEquals("testuser@email2.com", user1.get().getEmails().get(1));
//        Assertions.assertEquals("testuser@email3.com", user1.get().getEmails().get(2));
//        Assertions.assertEquals("Test", user1.get().getFirstName());
//        Assertions.assertEquals("User", user1.get().getLastName());
//        Assertions.assertEquals("+16195554321", user1.get().getPhoneNumber());
//        Assertions.assertEquals("passhash", user1.get().getPasswordHash());
//    }
//
//    @Test
//    public void testFindAll() {
//        // Create test Users.
//        User user = new User();
//        user.setUsername("testuser");
//
//        ArrayList<String> emails = new ArrayList<>();
//        emails.add("testuser@email1.com");
//        emails.add("testuser@email2.com");
//        emails.add("testuser@email3.com");
//        user.setEmails(emails);
//
//        user.setFirstName("Test");
//        user.setLastName("User");
//        user.setPhoneNumber("+16195554321");
//
//        User user1 = new User();
//        user1.setUsername("testuser1");
//
//        ArrayList<String> emails1 = new ArrayList<>();
//        emails1.add("testuser1@email1.com");
//        emails1.add("testuser1@email2.com");
//        emails1.add("testuser1@email3.com");
//        user1.setEmails(emails1);
//
//        user1.setFirstName("Test1");
//        user1.setLastName("User1");
//        user1.setPhoneNumber("+16195554321");
//
//        user = (this.userDAO.save(user)).get();
//        user1 = (this.userDAO.save(user1)).get();
//
//        // Test the findAll() method.
//        List<User> users = this.userDAO.findAll();
//
//        Assertions.assertEquals(2, users.size());
//
//        User resUser = users.getFirst();
//        User resUser1 = users.getLast();
//
//        Assertions.assertEquals(user.getUsername(), resUser.getUsername());
//        Assertions.assertEquals(user1.getUsername(), resUser1.getUsername());
//    }
//
//    @Test
//    public void testUpdate() {
//        // Create test Users.
//        User user = new User();
//        user.setUsername("testuser");
//
//        ArrayList<String> emails = new ArrayList<>();
//        emails.add("testuser@email1.com");
//        emails.add("testuser@email2.com");
//        emails.add("testuser@email3.com");
//        user.setEmails(emails);
//
//        user.setFirstName("Test");
//        user.setLastName("User");
//        user.setPhoneNumber("+16195554321");
//
//        user.setPasswordHash("passhash");
//
//        user = (this.userDAO.save(user)).get();
//
//        // Update values on the User object.
//        user.setUsername("ckramer");
//
//        emails.clear();
//        emails.add("ckramer@email1.com");
//        emails.add("ckramer@email2.com");
//        emails.add("ckramer@email3.com");
//
//        user.setFirstName("Cosmo");
//        user.setLastName("Kramer");
//        user.setPhoneNumber("+18385554321");
//
//        user.setPasswordHash("passhash1");
//
//        // Update the database record.
//        int result = this.userDAO.update(user);
//
//        // Query the user that was updated. Test that values match the updated values.
//        Optional<User> oUser1 = this.userDAO.findById(user.getId());
//
//        Assertions.assertTrue(oUser1.isPresent());
//
//        User user1 = oUser1.get();
//
//        Assertions.assertEquals(user1.getId(), user.getId());
//        Assertions.assertEquals(user1.getUsername(), "ckramer");
//        Assertions.assertEquals(user1.getFirstName(), "Cosmo");
//        Assertions.assertEquals(user1.getLastName(), "Kramer");
//        Assertions.assertEquals(user1.getPhoneNumber(), "+18385554321");
//        Assertions.assertNotNull(user1.getUpdatedAt());
//        Assertions.assertEquals(user1.getPasswordHash(), "passhash1");
//    }
//
//    @Test
//    public void testDeleteById() {
//        // Create test Users.
//        User user = new User();
//        user.setUsername("testuser");
//
//        ArrayList<String> emails = new ArrayList<>();
//        emails.add("testuser@email1.com");
//        emails.add("testuser@email2.com");
//        emails.add("testuser@email3.com");
//        user.setEmails(emails);
//
//        user.setFirstName("Test");
//        user.setLastName("User");
//        user.setPhoneNumber("+16195554321");
//
//        user = (this.userDAO.save(user)).get();
//
//        // Test delete fail response.
//        int resFail = this.userDAO.deleteById(Long.valueOf(0));
//
//        Assertions.assertEquals(0, resFail);
//
//        // Test delete success response.
//        int resSuccess = this.userDAO.deleteById(Long.valueOf(user.getId()));
//
//        Assertions.assertEquals(1, resSuccess);
//    }
//
//    @Test
//    public void testDelete() {
//        // Create test Users.
//        User user = new User();
//        user.setUsername("testuser");
//
//        ArrayList<String> emails = new ArrayList<>();
//        emails.add("testuser@email1.com");
//        emails.add("testuser@email2.com");
//        emails.add("testuser@email3.com");
//        user.setEmails(emails);
//
//        user.setFirstName("Test");
//        user.setLastName("User");
//        user.setPhoneNumber("+16195554321");
//
//        user = (this.userDAO.save(user)).get();
//
//        // Test delete fail response.
//        User failUser = new User();
//        failUser.setId(Long.valueOf(0));
//        int resFail = this.userDAO.delete(failUser);
//
//        Assertions.assertEquals(0, resFail);
//
//        // Test delete success response.
//        int resSuccess = this.userDAO.delete(user);
//
//        Assertions.assertEquals(1, resSuccess);
//    }
}
