import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:searchium/Models/SearchResultModel.dart';
import 'package:searchium/Views/WebViewWidget.dart';
import 'package:pagination/pagination.dart';
import 'package:http/http.dart' as http;

class SearchResults extends StatefulWidget {
  //const SearchResults({ Key? key }) : super(key: key);
  final String searchString;
  SearchResults(this.searchString);

  @override
  _SearchResultsState createState() => _SearchResultsState(searchString);
}

class _SearchResultsState extends State<SearchResults> {
  String _searchString;
  _SearchResultsState(this._searchString);
  final searchController = TextEditingController();
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    searchController.text = _searchString;
  }

  static const platform = const MethodChannel('cmp.searchium.dev/searchium');
  Future<String> stem(String sentence) async {
    try {
      final String result =
          await platform.invokeMethod('stem', {"sentence": sentence});
      return result;
    } on PlatformException catch (e) {
      return sentence;
    }
  }

  Future<List<SearchResultModel>> fetchData(String sentence, int offset) async {
    List<SearchResultModel> searchResults = <SearchResultModel>[];
    print("offset = $offset");
    try {
      print(sentence);
      http.Response response = await http.get(Uri.parse(
          "http://10.0.2.2:3000/search?q=$sentence&p=${(offset / 10) + 1}"));
      Map<String, dynamic> body = json.decode(response.body);
      print(body);
      List<dynamic> pages = body['pages'];
      print(pages);
      pages.forEach((element) {
        searchResults.add(SearchResultModel.fromJson(element));
      });
    } catch (e) {
      print(e);
    }

    return searchResults;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Container(
          height: 40,
          child: TextField(
            controller: searchController,
            decoration: new InputDecoration(
                border: new OutlineInputBorder(
                  borderRadius: const BorderRadius.all(
                    const Radius.circular(10.0),
                  ),
                ),
                filled: true,
                hintStyle: new TextStyle(color: Colors.grey[800]),
                hintText: "Search",
                suffix: IconButton(
                  icon: Icon(Icons.search),
                  onPressed: () {},
                ),
                fillColor: Colors.white70),
          ),
        ),
        backgroundColor: Colors.white,
      ),
      body: Column(
        children: [Expanded(child: _searchResultsPagination(context))],
      ),
    );
  }

  Widget _resultItem(
      BuildContext context, SearchResultModel searchResultModel) {
    List<TextSpan> textSpans = <TextSpan>[];
    try {
      searchResultModel.description.split(" ").forEach((element) {
        if (searchController.text.contains(element)) {
          textSpans.add(TextSpan(
              text: element,
              style:
                  TextStyle(fontWeight: FontWeight.bold, color: Colors.black)));
        } else
          textSpans.add(
              TextSpan(text: element, style: TextStyle(color: Colors.black)));
        textSpans
            .add(TextSpan(text: " ", style: TextStyle(color: Colors.black)));
      });
    } catch (e) {
      print(e);
      print("Here");
    }
    print(textSpans);
    return ListTile(
      title: Align(
        alignment: Alignment.topLeft,
        child: TextButton(
          child: Text(
            "${searchResultModel.title}",
            style: TextStyle(
              color: Colors.blue[900],
            ),
          ),
          onPressed: () {
            Navigator.push(
                context,
                MaterialPageRoute(
                    builder: (context) =>
                        WebViewWidget(searchResultModel.url)));
          },
        ),
      ),
      subtitle: Align(
        alignment: Alignment.centerLeft,
        child: RichText(
          text: TextSpan(text: searchResultModel.title, children: textSpans),
        ),
      ),
    );
  }

  Widget _searchResultsPagination(BuildContext context) {
    SearchResultModel searchResultModel = SearchResultModel(
        "Google",
        "https://www.google.com",
        "Search the world's information, including webpages, images, videos and more. Google has many special features to help you find exactly what you're looking ...");
    return PaginationList<SearchResultModel>(
        pageFetch: (offset) => fetchData(_searchString, offset),
        onEmpty: Center(
          child: Text('Empty List'),
        ),
        //pageFetch: pageFetch,
        onError: (dynamic error) => Center(
              child: Text('Something Went Wrong'),
            ),
        separatorWidget: Container(
          height: 0.5,
          color: Colors.black,
        ),
        itemBuilder: (context, index) => _resultItem(context, index));
  }

  Future<List<SearchResultModel>> pageFetch(int offset) {
    final List<SearchResultModel> upcommingList = <SearchResultModel>[];
  }

  Widget _searchResults(BuildContext context) {
    SearchResultModel searchResultModel = SearchResultModel(
        "Google",
        "https://www.google.com",
        "Search the world's information, including webpages, images, videos and more. Google has many special features to help you find exactly what you're looking ...");
    return ListView.builder(
        itemCount: 10,
        itemBuilder: (context, index) =>
            _resultItem(context, searchResultModel));
  }
}
