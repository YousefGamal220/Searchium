const mongoose = require('mongoose');

const crawledPageSchema = new mongoose.Schema({
    id: Number,
    title: String,
    url: String,
    content: String,
});

module.exports = mongoose.model("CrawledPage", crawledPageSchema, "CrawledPages");