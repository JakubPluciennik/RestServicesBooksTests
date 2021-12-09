package pl.sggw;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
  public static void main(String[] args) throws IOException {
    URL url = new URL("http://localhost:8080");
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");

    Map<String, String> parameters = new HashMap<>();
    parameters.put("łźż", "óć");

    con.setDoOutput(true);
    DataOutputStream out = new DataOutputStream(con.getOutputStream());
    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
    out.flush();
    out.close();

    int status = con.getResponseCode();
    BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();

    con.disconnect();
    System.out.println("content: " + content);
  }

  public static class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params)
        throws UnsupportedEncodingException {
      StringBuilder result = new StringBuilder();

      for (Map.Entry<String, String> entry : params.entrySet()) {
        result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
        result.append("=");
        result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        result.append("&");
      }

      String resultString = result.toString();
      return resultString.length() > 0
          ? resultString.substring(0, resultString.length() - 1)
          : resultString;
    }
  }
}
