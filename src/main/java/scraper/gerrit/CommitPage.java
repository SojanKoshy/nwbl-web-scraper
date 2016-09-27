package scraper.gerrit;

/**
 * Gerrit commit page scraper
 */
class CommitPage extends GerritScraper {
    private static final String COMMIT_PATH = "/#/c/";

    public CommitPage(String url, Integer branchId, Integer patchSet) {

        if (patchSet == null) {
            this.url = url + COMMIT_PATH + branchId.toString();
        } else {
            this.url = url + COMMIT_PATH + branchId.toString() + patchSet.toString();
        }
    }
}
