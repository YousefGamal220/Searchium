const mongoose = require('mongoose');

const wordSchema = new mongoose.Schema({
    word: String,
    IDF: Number,
    pages: Array
});

module.exports = mongoose.model("Word", wordSchema, "Words");