package com.example.searchium;

import io.flutter.embedding.android.FlutterActivity;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "cmp.searchium.dev/searchium";
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
    super.configureFlutterEngine(flutterEngine);
      new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
          .setMethodCallHandler(
            (call, result) -> {
              // Note: this method is invoked on the main thread.
              // TODO
              if (call.method.equals("getBatteryLevel"))
              {
                  int batteryLevel = getBatteryLevel();
                  result.success(batteryLevel);
              }
                if (call.method.equals("stem"))
                {
                    String sentence = call.argument("sentence");
                    result.success(stemSentence(sentence));
                }

            }
          );
    }

    private int getBatteryLevel() {
        int batteryLevel = -1;
        return batteryLevel;
      }

      private String stemSentence(String sentence){
          return (new Stemmer(sentence)).toString();
    }
  
}
