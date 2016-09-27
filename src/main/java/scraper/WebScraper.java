package scraper;

/**
 * Service interface exported by web scraper
 */
public interface WebScraper {

    void scrape();

    void setSearchText(String searchText);
}
