package org.webScraper;

import java.io.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Main {
    public static void main(String[] args) {

        try {
            String websiteUrl = "https://www.decathlon.pt/search?Ntt=jacuzzi";
            String userAgent = getUserAgent();
            Document scrap = Jsoup.connect(websiteUrl).userAgent(userAgent).get();

            // Three scrapings are made because we can't get the price and image links in the first one
            // getting all the info from each product (except price)
            Elements productsBlock = scrap.select(".vtmn-w-full.vtmn-flex.vtmn-flex-col.vtmn-items-center");
            //getting the product prices (in the same order as the products above)
            Elements priceDiv = scrap.getElementsByClass("prc__cartridge");

            // Using Selenium to capture the image links
            Elements imgLinks = getImgLinks(websiteUrl);

            // Initializes the file where the detections will be saved
            String filePath = "./src/main/java/org/example/detections.csv";
            FileWriter csvFile = new FileWriter(filePath, true);
            PrintWriter detectionsFile = new PrintWriter(csvFile);

            // Writing the header on the file (if it was not written before)
            if (!isHeaderPrinted(filePath)) {
                detectionsFile.printf("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n","detectionDate","productTitle","productDescription","productPrice","productCurrency","prodImageUrl","productUrl","paidSearch", "orderOnPage", "productSeller");
            }

            // Getting the scrapping date
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            // Limit to the first 10 results
            int MaxSize = (Math.min(productsBlock.size(), 10));

            // Getting the info
            for (int i = 0; i < MaxSize; i++) {

                String productTitle = productsBlock.get(i).select(".vtmn-p-0.vtmn-m-0.vtmn-text-sm.vtmn-font-normal.vtmn-overflow-hidden.vtmn-text-ellipsis.svelte-1l3biyf").text();

                Element prodURL = productsBlock.get(i).select("a").first();
                String productUrl = prodURL.attr("abs:href");

                String productDescription = getProductDescription(productUrl);

                String prodImageUrl = getProductImageUrl(imgLinks.get(i));

                // Get the currency and the price
                ArrayList<String> prodPriceCurrency = new ArrayList<>(getPriceCurrency(priceDiv.get(i)));
                Double productPrice = Double.parseDouble(prodPriceCurrency.get(1));
                String currency = prodPriceCurrency.get(0);

                int orderOnPage = i + 1;

                // In this page there isn't any paid search
                String paidSearch = "No";

                String productSeller = productsBlock.get(i).getElementsByClass("svelte-ht6pwr").text();
                productSeller = productSeller.replace("Vendido e expedido por ","");

                LocalDateTime today = LocalDateTime.now();
                String todayDate = dtf.format(today);

                //writing in the file
                detectionsFile.printf("%s\t%s\t%s\t%.2f\t%s\t%s\t%s\t%s\t%d\t%s\n",todayDate,productTitle,productDescription,productPrice,
                        currency, prodImageUrl,productUrl,paidSearch, orderOnPage, productSeller);

                // variable to show on screen the progress of the process
                double progressStatus = (i+1)/(float)MaxSize*100;
                System.out.printf("Scraping... %.1f %% complete\n", progressStatus);
            }

            detectionsFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUserAgent() {
        //  Returns a random user agent

        ArrayList<String> userAgentsList = new ArrayList<>();

        // Get user agents list from an external file
        try {
            String filePath = "./src/main/java/org/example/userAgents.txt";
            File extUserAgentLst = new File(filePath);
            Scanner usrAgentReader = new Scanner(extUserAgentLst);
            while (usrAgentReader.hasNextLine()) {
                String userAgent = usrAgentReader.nextLine();
                userAgentsList.add(userAgent);
            }
            usrAgentReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }

        // returns a random user agent from the list
        int randomIndex = ThreadLocalRandom.current().nextInt(0, userAgentsList.size());
        return userAgentsList.get(randomIndex);
    }

    public static boolean isHeaderPrinted(String filepath) {

        boolean hasHeader = false;

        try {
            File csv = new File(filepath);
            Scanner detections = new Scanner(csv);

            if (detections.hasNextLine()) {
                hasHeader = true;
            }

            detections.close();

        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        return (hasHeader);

    }

    public static Elements getImgLinks(String websiteUrl) {

        // Using Selenium
        System.setProperty("webdriver.gecko.driver", "geckodriver");
        WebDriver driver = new FirefoxDriver();
        driver.get(websiteUrl);
        driver.manage().window().minimize();
        String linksSource = driver.getPageSource();

        // parsing image links to treat them with Jsoup
        Document parsedHTML = Jsoup.parse(linksSource);

        return parsedHTML.select(".svelte-w1lrdd");
    }

    public static String getProductDescription(String url) {

        String prodDescription = "";

        try {
            String userAgent = getUserAgent();
            Document scrap = Jsoup.connect(url).userAgent(userAgent).get();
            prodDescription = scrap.select(".svelte-1uw9j0x").text();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (prodDescription.length() == 0) {
            prodDescription = null;
        }

        return prodDescription;
    }

    public static String getProductImageUrl(Element imgElement) {

        String imgLink = imgElement.attr("abs:src");

        // In some products, the image link is stored in another attribute than "src"
        if (imgLink.startsWith("data")) {
            imgLink = imgElement.attr("abs:data-src");
        }

        return imgLink;
    }

    public static ArrayList getPriceCurrency(Element priceDiv) {
        ArrayList<String> priceCurrency = new ArrayList<>();

        String prodPrice = priceDiv.select(".prc__active-price.svelte-t8m03u").text();
        String currency = String.valueOf(prodPrice.charAt(prodPrice.length()-1));
        String productPrice = prodPrice.replace(currency, "").replace(",", ".");

        priceCurrency.add(currency);
        priceCurrency.add(productPrice);

        return priceCurrency;

    }
}