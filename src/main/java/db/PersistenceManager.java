package db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum PersistenceManager {
    INSTANCE;
    private final EntityManagerFactory emFactory;

    PersistenceManager() {
        // "web-scraper" is the value of the name attribute of the
        // persistence-unit element.
        emFactory = Persistence.createEntityManagerFactory("web-scraper");
    }

    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    public void close() {
        emFactory.close();
    }
}