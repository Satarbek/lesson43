import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = makeServer();
            server.start();
            initRoutes(server);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static HttpServer makeServer() throws IOException {
        String host = "localhost"; // 127.0.0.1
        InetSocketAddress address = new InetSocketAddress(host, 9889);

        System.out.printf("Server start on address: http://%s:%s%n", address.getHostName(), address.getPort());

        HttpServer server = HttpServer.create(address, 50);
        System.out.println("Successfully!");

        return server;
    }

    private static void initRoutes(HttpServer server){
        server.createContext("/", Main::firstHandleRequest);
        server.createContext("/apps", Main::secondHandleRequest);
        server.createContext("/apps/profile", Main::thirdHandleRequest);
    }

    private static void firstHandleRequest(HttpExchange exchange){
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            int response = 200;
            int length = 0;
            exchange.sendResponseHeaders(response, length);

            try (PrintWriter writer = getWriterFrom(exchange)){
                write(writer, "First processing...", "********");

                writeData(writer,exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void secondHandleRequest(HttpExchange exchange){
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            int response = 200;
            int length = 0;
            exchange.sendResponseHeaders(response, length);

            try (PrintWriter writer = getWriterFrom(exchange)){
               write(writer, "Second processing...", "*******");

                writeData(writer,exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void thirdHandleRequest(HttpExchange exchange){
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            int response = 200;
            int length = 0;
            exchange.sendResponseHeaders(response, length);

            try (PrintWriter writer = getWriterFrom(exchange)){
                write(writer, "Third processing...", "*******");

                writeData(writer,exchange);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeHeaders(Writer writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach( (k, v) -> write(writer, "\t" + k, v.toString()) );
    }

    private static void write(Writer writer, String msg, String method) {
        String data = String.format("%s: %s%n%n", msg, method);

        try {
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) {
        OutputStream output = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(output, false, charset);
    }

    private static BufferedReader getReader(HttpExchange exchange){
        InputStream input = exchange.getRequestBody();
        Charset charset = StandardCharsets.UTF_8;
        InputStreamReader isr = new InputStreamReader(input, charset);
        return new BufferedReader(isr);
    }

    private static void writeData(Writer writer, HttpExchange exchange){
        try (BufferedReader reader = getReader(exchange)){
            if (!reader.ready()) return;
            write(writer, "Data block", "");
            reader.lines().forEach(e -> write(writer, "\t", e));
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}