import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Alex on 07.05.2016.
 */
public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        ResolvePath pathCsv = new ResolvePath();
        pathCsv.cliHandler(args);

        List<String> urlList = getListUrl(pathCsv.getPathCsvFile());

        Map<String, Integer> response = getResponseCode(urlList, pathCsv.getCountThreads());

        writeToCsv(response, pathCsv.getPathCsvFileResp());

    }

    private static void writeToCsv(Map<String, Integer> response, String pathToCvsFile) throws IOException {
        File csvFile = new File(pathToCvsFile);
        FileWriter writer = new FileWriter(csvFile);
        writer.append("Url");
        writer.append(';');
        writer.append("StatusCode");
        writer.append('\n');

        for (Map.Entry<String, Integer> entry : response.entrySet()) {
            writer.append(entry.getKey());
            writer.append(';');
            writer.append(entry.getValue().toString());
            writer.append('\n');
            writer.flush();
        }
        writer.close();
    }

    private static Map<String, Integer> getResponseCode(List<String> urlList, Integer threads) throws IOException, InterruptedException {
        final Map<String, Integer> response = Collections.synchronizedMap(new LinkedHashMap<>());
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        System.out.println("Created " + threads + " threads!");
        for (final String url : urlList) {
            executor.submit(() -> {
                System.out.println("Start work with: " + url.toLowerCase() + " " + Thread.currentThread());

                response.put(url, getStatusCode(url));
                System.out.println("Put result in response: " + url.toLowerCase() + " " + Thread.currentThread());
            });
        }
        while (((ThreadPoolExecutor) executor).getActiveCount() > 0) {
            System.out.println("Checking... " + "Count working threads:  " + ((ThreadPoolExecutor) executor).getActiveCount());
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        executor.shutdown();
        return response;
    }

    private static Integer getStatusCode(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.35 (KHTML, like Gecko) Chrome/49.0.2661.94 Safari/537.34");
            con.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
            con.setRequestProperty("Accept-Encoding", " gzip, deflate, sdch");
            con.setInstanceFollowRedirects(true);
            con.setReadTimeout(20000);

            con.connect();
            con.getInputStream();
            if (con.getResponseCode() != 301 && con.getResponseCode() != 302) {
                return con.getResponseCode();
            } else {
                String redirectUrl = con.getHeaderField("Location");
                return getStatusCode(redirectUrl);
            }
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            return 404;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    private static List<String> getListUrl(String pathCsvFile) throws IOException {
        List<String> list = new ArrayList<>();
        File csvFile = new File(pathCsvFile);
        CSVParser parser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.RFC4180);
        for (CSVRecord csvRecord : parser) {
            String url = csvRecord.iterator().next().split(";")[0];
            if (url.startsWith("http")) {
                list.add(url);
            }
        }
        System.out.println("Created list urls!");
        return list;
    }
}
