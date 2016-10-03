package scraper.gerrit;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import db.GerritChange;
import db.PersistenceManager;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private boolean hasNextPage = false;
    private String nextPageUrl;
    private EntityManager em;

    private GerritChange gerritChange;

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
        em = PersistenceManager.INSTANCE.getEntityManager();
        em.getTransaction().begin();
        do {
            if (hasNextPage) {
                this.url = nextPageUrl;
                hasNextPage = false;
            }
            loadPage();
            scrapeChangeTable();
            scrapePrevNextLinks();
        } while (hasNextPage);
        em.getTransaction().commit();
        em.close();
        PersistenceManager.INSTANCE.close();
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

            gerritChange = new GerritChange();
            storeSubject(cellList.get(SUBJECT));
            storeStatus(cellList.get(STATUS));
            storeOwner(cellList.get(OWNER));
            storeProject(cellList.get(PROJECT));
            storeBranch(cellList.get(BRANCH));
            storeUpdatedTime(cellList.get(UPDATED));
            storeSize(cellList.get(SIZE));
            storeCodeReviewScore(cellList.get(CODE_REVIEW_SCORE));
            storeModuleOwnerScore(cellList.get(MODULE_OWNER_SCORE));
            em.merge(gerritChange);
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

        hasNextPage = true;
        String nextLink = anchor.getHrefAttribute();
        log.info("Next link is {}", nextLink);
        nextPageUrl = baseUrl + '/' + nextLink;
    }

    private void storeSubject(HtmlTableCell cell) {
        if (cell.getFirstChild() instanceof HtmlAnchor) {
            HtmlAnchor anchor = (HtmlAnchor) cell.getFirstChild();
            String link = anchor.getHrefAttribute();
            String subject = cell.asText();
            log.debug("Change subject is {}", subject);
            log.debug("Change link is {}", link);
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(link);
            if (matcher.find()) {
                Integer id = Integer.valueOf(matcher.group(0));
                gerritChange.setId(id);
            } else {
                log.error("Unable to parse id from link '{}'", link);
            }
            gerritChange.setSubject(subject);
            gerritChange.setLink(link);
        }
    }

    private void storeStatus(HtmlTableCell cell) {
        String status = cell.asText();
        log.debug("Status is '{}'", status);
        gerritChange.setStatus(status);
    }

    private void storeOwner(HtmlTableCell cell) {
        String owner = cell.asText();
        log.debug("Owner is '{}'", owner);
        gerritChange.setOwner(owner);
    }

    private void storeProject(HtmlTableCell cell) {
        String project = cell.asText();
        log.debug("Project is '{}'", project);
        gerritChange.setProject(project);
    }

    private void storeBranch(HtmlTableCell cell) {
        String branch = cell.asText();
        log.debug("Branch is '{}'", branch);
        gerritChange.setBranch(branch);
    }

    private void storeUpdatedTime(HtmlTableCell cell) {
        String updatedOn = cell.asText();
        log.debug("Updated Time is '{}'", updatedOn);

        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("\\S+ \\d+, \\d+"); //Mar 31, 2016
        matcher = pattern.matcher(updatedOn);
        if (!matcher.find()) {
            pattern = Pattern.compile("\\S+ \\d+"); //Aug 30
            matcher = pattern.matcher(updatedOn);

            if (matcher.find()) {
                updatedOn += new SimpleDateFormat(", yyyy").format(Calendar.getInstance().getTime());
            } else { //6:37 AM
                updatedOn = new SimpleDateFormat("MMM dd, yyyy").format(Calendar.getInstance().getTime());
            }
        }
        java.util.Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            date = sdf.parse(updatedOn);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date sqlDate = null;
        if (date == null) {
            log.error("Unable to parse the updatedOn {} in format 'MMM dd, yyyy'", updatedOn);
        } else {
            sqlDate = new Date(date.getTime());
            log.debug("Updated SQL Time is '{}'", sqlDate.toString());
            gerritChange.setUpdatedOn(sqlDate);
        }
    }

    private void storeSize(HtmlTableCell cell) {
        String size = cell.getAttribute("title");

        log.debug("Size is '{}'", size);
        gerritChange.setSize(size);
        Pattern pattern = Pattern.compile("(\\d+).*(\\d+)");
        Matcher matcher = pattern.matcher(size);
        if (matcher.find()) {
            Integer addedSize = Integer.valueOf(matcher.group(1));
            Integer deletedSize = Integer.valueOf(matcher.group(2));

            log.debug("AddedSize is '{}'", addedSize);
            log.debug("DeletedSize is '{}'", deletedSize);
            gerritChange.setAddedSize(addedSize);
            gerritChange.setDeletedSize(deletedSize);
        } else {
            log.error("Size '{}' is not matching the pattern '{}'", size, pattern.pattern());
        }
    }

    private void storeCodeReviewScore(HtmlTableCell cell) {
        String codeReviewScore = cell.asText();
        log.debug("Code review score is '{}'", codeReviewScore);
        gerritChange.setCodeReviewScore(codeReviewScore);
    }

    private void storeModuleOwnerScore(HtmlTableCell cell) {
        String moduleOwnerScore = cell.asText();
        log.debug("Module owner score is '{}'", moduleOwnerScore);
        gerritChange.setModuleOwnerScore(moduleOwnerScore);
    }
}
