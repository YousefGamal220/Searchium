import 'package:searchium/Models/Docs.dart';

class SearchModel {
  List<Docs> docs = <Docs>[];
  int totalPages;
  int currentPage;
  SearchModel(List<Docs> docs, int totalPages, int currentPage) {
    this.docs.addAll(docs);
    this.totalPages = totalPages;
    this.currentPage = currentPage;
  }
  //factory SearchModel.fromJson(Map<String, dynamic> jsonFile) =>
}
