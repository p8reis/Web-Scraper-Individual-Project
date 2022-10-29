## Web Scrapper
### Description

This program scrapes the [decathlon.pt](https://www.decathlon.pt/) website for "Jacuzzi".
The data is stored in a *detections.csv* file.

**The fields scraped are:**
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

The scraping was done using *Jsoup*.

### How to run in docker

1. Open the terminal in the directory where the Dockerfile is;
2. Build the image by running  ```docker build -t image-name:tag .``` in the terminal, where ```image-name```and ```tag```are chosen by you.
3. Run the image by using ```docker run your-computer-path:/tmp/DetectionsOutput image-name:tag``` where ```your-computer-path```is the 
path, in your computer, where you want to save the ```.csv```file with the scraping.