package com.example.booklending.configuration;

import com.example.booklending.model.Book;
import com.example.booklending.model.BookStatus;
import com.example.booklending.repository.BookRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Configuration
public class DataLoader {

    @Value("${data.populate.local}")
    private boolean gelLocalData;

    @Value("${data.populate.enabled}")
    private boolean isDataPopulationEnabled;

    @Value("${data.populate.limit}")
    private int limit;

    @Autowired
    BookRepository bookRepository;

    ObjectMapper mapper = new ObjectMapper();

    ArrayList<Book> books = new ArrayList<>();

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes";

    Random rand = new Random();

    @Bean
    public CommandLineRunner conditionalPopulateData(BookRepository bookRepository) {

        return args -> {
            if (isDataPopulationEnabled) {
                if (gelLocalData) {
                    log.info("Populate books data from local resources /books.json.");

                    try (InputStream is = DataLoader.class.getResourceAsStream("/books.json")) {
                        // Deserialize JSON data into a list of Book objects
                        List<Book> books = mapper.readValue(is, new TypeReference<>() {
                        });

                        books.stream()
                                .limit(limit) // Limit the stream to 'limit' number of books
                                .forEach(book -> {
                                    Optional<Book> existingBook = bookRepository.findByIsbn(book.getIsbn());
                                    if (existingBook.isEmpty()) {
                                        try {
                                            bookRepository.save(book);
                                        } catch (Exception e) {
                                            log.info("Error occurred.");
                                        }
                                    }
                                });


                    } catch (Exception e) {
                        log.error("Failed to load and save books from JSON: ", e);
                    }


                } else {
                    log.info("Populate books data from external source.");

                    try {
                        fetchAndSaveTopBooks(limit);

                    } catch (Exception e) {
                        log.error("Error occurred while fetching book.", e);
                    }

                }
            } else {
                System.out.println("Data population is disabled. No data loaded.");
            }

            log.info("Initial test data population completed sucsessfully.");
        };


    }


    public void fetchAndSaveTopBooks(int totalBooks) throws Exception {


        String[] topics = {
                "transportation",
                "romance",
                "religion",
                "programming",
                "business",
                "health",
                "faith",
                "virility",
                "sustainable",
                "design",
                "mathematics",
                "estonian"
        };

        String topic;

        HttpClient client = HttpClient.newHttpClient();
        int startIndex = 0;
        int fetchedBooks = 0;
        int MAX_RESULTS = 40;

        while (fetchedBooks < totalBooks) {

            topic = topics[rand.nextInt(topics.length)];

            // Build the request URL with pagination
            String requestUrl = String.format("%s?q=%s&orderBy=relevance&startIndex=%d&maxResults=%d",
                    API_URL, topic, startIndex, MAX_RESULTS);

            // Create the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray items;
            try {
                items = jsonResponse.getJSONArray("items");
            } catch (JSONException e) {
                startIndex = 0;
                continue;
            }

            for (int i = 0; i < items.length(); i++) {
                JSONObject volumeInfo = items.getJSONObject(i).getJSONObject("volumeInfo");
                String title;
                try {
                    title = volumeInfo.getString("title");
                } catch (Exception e) {
                    break;
                }

                String authors = volumeInfo.has("authors") ? volumeInfo.getJSONArray("authors").join(", ") : "Unknown";
                String isbn = volumeInfo.has("industryIdentifiers")
                        ? volumeInfo.getJSONArray("industryIdentifiers").getJSONObject(0).getString("identifier")
                        : "Unknown";
                String coverUrl = volumeInfo.has("imageLinks")
                        ? volumeInfo.getJSONObject("imageLinks").getString("thumbnail")
                        : "No cover";

                Book book = new Book();
                book.setTitle(title);
                book.setAuthor(authors);
                book.setIsbn(isbn);
                book.setCoverUrl(coverUrl);
                book.setStatus(BookStatus.AVAILABLE); // Set default status or modify as needed


                /// Save the book if it does not already exist
                if (isValidISBN(book.getIsbn())) {
                    Optional<Book> existingBook = bookRepository.findByIsbn(book.getIsbn());
                    if (existingBook.isEmpty()) {
                        if (isValidISBN(book.getIsbn())) {
                            if (books.stream().filter(b -> b.getIsbn().equals(book.getIsbn())).count() == 0) {
                                if (book.getTitle().length() < 255 && book.getAuthor().length() < 255) {
                                    books.add(book);
                                    bookRepository.save(book);
                                    fetchedBooks++;
                                }
                            }
                        }
                    }
                }
            }
            startIndex += MAX_RESULTS;
        }

        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(new File("books.json"), books);
            log.info("Books have been serialized to JSON successfully.");
        } catch (IOException e) {
            log.error("Error occurred while saving file.", e);
        }
    }

    // Regex pattern for ISBN-10
    private static final String ISBN_10_REGEX = "^(\\d{9}[\\dX])$";

    // Regex pattern for ISBN-13
    private static final String ISBN_13_REGEX = "^(\\d{13})$";

    /**
     * Validates an ISBN (either ISBN-10 or ISBN-13).
     *
     * @param isbn The ISBN string to validate.
     * @return true if the ISBN is valid, false otherwise.
     */
    public static boolean isValidISBN(String isbn) {
        if (isbn == null) {
            return false;
        }

        isbn = isbn.replaceAll("-", ""); // Remove any hyphens for easier validation

        if (isbn.matches(ISBN_10_REGEX)) {
            return isValidISBN10(isbn);
        } else if (isbn.matches(ISBN_13_REGEX)) {
            return isValidISBN13(isbn);
        } else {
            return false;
        }
    }

    private static boolean isValidISBN10(String isbn) {
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (isbn.charAt(i) - '0') * (10 - i);
        }
        char check = isbn.charAt(9);
        sum += (check == 'X') ? 10 : (check - '0');
        return sum % 11 == 0;
    }

    private static boolean isValidISBN13(String isbn) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = isbn.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = 10 - (sum % 10);
        return checkDigit == (isbn.charAt(12) - '0');
    }
}


