package scraper.gerrit;

import scraper.WebScraper;

/**
 * Web scraper for ONOS gerrit
 */
public class OnosGerritScraper implements WebScraper {
    private static final String URL = "https://gerrit.onosproject.org";
    private String searchTerm;

    @Override
    public void scrape() {
        GerritScraper scraper = new QueryPage(URL, searchTerm);
        scraper.loadPage();
        scraper.scrape();
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchTerm = searchText;
    }
}
