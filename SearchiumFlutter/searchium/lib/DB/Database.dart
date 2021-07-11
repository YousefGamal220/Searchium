import 'package:mongo_dart/mongo_dart.dart' show Db, DbCollection;

class DBConnection {
  static DBConnection _instance;

  final String _host = "127.0.0.1";
  final String _port = "27017";
  final String _dbName = "Searchium";
  Db _db;

  static getInstance() {
    if (_instance == null) {
      _instance = DBConnection();
    }
    return _instance;
  }

  Future<Db> getConnection() async {
    if (_db == null) {
      try {
        _db = Db(_getConnectionString());
        await _db.open();
      } catch (e) {
        print(e);
      }
    }
    return _db;
  }

  _getConnectionString() {
    return "mongodb://0.0.0.0:27017/?compressors=zlib&gssapiServiceName=mongodb ";
  }

  closeConnection() {
    _db.close();
  }
}
