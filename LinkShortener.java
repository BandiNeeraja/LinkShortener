import java.io.*;
import java.util.*;

public class LinkShortener {

    private static final String BASE_URL = "http://short.ly/";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_URL_LENGTH = 6;
    private static final String FILE_NAME = "url_mappings.txt";  // File to store URL mappings

    private Map<String, String> urlMap;  // Short URL -> Long URL
    private Map<String, String> reverseMap;  // Long URL -> Short URL
    private Random random;

    // Constructor
    public LinkShortener() {
        urlMap = new HashMap<>();
        reverseMap = new HashMap<>();
        random = new Random();
        loadMappings();  // Load previous mappings from file
    }

    // Function to shorten a long URL
    public String shortenUrl(String longUrl) {
        if (reverseMap.containsKey(longUrl)) {
            return BASE_URL + reverseMap.get(longUrl);
        }

        String shortCode;
        do {
            shortCode = generateShortCode();
        } while (urlMap.containsKey(shortCode));

        urlMap.put(shortCode, longUrl);
        reverseMap.put(longUrl, shortCode);
        saveMappings();  // Save mappings to file

        return BASE_URL + shortCode;
    }

    // Function to expand a short URL
    public String expandUrl(String shortUrl) {
        String shortCode = shortUrl.replace(BASE_URL, "");
        return urlMap.getOrDefault(shortCode, "Error: URL not found!");
    }

    // Generate a random 6-character short code
    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_URL_LENGTH);
        for (int i = 0; i < SHORT_URL_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    // Save mappings to a file
    private void saveMappings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Map.Entry<String, String> entry : urlMap.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Error saving URL mappings.");
        }
    }

    // Load mappings from a file
    private void loadMappings() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    urlMap.put(parts[0], parts[1]);
                    reverseMap.put(parts[1], parts[0]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading URL mappings.");
        }
    }

    // Main method for CLI
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LinkShortener shortener = new LinkShortener();

        while (true) {
            System.out.println("\n1. Shorten URL");
            System.out.println("2. Expand URL");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            
            switch (choice) {
                case 1:
                    System.out.print("Enter long URL: ");
                    String longUrl = scanner.nextLine();
                    String shortUrl = shortener.shortenUrl(longUrl);
                    System.out.println("Shortened URL: " + shortUrl);
                    break;
                case 2:
                    System.out.print("Enter short URL: ");
                    String inputShortUrl = scanner.nextLine();
                    String expandedUrl = shortener.expandUrl(inputShortUrl);
                    System.out.println("Expanded URL: " + expandedUrl);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}
