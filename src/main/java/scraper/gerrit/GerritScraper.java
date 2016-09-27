package scraper.gerrit;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Gerrit home page scraper
 */
class GerritScraper {
    private static final String PAGE_LOADING_XPATH = "/html/body/span";
    private static final String PAGE_LOADING_TEXT = "Working ...";
    private final WebClient webClient = new WebClient(BrowserVersion.CHROME);
    final Logger log = getLogger(getClass());
    String url;
    HtmlPage page;

    public void setUrl(String url) {
        this.url = url;
    }

    void loadPage() {
        try {
            log.info("Loading page {}", url);
            long lStartTime = new Date().getTime();
            page = webClient.getPage(url);
            waitForPageToLoad(page);
            long lEndTime = new Date().getTime();
            long elapsedTime = (lEndTime - lStartTime) / 1000;

            if (log.isDebugEnabled()) {
                log.debug("Page as xml\n{}", page.asXml());
                log.debug("Page as text\n{}", page.asText());
            }

            log.info("Page loaded in {} sec", elapsedTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForPageToLoad(final HtmlPage page) {
        log.info("Page title is '{}'", page.getTitleText());

        int tries = 5;
        while (isLoading(page) && tries > 0) {
            tries--;
            synchronized (page) {
                try {
                    page.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isLoading(final HtmlPage page) {
        String loadingStatus = ((HtmlSpan) page.getByXPath(PAGE_LOADING_XPATH).get(0)).asText();
        if (!loadingStatus.isEmpty()) {
            log.info("Page loading status is '{}'", loadingStatus);
        }
        return loadingStatus.equalsIgnoreCase(PAGE_LOADING_TEXT);
    }

    public void scrape() {
        log.info("Page as xml\n{}", page.asXml());
        log.info("Page as text\n{}", page.asText());
    }
}
