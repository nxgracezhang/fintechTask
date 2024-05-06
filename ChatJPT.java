import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * GUI for the main page.
 * Objective: an interactive platform modeled after chatgpt
 * @author Grace Zhang
 * @version 1.0
 */
public class ChatJPT extends Application {
    private ResponseGenerator responseGenerator;
    private ArrayList<String> conversationHistory = new ArrayList<>();
    private VBox conversationContainer;
    private TextField inputTextField;

    /**
     * Initialize UI components and event handlers.
     * @param primaryStage - stage object
     */
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        conversationContainer = new VBox();
        ScrollPane scrollPane = new ScrollPane(conversationContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // Set up input area
        inputTextField = new TextField();
        inputTextField.setPromptText("Enter your message: ");

        // Setup submit button
        // Used lambda expression
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> submitButtonHandler());

        // Setup pressing enter key to submit alternative
        // Used anonymous inner class
        inputTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            /**
             * handle method.
             * @param e - key event
             */
            @Override
            public void handle(KeyEvent e) {
                if (e.getCode() == KeyCode.ENTER) {
                    submitButtonHandler();
                }
            }
        });

        // Add input field and submit button to the bottom of the main layout
        VBox inputContainer = new VBox(10);
        inputContainer.getChildren().addAll(inputTextField, submitButton);
        inputContainer.setPadding(new Insets(10));

        // Combine conversation and input areas
        root.setCenter(scrollPane);
        root.setBottom(inputContainer);

        // Create the scene and set it to the stage
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Your Friend ChatJPT");
        primaryStage.show();

        // load conversation history from file
        loadConversationHistory();
        // System.out.println("LOADED");

        // add history to conversationContainer
        displayHistory();
        // System.out.println("DISPLAYED");

        primaryStage.setOnCloseRequest(e -> stop());

        // initialize response generator
        try {
            responseGenerator = ResponseGenerator.getResponseGenerator();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Before closing the file, sync and save conversation history.
     */
    public void stop() {
        saveConversationHistory();
        // System.out.println("STOP");
    }

    /**
     * write conversation history from memory to a new file.
     * (overwrite to a new file)
     */
    private void saveConversationHistory() {
        String fileName = "conversation_history.txt";
        String delimiter = "\u001C";
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(fileName);
            for (String message : conversationHistory) {
                writer.print(message);
                writer.print(delimiter);
            }
            writer.flush();
            // System.out.println("SAVED");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Read conversation history from file to memory.
     * Update conversationHistory ArrayList
     */
    private void loadConversationHistory() {
        File inFile;
        Scanner scan;
        String delimiter = "\u001C";
        try {
            inFile = new File("conversation_history.txt");
            scan = new Scanner(inFile);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] parts = line.split(delimiter);
                for (String part : parts) {
                    conversationHistory.add(part);
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException caught: " + e.getMessage());
        }
    }

    /**
     * Handler for submit button.
     */
    private void submitButtonHandler() {
        // remove trailing or leading whitespaces
        String userMessage = inputTextField.getText().trim();
        if (!userMessage.isEmpty()) {
            appendToConversation("User  : " + userMessage);
            conversationHistory.add(userMessage);
            String response = responseGenerator.getResponseToPrompt(userMessage);
            appendToConversation("ChatJPT   : " + response);
            conversationHistory.add(response);
            inputTextField.clear();
        }
    }

    /**
     * Helper method for submit button handler.
     * @param message - user message
     */
    private void appendToConversation(String message) {
        Text text = new Text(message);
        conversationContainer.getChildren().add(text);
    }

    private void displayHistory() {
        if (conversationHistory != null) {
            for (int i = 0; i < conversationHistory.size(); i++) {
                if (i % 2 == 0) {
                    String userMessage = conversationHistory.get(i);
                    appendToConversation("User  : " + userMessage);
                } else {
                    String jptResponse = conversationHistory.get(i);
                    appendToConversation("ChatJPT   : " + jptResponse);
                }
            }
        }
    }

    /**
     * Launch.
     * @param args command line argument
     */
    public static void main(String[] args) {
        launch(args);
    }

}
