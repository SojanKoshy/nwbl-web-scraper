package scraper.gerrit;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import java.util.List;

/**
 * Gerrit query page scraper
 */
class QueryPage extends GerritScraper {
    private static final String QUERY_PAGE_PATH = "/#/q/";

    private static final String CHANGE_TABLE_XPATH = "//*[@id=\"gerrit_body\"]/div/div/div/div/table[1]";
    private static final String CHANGE_TABLE_CLASS = "changeTable";

    private static final String PREV_NEXT_LINKS_XPATH = "//*[@id=\"gerrit_body\"]/div/div/div/div/table[2]";
    private static final String PREV_NEXT_LINKS_CLASS = "changeTablePrevNextLinks";

    private static final String NEXT_LINK_XPATH = "//*[@id=\"gerrit_body\"]/div/div/div/div/table[2]/tbody/tr/td[2]/div/a";

    private static final int SUBJECT = 3;
    private static final int STATUS = 4;
    private static final int OWNER = 5;
    private static final int PROJECT = 6;
    private static final int BRANCH = 7;
    private static final int UPDATED = 8;
    private static final int SIZE = 9;
    private static final int CODE_REVIEW_SCORE = 10;
    private static final int MODULE_OWNER_SCORE = 11;

    private static final int EXPECT_CELL_SIZE_IN_ROW = 13;

    private final String baseUrl;
    private final String searchTerm;
    private String nextPageUrl;

    QueryPage(String baseUrl, String searchTerm) {
        this.baseUrl = baseUrl;
        this.searchTerm = searchTerm;
        if (searchTerm == null) {
            this.url = baseUrl;
        } else {
            this.url = baseUrl + QUERY_PAGE_PATH + searchTerm;
        }
    }

    public void scrape() {
        do {
            if (nextPageUrl != null) {
                this.url = nextPageUrl;
                nextPageUrl = null;
            }
            loadPage();
            scrapeChangeTable();
            scrapePrevNextLinks();
        } while (nextPageUrl != null);
    }

    private void scrapeChangeTable() {
        HtmlTable changeTable = page.getFirstByXPath(CHANGE_TABLE_XPATH);

        if (changeTable == null || !changeTable.getAttribute("class").equalsIgnoreCase(CHANGE_TABLE_CLASS)) {
            log.error("Change table is not found in page. Please check the url '{}'", url);
            return;
        }

        HtmlTableBody tableBody = changeTable.getBodies().get(0);

        int rowSize = tableBody.getRows().size();
        log.info("Number of rows in change table are {}", rowSize);

        List<HtmlTableRow> rowList = tableBody.getRows();
        for (int i = 1; i < rowSize; i++) {
            HtmlTableRow row = rowList.get(i);
            List<HtmlTableCell> cellList = row.getCells();
            int cellsInRow = cellList.size();

            if (cellsInRow != EXPECT_CELL_SIZE_IN_ROW) {
                log.error("Number of cells in row {} are {} while expected is {}. Please check the search term '{}'",
                        i, cellsInRow, EXPECT_CELL_SIZE_IN_ROW, searchTerm);
                continue;
            }

            storeSubject(cellList.get(SUBJECT));
            storeStatus(cellList.get(STATUS));
            storeOwner(cellList.get(OWNER));
            storeProject(cellList.get(PROJECT));
            storeBranch(cellList.get(BRANCH));
            storeUpdatedTime(cellList.get(UPDATED));
            storeSize(cellList.get(SIZE));
            storeCodeReviewScore(cellList.get(CODE_REVIEW_SCORE));
            storeModuleOwnerScore(cellList.get(MODULE_OWNER_SCORE));
        }
    }

    private void scrapePrevNextLinks() {
        HtmlTable prevNextLinksTable = page.getFirstByXPath(PREV_NEXT_LINKS_XPATH);

        if (prevNextLinksTable == null ||
                !prevNextLinksTable.getAttribute("class").equalsIgnoreCase(PREV_NEXT_LINKS_CLASS)) {
            log.error("Unable to find Prev Next links in page. Page might not be loaded properly");
            return;
        }

        HtmlAnchor anchor = page.getFirstByXPath(NEXT_LINK_XPATH);
        if (anchor.getEnclosingElement("div").getAttribute("style").equalsIgnoreCase("display: none;")) {
            log.info("No next link present in page");
            return;
        }
        String nextLink = anchor.getHrefAttribute();
        log.info("Next link is {}", nextLink);
        nextPageUrl = baseUrl + '/' + nextLink;
    }

    private void storeSubject(HtmlTableCell cell) {
        if (cell.getFirstChild() instanceof HtmlAnchor) {
            HtmlAnchor anchor = (HtmlAnchor) cell.getFirstChild();
            String changeLink = anchor.getHrefAttribute();
            log.debug("Change link is {}", changeLink);
        }
    }

    private void storeStatus(HtmlTableCell cell) {
        String status = cell.asText();
        log.debug("Status is '{}'", status);
    }

    private void storeOwner(HtmlTableCell cell) {
        String owner = cell.asText();
        log.debug("Owner is '{}'", owner);
    }

    private void storeProject(HtmlTableCell cell) {
        String project = cell.asText();
        log.debug("Project is '{}'", project);
    }

    private void storeBranch(HtmlTableCell cell) {
        String branch = cell.asText();
        log.debug("Branch is '{}'", branch);
    }

    private void storeUpdatedTime(HtmlTableCell cell) {
        String updatedTime = cell.asText();
        log.debug("Updated Time is '{}'", updatedTime);
    }

    private void storeSize(HtmlTableCell cell) {
        String size = cell.getAttribute("title");
        log.debug("Size is '{}'", size);
    }

    private void storeCodeReviewScore(HtmlTableCell cell) {
        String codeReviewScore = cell.asText();
        log.debug("Code review score is '{}'", codeReviewScore);
    }

    private void storeModuleOwnerScore(HtmlTableCell cell) {
        String moduleOwnerScore = cell.asText();
        log.debug("Module owner score is '{}'", moduleOwnerScore);
    }
}
