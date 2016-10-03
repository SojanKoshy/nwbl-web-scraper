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
//        GerritScraper gerritScraper = new QueryPage(URL, searchTerm);
//        GerritScraper gerritScraper = new ChangePage(URL, "10903");
//        gerritScraper.scrape();
        GerritScraper gerritScraper = new AccountQuery(URL);
        gerritScraper.scrape();
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchTerm = searchText;
    }
}
