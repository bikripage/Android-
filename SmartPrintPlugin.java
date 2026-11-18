// Reference Capacitor plugin. Update package declaration to match generated Android package.
package com.bikripage.smartprint;

import android.content.Context;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.print.PrintDocumentAdapter;
import android.print.PrintAttributes;
import android.graphics.Color;
import android.os.Bundle;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "SmartPrint")
public class SmartPrintPlugin extends Plugin {
  @PluginMethod
  public void printImage(PluginCall call) {
    String dataUrl = call.getString("dataUrl");
    String fileName = call.getString("fileName", "Smart Print document");
    if (dataUrl == null || !dataUrl.startsWith("data:image/")) {
      call.reject("This reference bridge currently supports image files only.");
      return;
    }
    Context context = getContext();
    PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
    WebView webView = new WebView(context);
    String html = "<html><body style='margin:0'><img style='width:100%;height:auto' src='" + dataUrl + "'></body></html>";
    webView.setBackgroundColor(Color.WHITE);
    webView.setWebViewClient(new WebViewClient() {
      @Override public void onPageFinished(WebView view, String url) {
        PrintDocumentAdapter adapter = view.createPrintDocumentAdapter(fileName);
        PrintAttributes attributes = new PrintAttributes.Builder()
          .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
          .setResolution(new PrintAttributes.Resolution("smartprint", "Smart Print", 300, 300))
          .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
          .build();
        printManager.print(fileName, adapter, attributes);
        call.resolve();
      }
    });
    webView.loadDataWithBaseURL("https://localhost/", html, "text/html", "UTF-8", null);
  }
}
