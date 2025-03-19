package cn.oyzh.easyzk.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalApp extends Application {

    private TextArea textArea; // the text area to display the terminal output
    private String prompt = "> "; // the prompt symbol
    private String input = ""; // the input buffer

    @Override
    public void start(Stage primaryStage) {
        textArea = new TextArea(); // create a new text area
//        textArea.setEditable(false); // disable editing
        textArea.setStyle("-fx-font-family: monospace;"); // set the font to monospace
        textArea.appendText(prompt); // append the prompt symbol

        // add a key pressed event handler to the text area
        textArea.setOnKeyPressed(event -> {
            // get the pressed key code
            KeyCode keyCode = event.getCode();

            if (keyCode == KeyCode.ENTER) { // if the key is ENTER
                // execute the input command
                executeCommand(input);
                // clear the input buffer
                input = "";
                // append a new line and the prompt symbol
                textArea.appendText("\n" + prompt);
            } else if (keyCode == KeyCode.BACK_SPACE) { // if the key is BACK_SPACE
                // delete the last character from the input buffer
                if (input.length() > 0) {
                    input = input.substring(0, input.length() - 1);
                }
            } else if (keyCode.isLetterKey() || keyCode.isDigitKey() || keyCode.isWhitespaceKey()) { // if the key is a letter, a digit, or a whitespace
                // append the key text to the input buffer
                input += event.getText();
            }
        });

        // create a border pane to hold the text area
        BorderPane root = new BorderPane();
        root.setCenter(textArea);

        // create a scene and set it to the stage
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Terminal App");
        primaryStage.setScene(scene);
        primaryStage.show();

        textArea.requestFocus();
    }

    // a method to execute a command and display the output
    private void executeCommand(String command) {
        try {
            // create a process to run the command
            Process process = Runtime.getRuntime().exec(command);
            // create a buffered reader to read the output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // read each line from the output and append it to the text area
            String line;
            while ((line = reader.readLine()) != null) {
                textArea.appendText("\n" + line);
            }
            // close the reader
            reader.close();
        } catch (IOException e) {
            // if an exception occurs, display the error message
            textArea.appendText("\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

