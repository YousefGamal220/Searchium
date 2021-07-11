const express = require('express');
const CrawledPage = require('./models/CrawledPage');
const Word = require('./models/Word');

const app = express();

const PORT = process.env.PORT || 3000;

require('dotenv').config();
require('./mongo');

app.get("/", (req, res) => {
    res.send("Hello world!");
});

app.get("/search", paginatedResults(), async (req, res) => {
    console.log("Here");
    res.header("Content-Type", 'application/json');
    res.status(200).send(JSON.stringify(res.paginatedResults, null, 1));
});

function paginatedResults() {
    return async (req, res, next) => {
        console.log("Reached ")
        const word = req.query.q;
        const page = parseInt(req.query.p);
        const limit = 10;

        const startIndex = (page - 1) * limit;
        const endIndex = page * limit;

        const results = { word };

        try {
            const { IDF, pages } = await Word.findOne({ word }).select('IDF pages -_id');
            const slicedPages = pages.slice(startIndex, endIndex);
            slicedPages.sort((a, b) => { return (b.tf * IDF) - (a.tf * IDF) });
            let index = 0;
            for (let page of slicedPages) {
                const { title, content } = await CrawledPage.findOne({ url: page.url }).select('title content -_id');
                delete page.tf;
                slicedPages[index++] = { ...page, title, content };
            }
            slicedPages.forEach(page => {
                const wordIndex = page.content.toLowerCase().indexOf(word);
                let startIndex = wordIndex;
                let endIndex = wordIndex;
                while (page.content[startIndex] != ". " && (page.content[startIndex] != " " || wordIndex - startIndex < 100)) startIndex--;
                while (page.content[endIndex] != ". " && (page.content[endIndex] != " " || endIndex - wordIndex < word.length + 100)) endIndex++;
                page.snippet = page.content.slice(startIndex + 1, endIndex);
                delete page.content;
            });

            if (endIndex < pages.length) {
                results.next = page + 1;
            }

            results.current = page;

            if (startIndex > 0) {
                results.previous = page - 1;
            }

            results.totalPages = pages.length;
            results.pages = slicedPages;

            res.paginatedResults = results;
            console.log(results);
            next();
        } catch (e) {
            res.status(500).json({ message: e.message });
        }
    }
}

app.listen(PORT, err => {
    if (err) return console.error(err);
    console.log(`Server started listening at port ${PORT}`);
});