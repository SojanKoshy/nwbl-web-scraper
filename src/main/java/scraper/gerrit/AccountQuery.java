package scraper.gerrit;


import db.GerritAccount;
import db.PersistenceManager;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Queries Gerrit accounts and stores in local db
 */
class AccountQuery extends GerritScraper {

    private static final String ACCOUNT_QUERY_PATH = "/accounts/?q=name:";

    private static final Integer MAX_QUERY_SIZE = 100;
    private static final String DOWNLOAD_FILE = "/tmp/download.txt";
    private static final String JSON_FILE = "/tmp/download.json";

    private final String baseUrl;
    private EntityManager em;
    private GerritAccount gerritAccount;

    AccountQuery(String baseUrl) {
        this.baseUrl = baseUrl;

    }

    public void scrape() {
        em = PersistenceManager.INSTANCE.getEntityManager();
        em.getTransaction().begin();
        for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            this.url = baseUrl + ACCOUNT_QUERY_PATH + alphabet + "&n=" + MAX_QUERY_SIZE.toString();
            download();
            scrapeAccountDetails();
        }
        em.getTransaction().commit();
        em.close();
        PersistenceManager.INSTANCE.close();
    }

    private void download() {
        URL link = null;

        log.info("Downloading account info from link {}", url);
        try {
            link = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.copyURLToFile(link, new File(DOWNLOAD_FILE));

            BufferedReader reader = new BufferedReader(new FileReader(DOWNLOAD_FILE));
            BufferedWriter writer = new BufferedWriter(new FileWriter(JSON_FILE));

            String lineToRemove = ")]}'";
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if (trimmedLine.equals(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scrapeAccountDetails() {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) parser.parse(new FileReader(JSON_FILE));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        if (jsonArray.size() == MAX_QUERY_SIZE) {
            log.warn("Possible loss of account info as max size {} is reached ", MAX_QUERY_SIZE);
        } else {
            log.info("Number of accounts found are {}", jsonArray.size());
        }
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;

            Integer id = ((Long) jsonObject.get("_account_id")).intValue();
            String name = (String) jsonObject.get("name");
            String email = (String) jsonObject.get("email");
            String username = (String) jsonObject.get("username");

            log.debug("_account_id : {}", id);
            log.debug("name : {}", name);
            log.debug("email : {}", email);
            log.debug("username : {}", username);

            gerritAccount = new GerritAccount();
            gerritAccount.setId(id);
            gerritAccount.setName(name);
            gerritAccount.setEmail(email);
            gerritAccount.setUsername(username);

            String huawei = "huawei";
            if(name.toLowerCase().contains(huawei)
                    || (email != null && email.toLowerCase().contains(huawei))
                    || (username != null && username.toLowerCase().contains(huawei))) {
                gerritAccount.setCompany(huawei);
            }
            em.merge(gerritAccount);
        }
    }

}

