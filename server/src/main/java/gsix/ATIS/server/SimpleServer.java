package gsix.ATIS.server;

import gsix.ATIS.entities.*;
import gsix.ATIS.server.ocsf.AbstractServer;
import gsix.ATIS.server.ocsf.ConnectionToClient;
import gsix.ATIS.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.NoResultException;

import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class SimpleServer extends AbstractServer {

    private static ArrayList<SubscribedClient> subscribersList = new ArrayList<>();

    private SessionFactory sessionFactory;
    private static Session session;
    private static Transaction transaction;



    // Method to check if the tasks table is empty
    private static boolean isTasksTableEmpty() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        query.select(builder.count(query.from(Task.class)));
        Long count = session.createQuery(query).uniqueResult();
        return count == 0;
    }

    /*private static void initializeData() throws Exception {

        if(isTasksTableEmpty()) {
            // Check if tasks exist before creating and saving
            if (!taskExists(1)) {
                Task t1 = new Task( "1", "car repair", LocalDateTime.now(), "Request",1);
                session.save(t1);
            }
            if (!taskExists(2)) {
                Task t2 = new Task("2", "wash machine repair", LocalDateTime.now(), "Pending",1);
                session.save(t2);
            }
            if (!taskExists(3)) {
                Task t3 = new Task("3", "fridge repair", LocalDateTime.now(), "Pending",1);
                session.save(t3);
            }
            if (!taskExists(4)) {
                Task t4 = new Task("4", "buy groceries", LocalDateTime.now(), "Pending",1);
                session.save(t4);
            }
            if (!taskExists(5)) {
                Task t5 = new Task("5", "CHECK CODE PLEASE", LocalDateTime.now(), "Pending",2);
                session.save(t5);
            }
            if (!taskExists(6)) {
                Task t6 = new Task("6", "VALIDATE CODE PLEASE", LocalDateTime.now(), "Pending",2);
                session.save(t6);
            }
            if (!taskExists(7)) {
                Task t7 = new Task("7", "Task 7", LocalDateTime.now(), "Pending",2);
                session.save(t7);
            }
            if (!taskExists(8)) {
                Task t8 = new Task("8", "Task 8", LocalDateTime.now(), "Pending",2);
                session.save(t8);
            }
            if (!taskExists(9)) {
                Task t9 = new Task("9", "Task 9", LocalDateTime.now(), "Pending",3);
                session.save(t9);
            }

            session.flush();
            session.getTransaction().commit();
        }
    }*/

    // Method to check if a task with a given ID exists
    private static boolean taskExists(int taskId) {
        Task task = session.get(Task.class, taskId);
        return task != null;
    }

    public void updateUserType(String userId, String newUserType) {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            User user = session.get(User.class, userId);
            if (user != null) {
                user.setUser_type(newUserType);
                session.update(user);
                session.getTransaction().commit();
                System.out.println("User type updated in the database.");
            } else {
                System.out.println("User not found.");
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void updateUser2(User message) {
        // Extract the data from the message
        User userToUpdate = message;

        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            // Fetch the user from the database using the user_id
            User existingUser = session.get(User.class, userToUpdate.getUser_id());
            if (existingUser != null) {
                // Update the user details
                existingUser.setUser_name(userToUpdate.getUser_name());  // Update user name
                existingUser.setFirst_name(userToUpdate.getFirst_name()); // Update first name
                existingUser.setLast_name(userToUpdate.getLast_name());   // Update last name
                existingUser.setPassword(userToUpdate.getPassword());      // Update password
                existingUser.setUser_type(userToUpdate.getUser_type());    // Update user type
                existingUser.setCommunity(userToUpdate.getCommunityId());  // Update community ID
                existingUser.setLogged_in(userToUpdate.getLogged_in());    // Update logged in status

                // Save changes
                session.update(existingUser);
                session.getTransaction().commit();
                System.out.println("User updated in the database.");
            } else {
                System.out.println("User with ID " + userToUpdate.getUser_id() + " not found in the database.");
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }






    public void updateUserCommunity(User user) {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            // Fetch the user from the database using the user_id
            User existingUser = session.get(User.class, user.getUser_id());
            if (existingUser != null) {
                // Update the community_id
                existingUser.setCommunity(user.getCommunityId());

                // Save changes
                session.update(existingUser);
                session.getTransaction().commit();
                System.out.println("User community updated in the database.");
            } else {
                System.out.println("User not found in the database.");
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    private User getUserByManagerId(String managerId, Integer communityId) {
        Session session = null;
        User user = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            // Fetch the community to verify if the manager_id matches
            Community community = session.get(Community.class, communityId);
            if (community != null && managerId.equals(community.getManager_id())) {
                user = session.get(User.class, managerId);
            }
            session.getTransaction().commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return user;
    }

    public void updateUser(User user) {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            // Fetch the user from the database using the user_id
            User existingUser = session.get(User.class, user.getUser_id());
            if (existingUser != null) {
                // Update the user details
                existingUser.setUser_name(user.getUser_name());  // Update user name
                existingUser.setFirst_name(user.getFirst_name()); // Update first name
                existingUser.setLast_name(user.getLast_name());   // Update last name
                existingUser.setPassword(user.getPassword());      // Update password
                existingUser.setUser_type(user.getUser_type());    // Update user type
                existingUser.setCommunity(user.getCommunityId());  // Update community ID
                existingUser.setLogged_in(user.getLogged_in());    // Update logged in status

                // Save changes
                session.update(existingUser);
                session.getTransaction().commit();
                System.out.println("User updated in the database.");
            } else {
                System.out.println("User with ID " + user.getUser_id() + " not found in the database.");
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }



    public void addUserToCommunity(User newUser) {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            session.save(newUser);

            session.getTransaction().commit();
            System.out.println("User added to the community in the database.");
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }


    public void deleteUserFromCommunity(String userId) {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            User user = session.get(User.class, userId);
            if (user != null) {
                session.delete(user);
                session.getTransaction().commit();
                System.out.println("User deleted from the community in the database.");
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void updateCommunityManager(int communityId, String newManagerId) {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            session.beginTransaction();

            // Fetch the current manager ID
            String currentManagerId = getCurrentManagerId(communityId);

            // Update the user type of the current manager to "community user"
            if (currentManagerId != null && !currentManagerId.equals(newManagerId)) {
                updateUserType(currentManagerId, "community user");
            }

            // Update the user type of the new manager to "community manager"
            if (newManagerId != null) {
                updateUserType(newManagerId, "community manager");
            }

            // Update the community with the new manager ID
            Community community = session.get(Community.class, communityId);
            if (community != null) {
                community.setManager_id(newManagerId);
                session.update(community);
                session.getTransaction().commit();
                System.out.println("Community manager updated in the database.");
            } else {
                System.out.println("Community not found.");
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    private String getCurrentManagerId(int communityId) {
        Session session = null;
        String managerId = null;
        try {
            session = getSessionFactory().openSession();
            Community community = session.get(Community.class, communityId);
            if (community != null) {
                managerId = community.getManager_id();
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return managerId;
    }



    public void addNewCommunity(Community newCommunity) {
        Session session = null;

        try {
            System.out.println("Opening session..."); // Debug line
            session = getSessionFactory().openSession();
            session.beginTransaction();
            System.out.println("Transaction has BEGUN");

            session.save(newCommunity);
            System.out.println("After session.save");

            session.getTransaction().commit();
            System.out.println("Community added to the database.");
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
                System.out.println("Transaction rolled back due to an error.");
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
                System.out.println("Session closed.");
            }
        }
    }


    public static List<Community> getAllCommunities() //Majd
    {    // Obtain the current session
        Session session = getSessionFactory().openSession(); // Make sure to manage the session lifecycle appropriately
        List<Community> communities = null;
        try {
            // Create a CriteriaBuilder
            CriteriaBuilder builder = session.getCriteriaBuilder();

            // Create a CriteriaQuery
            CriteriaQuery<Community> criteriaQuery = builder.createQuery(Community.class);

            // Define the root for the query
            Root<Community> rootEntry = criteriaQuery.from(Community.class);

            // Set the query to select all entries from the Community table
            CriteriaQuery<Community> allCriteriaQuery = criteriaQuery.select(rootEntry);

            // Create a TypedQuery from the CriteriaQuery
            TypedQuery<Community> allQuery = session.createQuery(allCriteriaQuery);

            // Get the result list
            communities = allQuery.getResultList();
            System.out.println("I am in getAllCommunities: " + communities);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensure the session is closed
            if (session != null) {
                session.close();
            }
        }
        return communities;
    }

    // Fetch SOS requests between start and end date
    public static List<SosRequest> getSosRequestsBetween(LocalDate startDate, LocalDate endDate) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<SosRequest> criteriaQuery = builder.createQuery(SosRequest.class);
        Root<SosRequest> sosRoot = criteriaQuery.from(SosRequest.class);
        System.out.println("INSIDE getSosRequests database code");
        LocalDateTime startDateTime = startDate.atStartOfDay(); // Get the start of the day
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusSeconds(1); // Get the end of the day

        Predicate betweenPredicate = builder.between(
                sosRoot.get("time"),
                startDateTime,
                endDateTime
        );

        criteriaQuery.where(betweenPredicate);
        return session.createQuery(criteriaQuery).getResultList();
    }
    public List<SosRequest> getSosRequestsForCommunityBetween(int communityId, LocalDate startDate, LocalDate endDate) {
        List<SosRequest> allSosRequests = getSosRequestsBetween(startDate, endDate);
        List<User> allUsers = getAll(User.class);

        // Create a map to easily get the community ID for each requester
        Map<String, Integer> requesterToCommunity = allUsers.stream()
                .collect(Collectors.toMap(User::getUser_id, User::getCommunityId));

        // Filter the SOS requests to get only those for the specified community
        List<SosRequest> filteredSosRequests = allSosRequests.stream()
                .filter(sosRequest -> {
                    Integer community = requesterToCommunity.get(sosRequest.getRequester_id());
                    return community != null && community == communityId;
                })
                .collect(Collectors.toList());
        System.out.println("Community id is: "+communityId);
        System.out.println(filteredSosRequests);
        //it returns the filtered Requests
        return filteredSosRequests;
    }



    public static List<Task> getAllSystemPendingTasks() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery = builder.createQuery(Task.class);
        Root<Task> taskRoot = criteriaQuery.from(Task.class);

        // Adding condition to the criteria query based on status
        criteriaQuery.where(builder.equal(taskRoot.get("status"), "Pending"));

        return session.createQuery(criteriaQuery).getResultList();
    }


    public static List<Task> getAllRequestedTasksByCommunity(int communityID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery = builder.createQuery(Task.class);
        Root<Task> taskRoot = criteriaQuery.from(Task.class);

        // Joining Task with User entity using the requester_id attribute
        Join<Task, User> userJoin = taskRoot.join("requester");

        // Adding condition to the criteria query based on communityId and status
        criteriaQuery.where(
                builder.equal(userJoin.get("community_id"), communityID),
                builder.equal(taskRoot.get("status"), "Request")
        );
        return session.createQuery(criteriaQuery).getResultList();
        /*List<Task> list =*/
        //String sqlQuery = session.createQuery(criteriaQuery).unwrap(org.hibernate.query.Query.class).getQueryString();
        //System.out.println("Generated SQL Query: " + sqlQuery);
        //System.out.println("Result: " + list);
        //return list;
    }

    public static List<User> getAllCommunityMembers(int communityID){
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> userRoot = criteriaQuery.from(User.class);

        // Adding condition to the criteria query based on communityId
        criteriaQuery.where(builder.equal(userRoot.get("community_id"), communityID));

        /*List<User> list =*/ return session.createQuery(criteriaQuery).getResultList();
        /*String sqlQuery = session.createQuery(criteriaQuery).unwrap(org.hibernate.query.Query.class).getQueryString();
        System.out.println("Generated SQL Query: " + sqlQuery);
        System.out.println("Result: " + list);
        return list;*/
    }

    public static <T> List<T> getAllByCommunity(Class<T> object, String userId) {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<Task> taskRoot = criteriaQuery.from(Task.class);

        // Joining Task with User entity using the requester_id attribute
        Join<Task, User> userJoin = taskRoot.join("requester"); // Assuming "requester" is the attribute in Task entity referring to User entity

        // Fetching the User entity based on userId
        CriteriaQuery<User> userQuery = builder.createQuery(User.class);
        Root<User> userRoot = userQuery.from(User.class);
        userQuery.select(userRoot).where(builder.equal(userRoot.get("user_id"), userId)); // Assuming the primary key in User entity is "userId"
        User user = session.createQuery(userQuery).getSingleResult();

        // Accessing communityId from the fetched User entity
        int communityId = user.getCommunityId();

        // Adding condition to the criteria query based on communityId
        criteriaQuery.where(
                builder.equal(userJoin.get("community_id"), communityId),
                builder.equal(taskRoot.get("status"), "pending"),
                builder.notEqual(taskRoot.get("requester").get("user_id"), userId)
        );
//
//        // Executing the criteria query and returning the result
//        List<T> list=session.createQuery(criteriaQuery).getResultList();
//        System.out.println("I am in getAllByCommunity"+list);
//        return list;
        // Executing the criteria query and returning the result
        List<T> list = session.createQuery(criteriaQuery).getResultList();
        String sqlQuery = session.createQuery(criteriaQuery).unwrap(org.hibernate.query.Query.class).getQueryString();
        System.out.println("Generated SQL Query: " + sqlQuery);
        System.out.println("Result: " + list);
        return list;
    }


//    private static String getCommunityIdByUserId(String userId) { //Made By Ayal
//        // Your logic to fetch community ID based on user ID
//        String queryString = "SELECT u.community_id FROM User u WHERE u.user_id = :userId";
//        Query<String> query = session.createQuery(queryString, String.class);
//        query.setParameter("userId", userId);
//        return query.uniqueResult();
//    }

    public static <T> List<T> getAll(Class<T> object) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst =allQuery.getResultList();
        System.out.println("I am in getAll"+lst);
        return lst;
    }
    public static <T> List<T> getRequestedTasks(Class<T> object, String userID,String desiredStatus) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        // Add a condition to filter tasks based on requester_id
        Predicate requesterPredicate  = builder.equal(rootEntry.get("requester_id"), userID);
        Predicate statusPredicate = builder.equal(rootEntry.get("status"), desiredStatus);
        Predicate finalPredicate = builder.and(requesterPredicate, statusPredicate);
        allCriteriaQuery.where(finalPredicate);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst = allQuery.getResultList();
        System.out.println("I am in getRequested" + lst);
        return lst;
    }
    public static <T> List<T> getVolunteeredTasks(Class<T> object, String userID,String desiredStatus) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        // Add a condition to filter tasks based on volunteer_id
        Predicate volunteerPredicate = builder.equal(rootEntry.get("volunteer_id"), userID);
        Predicate statusPredicate = builder.equal(rootEntry.get("status"), desiredStatus);
        Predicate finalPredicate = builder.and(volunteerPredicate, statusPredicate);
        allCriteriaQuery.where(finalPredicate);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst = allQuery.getResultList();
        System.out.println("I am in getVolunteered" + lst);
        return lst;
    }

    public static <T> List<T> getSentMessages(Class<T> object, String userID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        // Add a condition to filter tasks based on requester_id
        Predicate predicate = builder.equal(rootEntry.get("sender_id"), userID);
        allCriteriaQuery.where(predicate);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst = allQuery.getResultList();
        System.out.println("I am in getSent" + lst);
        return lst;
    }
    public static <T> List<T> getReceivedMessages(Class<T> object, String userID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        // Add a condition to filter tasks based on requester_id
        Predicate predicate = builder.equal(rootEntry.get("receiver_id"), userID);
        allCriteriaQuery.where(predicate);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst = allQuery.getResultList();
        System.out.println("I am in getSent" + lst);
        return lst;
    }



    public static <T> T getUserById(Class<T> object, String Id) {

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> root = criteriaQuery.from(object);
        if (object.equals(Task.class)) {
            criteriaQuery.select(root).where(builder.equal(root.get("task_id"), Id));
        } else {
            criteriaQuery.select(root).where(builder.equal(root.get("user_id"), Id));
        }
        TypedQuery<T> query = session.createQuery(criteriaQuery);
        return query.getSingleResult();
    }


    public static <T> T getEntityById(Class<T> object, int Id) {

        if (session == null || !session.isOpen()) {
            session = getSessionFactory().openSession();
        }
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
            Root<T> root = criteriaQuery.from(object);
            if (object.equals(Task.class)) {
                criteriaQuery.select(root).where(builder.equal(root.get("task_id"), Id));
            } else {
                criteriaQuery.select(root).where(builder.equal(root.get("user_id"), Id));
            }
            TypedQuery<T> query = session.createQuery(criteriaQuery);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null; // Return null if no result found
        }
    }

    public static <T> T getUser(Class<T> object, String user_name, String password) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> root = criteriaQuery.from(object);

        if (object.equals(User.class)) {
            Predicate condition1 = builder.equal(root.get("user_name"), user_name);
            Predicate condition2 = builder.equal(root.get("password"), password);
            Predicate finalCondition = builder.and(condition1, condition2);
            criteriaQuery.select(root).where(finalCondition);
        }

        TypedQuery<T> query = session.createQuery(criteriaQuery);

        try {
            T result = query.getSingleResult();
            if (result instanceof User) {
                User user = (User) result;
                if (user.getLogged_in() == 1) { // User is already logged in
                    Message message = new Message(2, "User already logged in");
                    return (T) message;
                } else { // User is not logged in, proceed with login
                    // Set logged_in to 1 to indicate successful login
                    user.setLogged_in(1);
                    session.update(user);
                    transaction.commit(); // Commit the update
                    return (T) user;
                }
            }
        } catch (Exception e) {
            // User not found
            Message message = new Message(1, "username or password is incorrect!");
            return (T) message;
        }

        return null; // Fallback if needed
    }
    public void logOut(User user){
        user.setLogged_in(0);
        session.update(user);
        transaction.commit(); // Commit the update
    }


    public static void updateTask(Task updatedTask) {
        session.update(updatedTask); // Update the task
        transaction.commit();
    }
    public static void addTask(Task newTask) {
        session.save(newTask); // Add the task
        transaction.commit();
    }
    public static void deleteTask(Task taskToDelete) {
        System.out.println("in server in delete task from DB function");

        session.delete(taskToDelete); // Delete the task
        transaction.commit();
        System.out.println("Task deleted successfully.");
    }

    public static void addSos(SosRequest newSos) {
        try {
            System.out.println("in server doing SOS  Save");
            System.out.println(newSos.toString());
            session.save(newSos); // Add the SosCall
            System.out.println("in server done SOS  Save");
            transaction.commit();
        } catch (Exception e1) {
            if (transaction != null && transaction.isActive()) {
                System.out.println("I am about to roll back3");
                transaction.rollback();
            }
            System.out.println(e1.getMessage());

        }
    }

    public static void printTasksTest(List<Task> lst){
        for (Task task:lst){
            System.out.println(task.getStatus());
        }
    }
    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(Task.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Community.class);
        configuration.addAnnotatedClass(CommunityMessage.class);
        configuration.addAnnotatedClass(SosRequest.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    public SimpleServer(int port) {
        super(port);

        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            //initializeData();
        }
        catch (Exception e) {
            if (session != null) {
                System.out.println("I am about to rollback");
                session.getTransaction().rollback();
            }

            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
                session.getSessionFactory().close();
            }
        }
    }
    public static <T> List<T> getUnfinishedTasks(Class<T> object, String userID) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = builder.createQuery(object);
        Root<T> rootEntry = criteriaQuery.from(object);
        CriteriaQuery<T> allCriteriaQuery = criteriaQuery.select(rootEntry);

        // Create predicates for the volunteer_id and status
        Predicate volunteerPredicate = builder.equal(rootEntry.get("volunteer_id"), userID);
        Predicate statusPredicate = builder.equal(rootEntry.get("status"), "in process");

        // Combine predicates with "and" to create the final condition
        Predicate combinedPredicate = builder.and(volunteerPredicate, statusPredicate);

        allCriteriaQuery.where(combinedPredicate);

        TypedQuery<T> allQuery = session.createQuery(allCriteriaQuery);
        List<T> lst = allQuery.getResultList();
        System.out.println("I am in getUnfinishedTasks" + lst);
        return lst;
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        Message message = (Message) msg;
        String request = message.getMessage();
        System.out.println("Im inside handle from client");
        try {

            sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            //we got an empty message, so we will send back an error message with the error details.
            if (request.isBlank()) {
                message.setMessage("Error! we got an empty message");
                client.sendToClient(message);
            }
            //we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
            // the IDs at data field in Message entity and send back to all subscribed clients a request to update
            //their IDs text fields. An example of use of observer design pattern.
            //message format: "change submitters IDs: 123456789, 987654321"
            else if (request.equals("get all tasks")) {
                //System.out.println("I am in get all tasks");
                message.setData(getAll(Task.class));
                message.setMessage("get all tasks: Done");
                //System.out.println(message);
                //printTasksTest((List<Task>) message.getData());
                //System.out.println(client);
                client.sendToClient(message);
                //sendToAllClients(message);

            }
            else if (message.getMessage().equals("get sos requests for all communities between dates")) {
                LocalDate[] dates = (LocalDate[]) message.getData(); // Extract the date array
                LocalDate start = dates[0];
                LocalDate end = dates[1];

                List<SosRequest> sosRequests = getSosRequestsBetween(start, end);
                System.out.println(sosRequests);

                // Send the retrieved SOS data back to the client
                message.setData(sosRequests);
                message.setMessage("SOS data retrieval for all communities: Done");
                client.sendToClient(message);
            }
            else if (message.getMessage().equals("get sos requests for community between dates")) {
                // Extract data from the message
                Object[] data = (Object[]) message.getData();
                int communityId = (int) data[0];
                LocalDate start = (LocalDate) data[1];
                LocalDate end = (LocalDate) data[2];
// Get SOS requests for the specified community and date range
                List<SosRequest> sosRequests = getSosRequestsForCommunityBetween(communityId, start, end);

                // Set the data and send it back to the client
                message.setData(sosRequests);
                message.setMessage("SOS data retrieval for community: Done");
                client.sendToClient(message);
            }
            else if(request.equals("get tasks for community")){ //Added by Ayal
                System.out.println("inside SimpleServer get task for community");
                String userId = (String) message.getData();
                message.setData(getAllByCommunity((Task.class),userId));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get tasks for community: Done");
                client.sendToClient(message);
            }
            else if(request.equals("get requested tasks by community")){
                System.out.println("inside SimpleServer get task for community");
                int communityID = (int) message.getData();
                message.setData(getAllRequestedTasksByCommunity(communityID));
                System.out.println(message.getData());
                message.setMessage("get requested tasks by community: Done");
                client.sendToClient(message);
            }
            else if(request.equals("get all system pending tasks")){
                //System.out.println("inside SimpleServer get task for community");
                //int communityID = (int) message.getData();
                message.setData(getAllSystemPendingTasks());
                //System.out.println(message.getData());
                message.setMessage("get all system pending tasks: Done");
                client.sendToClient(message);
            }
            else if(request.equals("send message")){ //Added by Ayal
                System.out.println("inside SimpleServer send message");
                String messageString = (String) message.getData();

                insertMessageToDataTable(messageString);

                message.setMessage("send masage to manager: Done");
                /**client.sendToClient(message);*/
                sendToAllClients(message);
            }
            else if(request.equals("notify no volunteer")){
                //System.out.println("inside SimpleServer ");
                String messageString = (String) message.getData();

                insertMessageToDataTable(messageString);

                //message.setMessage("send masage to manager: Done");
                //client.sendToClient(message);
            }
            // added by waheeb modified by ayal
            else if(request.equals("delete requested task")){
                System.out.println("inside SimpleServer delete requested task");
                int taskID = (int) message.getData();
                Task task=getEntityById(Task.class,taskID);//get task by id
                deleteTask(task);
                message.setMessage("delete requested task: Done");
                client.sendToClient(message);
            }
            else if(request.equals("start volunteering to task")){ //Added by Ayal
                System.out.println("inside simple server update task volunteer");
                Task dummyTask = (Task) message.getData();
                Task taskTarget=getEntityById(Task.class,dummyTask.getTask_id());
                taskTarget.setVolunteer_id(dummyTask.getVolunteer_id());
                taskTarget.setStatus(TaskStatus.inProcess);
                updateTask(taskTarget);

                String managerID = getManagerID(taskTarget.getCommunity_id());
                String msgToManager = "[Task in Process notification]: UserID "
                        + taskTarget.getVolunteer_id() + " is currently volunteering for task "+ taskTarget.getTask_id();
                CommunityMessage startVolunteeringMessage = new CommunityMessage(taskTarget.getVolunteer_id(),managerID,msgToManager);
                saveMessage(startVolunteeringMessage);

                message.setMessage("start volunteering to task : Done");
                //client.sendToClient(message);
                sendToAllClients(message);
            }
            else if(request.equals("get manager id")){ //Added by Ayal
                System.out.println("inside SimpleServer get manager id");
                int communityID = (int) message.getData();
                message.setData(getManagerID(communityID));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());//if it didnt reach here it means there is a problem
                message.setMessage("manager id is here");
                client.sendToClient(message);
            }
            else if(request.equals("Send Volunteering Task End and Update To Done")){
                System.out.println("inside SimpleServer get manager id");
                Task dummyTask = (Task) message.getData();
                String volunteerID = dummyTask.getVolunteer_id();
                String MessageToManager = dummyTask.getRequested_operation();
                Task taskDone=getEntityById(Task.class,dummyTask.getTask_id());

                updateTaskByID(taskDone.getTask_id(), "Done");

                //// Send message to current task requester's community manager to approve message
                User volunteerUser = getUserById(User.class,volunteerID);
                String managerID = getManagerID(volunteerUser.getCommunityId());
                saveMessage(volunteerID,managerID,MessageToManager);

              /*  message.setData(getManagerID(communityID));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());//if it didnt reach here it means there is a problem*/
                message.setMessage("Send Volunteering Task End and Update To Done: Done");
                //client.sendToClient(message);
                sendToAllClients(message);
            }
            else if(request.equals("get requested tasks")){ //Added by Ayal
                System.out.println("inside SimpleServer get requested tasks ");
                Task dummyTask = (Task) message.getData();
                String userId = dummyTask.getRequester_id();
                String desiredStatus = dummyTask.getStatus();
                message.setData(getRequestedTasks((Task.class),userId,desiredStatus));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get requested tasks: Done");
                client.sendToClient(message);
            }
            else if(request.equals("get volunteered tasks")){ //Added by Ayal
                System.out.println("inside SimpleServer get volunteered tasks ");
                Task dummyTask = (Task) message.getData();
                String userId = dummyTask.getRequester_id();
                String desiredStatus = dummyTask.getStatus();
                message.setData(getVolunteeredTasks((Task.class),userId,desiredStatus));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get volunteered tasks: Done");
                client.sendToClient(message);
            }

            else if(request.equals("get sent messages")){ //Added by Ayal
                System.out.println("inside SimpleServer get sent messages ");
                String userId = (String) message.getData();
                message.setData(getSentMessages((CommunityMessage.class),userId));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get sent messages: Done");
                client.sendToClient(message);
            }

            else if(request.equals("get unfinished tasks")){ //Added by Ayal
                System.out.println("inside SimpleServer get unfinished tasks");
                String userId = (String) message.getData();
                message.setData(getUnfinishedTasks((Task.class),userId));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get unfinished tasks: Done");
                client.sendToClient(message);
            }

            else if (request.equals("update community manager")) {
                System.out.println("Received request to update community manager"); // Debug line
                Object[] data = (Object[]) message.getData();
                int communityId = (int) data[0];
                String newManagerId = (String) data[1];

                updateCommunityManager(communityId, newManagerId);

                message.setMessage("update community manager: Done");
                client.sendToClient(message);
                System.out.println("Community manager update processed."); // Debug line
            }


            else if(request.equals("get received messages")){ //Added by Ayal
                System.out.println("inside SimpleServer get received messages ");
                String userId = (String) message.getData();
                message.setData(getReceivedMessages((CommunityMessage.class),userId));// Should change this back to getAllByCommunity , it works on getAll
                System.out.println(message.getData());
                message.setMessage("get received messages: Done");
                client.sendToClient(message);
            }
            else if (request.equals("get all users")) {

                message.setData(getAll(User.class));
                message.setMessage("get all users: Done");
                client.sendToClient(message);

            }

            else if (request.equals("add new community")) {
                System.out.println("Received request to add new community"); // Debug line
                Community newCommunity = (Community) message.getData();
                System.out.println("New Community Data: " + newCommunity); // Debug line
                addNewCommunity(newCommunity);
                message.setMessage("add new community: Done");
                client.sendToClient(message);
                System.out.println("Community addition processed."); // Debug line
            }



            else if (request.equals("add user to community")) {
                User newUser = (User) message.getData();

                // Add the user to the community
                addUserToCommunity(newUser);

                // Send confirmation back to the client
                message.setMessage("add user to community: Done");
                client.sendToClient(message);
            }


            else if (request.equals("delete community user")) {
                String userId = (String) message.getData();
                deleteUserFromCommunity(userId);
                message.setMessage("delete community user: Done");
                client.sendToClient(message);
            }

            else if (request.equals("update user")) {
                System.out.println("Received request to update user"); // Debug line
                // Extract data from the message
                Object[] data = (Object[]) message.getData();
                String newManagerId = (String) data[0];
                String newUserName = (String) data[1];
                String newFirstName = (String) data[2];
                String newLastName = (String) data[3];
                String newPassword = (String) data[4];
                String userType = (String) data[5];
                Integer communityId = (Integer) data[6];
                int loggedIn = (int) data[7];

                // Update user based on the provided manager_id
                User userToUpdate = getUserByManagerId(newManagerId, communityId);

                if (userToUpdate != null) {
                    userToUpdate.setUser_name(newUserName);
                    userToUpdate.setFirst_name(newFirstName);
                    userToUpdate.setLast_name(newLastName);
                    userToUpdate.setPassword(newPassword);
                    userToUpdate.setUser_type(userType);
                    userToUpdate.setCommunity(communityId);
                    userToUpdate.setLogged_in(loggedIn);

                    updateUser(userToUpdate); // Save the updated user

                    message.setMessage("update user: Done");
                    client.sendToClient(message);
                    System.out.println("User update processed."); // Debug line
                } else {
                    System.out.println("User to update not found.");
                }
            }

            else if (request.equals("update user2")) {
                System.out.println("Received request to update user2"); // Debug line

                // Extract data from the message
                Object[] data = (Object[]) message.getData();
                String userId = (String) data[0];
                String newUserName = (String) data[1];
                String newFirstName = (String) data[2];
                String newLastName = (String) data[3];
                String newPassword = (String) data[4];
                String userType = (String) data[5];
                int loggedIn = (int) data[6];

                // Fetch user based on user_id directly
                User userToUpdate = getUserById(User.class, userId);

                if (userToUpdate != null) {
                    userToUpdate.setUser_name(newUserName);
                    userToUpdate.setFirst_name(newFirstName);
                    userToUpdate.setLast_name(newLastName);
                    userToUpdate.setPassword(newPassword);
                    userToUpdate.setUser_type(userType);
                    userToUpdate.setLogged_in(loggedIn);

                    updateUser(userToUpdate); // Save the updated user

                    message.setMessage("update user2: Done");
                    client.sendToClient(message);
                    System.out.println("User update processed."); // Debug line
                } else {
                    System.out.println("User to update not found.");
                }
            }







            else if (request.equals("update user")) {
                System.out.println("Received request to update user"); // Debug line
                User userToUpdate = (User) message.getData();

                updateUser(userToUpdate); // Ensure this matches the method signature

                message.setMessage("update user: Done");
                client.sendToClient(message);
                System.out.println("User update processed."); // Debug line
            }





            else if (request.equals("get all community users")) {
                int community_id = (int) message.getData();
                //System.out.println("*********************************8");
                //System.out.println(getAllCommunityMembers(community_id));
                //System.out.println("**********************************");
                message.setData(getAllCommunityMembers(community_id));
                message.setMessage("get all community users: Done");
                client.sendToClient(message);

            }

            else if (request.equals("view task info")) {

                int taskId = (Integer) message.getData();
                Task task = getEntityById(Task.class, taskId);
                message.setData(task);
                message.setMessage("view task info: Done");
                client.sendToClient(message);

            } else if (request.equals("get task by id")) {

                int taskId = (Integer) message.getData();
                Task updatedTask = getEntityById(Task.class, taskId);
                //updatedTask.setStatus(TaskStatus.Pending);
                updateTask(updatedTask);
                Task testUpdate = getEntityById(Task.class, taskId);
                message.setData(testUpdate);
                message.setMessage("get task by id: Done");
                client.sendToClient(message);

            }else if (request.equals("get task for decline")) {

                int taskId = (Integer) message.getData();
                Task declinedTask = getEntityById(Task.class, taskId);
                message.setData(declinedTask);
                message.setMessage("get task for decline: Done");
                client.sendToClient(message);

            }else if (request.equals("change task status")) {

                Task updatedTask = (Task) message.getData();
                updateTask(updatedTask);
                Task testUpdate = getEntityById(Task.class, updatedTask.getTask_id());
                message.setData(testUpdate);
                message.setMessage("change task status: Done");
                client.sendToClient(message);

            }
            else if (request.equals("update task status")) {

                String updatedTaskIDInfo = (String) message.getData(); // string="taskID,status"
                String[] parts = updatedTaskIDInfo.split(",");
                System.out.println(Arrays.toString(parts)); //////////////////
                int updatedTaskID = Integer.parseInt(parts[0]);
                String newStatus = parts[1];

                updateTaskByID(updatedTaskID, newStatus);

                Task testUpdate = getEntityById(Task.class, updatedTaskID);
                message.setData(testUpdate);
                message.setMessage("update task status: Done");
                client.sendToClient(message);

            }
            else if (request.equals("update task status Pending")) {

                String updatedTaskIDInfo = (String) message.getData(); // string="taskID,status"
                String[] parts = updatedTaskIDInfo.split(",");
                System.out.println(Arrays.toString(parts)); //////////////////
                int updatedTaskID = Integer.parseInt(parts[0]);
                String newStatus = parts[1];

                updateTaskByID(updatedTaskID, newStatus);

                Task testUpdate = getEntityById(Task.class, updatedTaskID);
                sentMsgToCommunityTaskPending(testUpdate);

                message.setData(testUpdate);
                message.setMessage("update task status Pending: Done");
                //client.sendToClient(message);
                sendToAllClients(message);
            }else if (request.equals("delete task")) {

                int taskID = (int) message.getData();
                System.out.println("in server in delete task command");
                Task taskToDelete = getEntityById(Task.class, taskID);
                deleteTask(taskToDelete);
                message.setData(taskID);
                message.setMessage("delete task: Done");
                client.sendToClient(message);

            }else if (request.equals("Decline Task and Send Decline message to Requester")) {

                Message declineMessageInfo = (Message) message.getData();
                CommunityMessage communityMessage = (CommunityMessage) declineMessageInfo.getData();
                int declinedTaskID = Integer.parseInt(declineMessageInfo.getMessage());
                Task declinedTask = getEntityById(Task.class, declinedTaskID);
                communityMessage.setReceiver_id(declinedTask.getRequester_id());

                saveMessage(communityMessage);
                updateTaskByID(declinedTaskID,"Declined");

                //System.out.println("in server in delete task command");
                Task testDeclinedTask = getEntityById(Task.class, declinedTaskID);
                message.setData(testDeclinedTask);

                message.setMessage("Decline Task and Send Decline message to Requester: Done");
                //client.sendToClient(message);
                sendToAllClients(message);
            }else if (request.equals("get user by id")) {

                int userID = (Integer) message.getData();
                User user = getEntityById(User.class, userID);

                message.setData(user);
                message.setMessage("get user by id: Done");
                //System.out.println(user +" user got from server for volunteered window in members list");
                client.sendToClient(message);

            }

            else if (request.equals("open SoS request")) {

                SosRequest sosRequest = (SosRequest) message.getData();
                System.out.println("in server in SOS request command");
                //Task taskToDelete = getEntityById(Task.class, taskID);
                addSos(sosRequest);
                //message.setData(taskID);
                System.out.println("in server After SOS request save");
                message.setMessage("open SoS request: Done");
                //client.sendToClient(message);
                sendToAllClients(message);
            }
            else if (request.equals("log out")) {

                User user = (User) message.getData();
                System.out.println("logging out");
                logOut(user);

            }
            else if (request.equals("login request")) {
                System.out.println("*********INSIDE LOGIN REQUEST*********");
                User userData = (User) message.getData();
                System.out.println("Trying to log in to user: "+userData.toString());
                Object result = getUser(User.class, userData.getUser_name(), userData.getPassword());

                if (result instanceof User) {
                    message.setMessage("login request: Done");
                    SubscribedClient connection = new SubscribedClient(client);
                    subscribersList.add(connection);
                    User target = (User) result;
                    System.out.println("Trying to log in to user: "+target.toString());
                    message.setData(target);
                } else if (result instanceof Message) {
                    Message resultMessage = (Message) result;
                    if (resultMessage.getMessage().equals("User already logged in")) {
                        message.setMessage("login request: User already logged in");
                        message.setData(resultMessage);
                    } else {
                        message.setMessage("login request: Failed");
                        String errMsg = "username or password is incorrect!";
                        message.setData(errMsg);
                    }
                }

                client.sendToClient(message);
            }

            else if (request.equals("get all communities")) {
                System.out.println("inside SimpleServer get all communities");
                // Fetch all communities from the database
                List<Community> communities = getAllCommunities();


                // Set the response data and message
                message.setData(communities);
                message.setMessage("get all communities: Done");

                // Send the response back to the client
                client.sendToClient(message);
            }


            else if (request.equals("open request")) {

                Task newTask = (Task) message.getData();
                System.out.println(newTask.toString());
                addTask(newTask);
                Task testAdd = getEntityById(Task.class, newTask.getTask_id());
                message.setData(testAdd);
                message.setMessage("open request: Done");
                /**client.sendToClient(message);*/
                sendToAllClients(message);
                //// Send message to current task requester's community manager to approve message
                User requester = getUserById(User.class,testAdd.getRequester_id());
                String managerID = getManagerID(requester.getCommunityId());
                String messageContent = "[New Task Requested]: Task ID "+testAdd.getTask_id()+
                        "\nPlease approve it so your community users can volunteer to do it";
                saveMessage(requester.getUser_id(),managerID,messageContent);
            }
            // Commit the transaction
            System.out.println("server before transaction commit");
            transaction.commit();
        } catch (IOException e1) {
            if (transaction != null && transaction.isActive()) {
                System.out.println("I am about to roll back2");
                transaction.rollback();
            }
            e1.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
                session.getSessionFactory().close();
            }
        }
    }


    private void sentMsgToCommunityTaskPending(Task pendingTask){
        List<User> communityMembers = getAllCommunityMembers(pendingTask.getCommunity_id());
        String requesterID = pendingTask.getRequester_id();
        String currentUserID = "";
        String msgContent = "[New Task Available]: Task" + pendingTask.getTask_id() + " is currently Waiting for a volunteer";
        String managerID = getManagerID(pendingTask.getCommunity_id());
        for (User user : communityMembers){
            currentUserID =user.getUser_id();
            if (currentUserID.equals(requesterID)){
                String reqMsgContent = "[Task Approval notification]:Your Task (ID:"+pendingTask.getTask_id() +") has been approved";
                CommunityMessage requesterMsg = new CommunityMessage(managerID,requesterID,reqMsgContent);
                saveMessage(requesterMsg);
            }else{
                CommunityMessage currentMsg = new CommunityMessage(managerID,currentUserID,msgContent);
                saveMessage(currentMsg);
            }
        }
    }
    private void saveMessage(String senderID,String receiverID, String messageContent) {

        // Check if the session is available and open
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
        }
        // Check if there is an active transaction
        if (session.getTransaction() != null && session.getTransaction().isActive()) {//should we roll back or commit?
            System.out.println("There is an active transaction. Commiting...");
            session.getTransaction().commit();
        }

        // Continue with transaction handling
        try {
            // Begin a new transaction
            session.beginTransaction();
            System.out.println("Transaction has BEGUN");

            // Create a new CommunityMessage object
            CommunityMessage communityMessage = new CommunityMessage(senderID,receiverID,"\n"+messageContent);
            communityMessage.setSender_id(senderID);
            communityMessage.setReceiver_id(receiverID);
            communityMessage.setContent("\n"+messageContent);

            // Save the communityMessage object to the database
            session.save(communityMessage);
            System.out.println("After session.save");

            // Commit the transaction
            session.getTransaction().commit();
            System.out.println("Message saved to the database.");
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
    }

    public void saveMessage(CommunityMessage communityMessage) {

        // Check if the session is available and open
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
        }
        // Check if there is an active transaction
        if (session.getTransaction() != null && session.getTransaction().isActive()) {//should we roll back or commit?
            System.out.println("There is an active transaction. Commiting...");
            session.getTransaction().commit();
        }

        // Continue with transaction handling
        try {
            // Begin a new transaction
            session.beginTransaction();
            System.out.println("Transaction has BEGUN");

            // Save the communityMessage object to the database
            session.save(communityMessage);
            System.out.println("After session.save");

            // Commit the transaction
            session.getTransaction().commit();
            System.out.println("Message saved to the database.");
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
    }
    private void insertMessageToDataTable(String messageString) {//MADE BY Ayal
        //my messageString format is : senderID+","+receiverID+","+content
        // Find the position of the first comma
        int firstCommaIndex = messageString.indexOf(",");

        // Find the position of the second comma
        int secondCommaIndex = messageString.indexOf(",", firstCommaIndex + 1);

        // Extract senderID and receiverID
        String senderID = messageString.substring(0, firstCommaIndex);
        String receiverID = messageString.substring(firstCommaIndex + 1, secondCommaIndex);

        // Extract message content (everything after the second comma)
        String messageContent = messageString.substring(secondCommaIndex + 1);

        System.out.println(senderID);
        System.out.println(receiverID);
        System.out.println(messageContent);
        // Check if the session is available and open
        if (session == null || !session.isOpen()) {
            System.out.println("Session is null or closed.");
            return;
        }
        // Check if there is an active transaction
        if (session.getTransaction() != null && session.getTransaction().isActive()) {//should we roll back or commit?
            System.out.println("There is an active transaction. Commiting...");
            session.getTransaction().commit();
        }

        // Continue with transaction handling
        try {
            // Begin a new transaction
            session.beginTransaction();
            System.out.println("Transaction has BEGUN");

            // Create a new CommunityMessage object
            CommunityMessage communityMessage = new CommunityMessage(senderID,receiverID,"\n"+messageContent);
            /*communityMessage.setSender_id(senderID);
            communityMessage.setReceiver_id(receiverID);
            communityMessage.setContent("\n"+messageContent);*/

            // Save the communityMessage object to the database
            session.save(communityMessage);
            System.out.println("After session.save");

            // Commit the transaction
            session.getTransaction().commit();
            System.out.println("Message saved to the database.");
        } catch (HibernateException e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }


    }

    public String getManagerID(int communityID) {//added by Ayal
        String managerID = null;
        Transaction transaction = null;
        try {
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<String> criteriaQuery = builder.createQuery(String.class);
            Root<Community> root = criteriaQuery.from(Community.class);

            Predicate predicate = builder.equal(root.get("community_id"), communityID);
            criteriaQuery.select(root.get("manager_id")).where(predicate);

            managerID = session.createQuery(criteriaQuery).getSingleResult();
            System.out.println("Manager ID for Community ID " + communityID + " is: " + managerID);

            transaction.commit();
        } catch (NoResultException e) {
            System.out.println("No manager found for Community ID: " + communityID);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return managerID;
    }


    private void updateTaskByID(int updatedTaskID, String newStatus) {//added by Ayal
        Transaction transaction = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            Session session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Retrieve the task entity by ID
            Task task = session.get(Task.class, updatedTaskID);
            // Update the task status to "done"
            if(newStatus.equals("Done")){
                task.setStatus(TaskStatus.Done);
            }
            if(newStatus.equals("Pending")){
                task.setStatus(TaskStatus.Pending);
            }
            if(newStatus.equals("in process")){
                task.setStatus(TaskStatus.inProcess);
            }if(newStatus.equals("Declined")){
                task.setStatus(TaskStatus.Declined);
            }

            // Commit the transaction
            session.update(task);
            transaction.commit();
            System.out.println("Task with ID " + updatedTaskID + " updated to "+newStatus+".");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }


    public void sendToAllClients(Message message) {
        //System.out.println("send to all clients before try**************************");
        if(subscribersList == null){
            System.out.println("no clients online");
            return;
        }
        try {
            for (SubscribedClient SubscribedClient : subscribersList) {
                //System.out.println("send to all clients inside for loop **************************");
                SubscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * not checked function, all credits for GPT we hope it works!
     ***/
    public List<Task> getAllOverDuePendingTasks() {

        try {
            sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Task> criteriaQuery = builder.createQuery(Task.class);
            Root<Task> rootEntry = criteriaQuery.from(Task.class);
            criteriaQuery.select(rootEntry);

            // Create predicate for status condition
            Predicate statusPredicate = builder.equal(rootEntry.get("status"), "Pending");

            // Create predicate for time condition (tasks created more than 10 seconds ago)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tenSecondsAgo = now.minusSeconds(10);
            Predicate timePredicate = builder.lessThan(rootEntry.get("time"), tenSecondsAgo);

            // Combine predicates with "and" to create the final condition
            Predicate combinedPredicate = builder.and(statusPredicate, timePredicate);

            criteriaQuery.where(combinedPredicate);

            TypedQuery<Task> allQuery = session.createQuery(criteriaQuery);
            List<Task> tasks = allQuery.getResultList();

            // Filter tasks further to ensure they are exactly 10 seconds old
            List<Task> overdueTasks = tasks.stream()
                    .filter(task -> {
                        LocalDateTime taskTime = task.getTime();
                        long minutesDifference  = Duration.between(taskTime, now).toMinutes();

                        /**  next condition define which task have been enough time at Pending status
                         *   1440 is how much minutes in 24 hours
                         * */
                        return minutesDifference > 3 && (minutesDifference % 3) < 2;
                    })
                    .collect(Collectors.toList());

            System.out.println("Overdue tasks to check: " + overdueTasks);

            return overdueTasks;
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println(e.getMessage());
        } finally {
            session.close();
        }
        return null;
    }
    public List<Task> getAllOverDueInProcessTasks() {

        try {
            sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Task> criteriaQuery = builder.createQuery(Task.class);
            Root<Task> rootEntry = criteriaQuery.from(Task.class);
            criteriaQuery.select(rootEntry);

            // Create predicate for status condition
            Predicate statusPredicate = builder.equal(rootEntry.get("status"), "in process");

            // Create predicate for time condition (tasks created more than 10 seconds ago)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tenSecondsAgo = now.minusSeconds(10);
            Predicate timePredicate = builder.lessThan(rootEntry.get("time"), tenSecondsAgo);

            // Combine predicates with "and" to create the final condition
            Predicate combinedPredicate = builder.and(statusPredicate, timePredicate);

            criteriaQuery.where(combinedPredicate);

            TypedQuery<Task> allQuery = session.createQuery(criteriaQuery);
            List<Task> tasks = allQuery.getResultList();

            // Filter tasks further to ensure they are exactly 10 seconds old
            List<Task> overdueTasks = tasks.stream()
                    .filter(task -> {
                        LocalDateTime taskTime = task.getTime();
                        long minutesDifference  = Duration.between(taskTime, now).toMinutes();

                        /**  next condition define which task have been enough time at Pending status
                         *   1440 is how much minutes in 24 hours
                         * */
                        return minutesDifference > 3 && (minutesDifference % 3) < 2;
                    })
                    .collect(Collectors.toList());

            System.out.println("Overdue tasks to check: " + overdueTasks);

            return overdueTasks;
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println(e.getMessage());
        } finally {
            session.close();
        }
        return null;
    }


}

