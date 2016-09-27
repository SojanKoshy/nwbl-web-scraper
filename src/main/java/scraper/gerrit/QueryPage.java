package scraper.gerrit;

import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;

/**
 * Gerrit query page scraper
 */
class QueryPage extends GerritScraper {
    private static final String QUERY_PATH = "/#/q/";

    QueryPage(String url, String searchTerm) {
        if (searchTerm == null) {
            this.url = url;
        } else {
            this.url = url + QUERY_PATH + searchTerm;
        }
    }

    public void scrape() {

        final HtmlTableBody body = (HtmlTableBody) page.getByXPath("//*[@id=\"gerrit_body\"]/div/div/div/div/table[1]/tbody").get(0);
        log.info("Rows size: {}", body.getRows().size());

        final HtmlTableDataCell cell = (HtmlTableDataCell) page.getByXPath("//*[@id=\"gerrit_body\"]/div/div/div/div/table[1]/tbody/tr[5]/td[10]").get(0);
        log.info("Cell contents: {}", cell.toString());
    }
}
