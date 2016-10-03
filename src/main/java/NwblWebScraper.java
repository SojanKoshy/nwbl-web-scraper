import scraper.WebScraper;
import scraper.gerrit.OnosGerritScraper;

/**
 * Network BL web scraper application
 */
class NwblWebScraper {

    public static void main(String[] args) {
        System.out.println("Huawei Network BL Web Scraper for Open-Source Team");

        WebScraper webScraper = new OnosGerritScraper();
        webScraper.setSearchText("status:open");
        webScraper.scrape();
    }
}

