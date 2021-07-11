import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'dart:io';

class WebViewWidget extends StatefulWidget {
  //const WebViewWidget({ Key? key }) : super(key: key);
  final String url;
  WebViewWidget(this.url);

  @override
  _WebViewWidgetState createState() => _WebViewWidgetState(url);
}

class _WebViewWidgetState extends State<WebViewWidget> {
  String _url;
  _WebViewWidgetState(this._url);
  @override
  void initState() {
    super.initState();
    // Enable hybrid composition.
    if (Platform.isAndroid) WebView.platform = SurfaceAndroidWebView();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Webpage"),
      ),
      body: SafeArea(
        child: WebView(
          initialUrl: _url,
          gestureNavigationEnabled: false,
          debuggingEnabled: false,
        ),
      ),
    );
  }
}
