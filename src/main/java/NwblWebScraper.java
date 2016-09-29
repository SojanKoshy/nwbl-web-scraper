import db.Address;
import db.PersistenceManager;
import scraper.WebScraper;
import scraper.gerrit.OnosGerritScraper;
import javax.persistence.EntityManager;
/**
 * Network BL web scraper application
 */
class NwblWebScraper {

    public static void main(String[] args) {
        System.out.println("Huawei Network BL Web Scraper for Open-Source Team");

        WebScraper webScraper = new OnosGerritScraper();
        webScraper.setSearchText("status:open");
        //webScraper.scrape();



        Address address = new Address();
        address.setCity("Dhaka")
                .setCountry("Bangladesh1")
                .setPostcode("1000")
                .setStreet("Poribagh");

        EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
        em.getTransaction()
                .begin();
        em.persist(address);
        em.getTransaction()
                .commit();
        em.close();
        PersistenceManager.INSTANCE.close();

    }
}

