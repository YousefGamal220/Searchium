const express = require('express');
const CrawledPage = require('./models/CrawledPage');
const Word = require('./models/Word');

const app = express();

const PORT = process.env.PORT || 8080;

require('dotenv').config();
require('./mongo');

app.get("/", (req, res) => {
    res.send("Hello world!");
});

app.get("/search", paginatedResults(), async (req, res) => {
    res.status(200).json(res.paginatedResults);
});

function paginatedResults() {
    return async (req, res, next) => {
        const word = req.query.q;
        const page = parseInt(req.query.p);
        const limit = 10;

        const startIndex = (page - 1) * limit;
        const endIndex = page * limit;

        const results = {};

        try {
            const { pages } = await Word.findOne({ word }).select('pages -_id');
            const slicedPages = pages.slice(startIndex, endIndex);
            let index = 0;
            for (let page of slicedPages) {
                const pageDetails = await CrawledPage.findOne({ url: page.url }).select('title content -_id');
                slicedPages[index++] = { ...page, pageDetails };
            }
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