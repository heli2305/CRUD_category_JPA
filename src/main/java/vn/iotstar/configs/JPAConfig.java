package vn.iotstar.configs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class JPAConfig implements ServletContextListener {

    private static final String PERSISTENCE_UNIT = "jpa-hibernate-sql";
    private static volatile EntityManagerFactory factory;

    private static EntityManagerFactory getFactory() {
        if (factory == null || !factory.isOpen()) {
            synchronized (JPAConfig.class) {
                if (factory == null || !factory.isOpen()) {
                    factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
                }
            }
        }
        return factory;
    }

    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }

    public static void shutdown() {
        synchronized (JPAConfig.class) {
            if (factory != null && factory.isOpen()) {
                factory.close();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        shutdown();
    }
}
