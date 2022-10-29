## Web Scrapper
### Description

**Please note: This version does not run in docker. The scraper version in this branch is abandoned.**

This program scrapes the [decathlon.pt](https://www.decathlon.pt/) website for "Jacuzzi".
The data is stored in a *detections.csv* file.

**The fields sraped are:**
- product name
- product description
- product price
- product currency
- product image url
- paid search (yes/no)
- order on the page
- product seller

Additionally, every detection contains the date and the time of the scraping.
 
### Libraries

The scraping was done using *Jsoup* and *Selenium*.