package scraper.gerrit;

/**
 * Gerrit commit page scraper
 */
class ChangePage extends GerritScraper {
    private static final String CHANGE_PAGE_PATH = "/#/c/";

    public ChangePage(String url, Integer branchId, Integer patchSet) {

        if (patchSet == null) {
            this.url = url + CHANGE_PAGE_PATH + branchId.toString();
        } else {
            this.url = url + CHANGE_PAGE_PATH + branchId.toString() + patchSet.toString();
        }
    }
}
