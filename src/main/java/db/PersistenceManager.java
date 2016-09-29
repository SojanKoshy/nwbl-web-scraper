package db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
public enum PersistenceManager {
    INSTANCE;
    private final EntityManagerFactory emFactory;
    PersistenceManager() {
        // "jpa-example" was the value of the name attribute of the
        // persistence-unit element.
        emFactory = Persistence.createEntityManagerFactory("jpa-example");
    }
    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }
    public void close() {
        emFactory.close();
    }
}