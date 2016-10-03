package scraper.gerrit;

import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gerrit commit page scraper
 */
class ChangePage extends GerritScraper {
    private static final String CHANGE_PAGE_PATH = "/#/c/";

    private static final String CHANGE_TABLE_XPATH = "//*[@id=\"gerrit_body\"]/div/div/div/div/div/div[3]/table";
    private static final String CHANGE_TABLE_CLASS = "changeTable";

    private static final String PATCH_SETS_XPATH = "//*[@id=\"gerrit_body\"]/div/div/div/div/div/div[1]/div[3]/div[1]/button[2]/div";

    private static final int SIZE = 5;

    private static final int EXPECT_CELL_SIZE_IN_ROW = 7;

    private final String baseUrl;
    private final String branchId;

    private boolean hasNextPage = false;
    private String nextPageUrl;

    public ChangePage(String baseUrl, String branchId) {
        this.baseUrl = baseUrl;
        this.branchId = branchId;
        this.url = baseUrl + CHANGE_PAGE_PATH + branchId;
    }

    public void scrape() {
        do {
            if (hasNextPage) {
                this.url = nextPageUrl;
                hasNextPage = false;
            }
            loadPage();
            scrapeChangeTable();
            scrapePatchSets();
        } while (hasNextPage);
    }

    private void scrapeChangeTable() {
        HtmlTable changeTable = page.getFirstByXPath(CHANGE_TABLE_XPATH);

        if (changeTable == null || !changeTable.getAttribute("class").contains(CHANGE_TABLE_CLASS)) {
            log.error("Change table is not found in page. Please check the url '{}'", url);
            return;
        }

        HtmlTableBody tableBody = changeTable.getBodies().get(0);

        int rowSize = tableBody.getRows().size();
        log.info("Number of rows in change table are {}", rowSize);

        //*[@id="gerrit_body"]/div/div/div/div/div/div[3]/table/tbody/tr[7]/th[4]

        List<HtmlTableRow> rowList = tableBody.getRows();
        List<HtmlTableCell> cellList = rowList.get(rowSize - 1).getCells();
        int cellsInRow = cellList.size();

        if (cellsInRow != EXPECT_CELL_SIZE_IN_ROW) {
            log.error("Number of cells in row are {} while expected is {}. Please check the url '{}'",
                    cellsInRow, EXPECT_CELL_SIZE_IN_ROW, url);
            return;
        }
        storeSize(cellList.get(SIZE));
    }

    private void scrapePatchSets() {
        HtmlDivision patchSetsDivision = page.getFirstByXPath(PATCH_SETS_XPATH);

        if (patchSetsDivision == null) {
            log.error("Unable to find Patch Sets in page. Page might not be loaded properly");
            return;
        }

        String patchSets = patchSetsDivision.asText();
        log.info("Patch sets is '{}'", patchSets);

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(patchSets);
        if(matcher.find()) {
            Integer currentPatch = Integer.valueOf(matcher.group(0));

            if (currentPatch != 1) {
                Integer nextPatch = currentPatch - 1;
                hasNextPage = true;
                nextPageUrl = baseUrl + CHANGE_PAGE_PATH + branchId + '/' + nextPatch.toString();
            }
        } else {
            log.error("Unable to parse the next patch number from '{}'", patchSets);
        }
    }

    private void storeSize(HtmlTableCell cell) {
        String size = cell.asText();
        log.info("Size is '{}'", size);
    }


}
