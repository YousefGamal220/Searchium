class SearchResultModel {
  String title;
  String url;
  String description;

  SearchResultModel(this.title, this.url, this.description);
  factory SearchResultModel.fromJson(Map<String, dynamic> jsonFile) =>
      SearchResultModel(
          jsonFile['title'], jsonFile['url'], jsonFile['snippet']);
}
