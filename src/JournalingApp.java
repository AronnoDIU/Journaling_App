import java.io.*; // For File, FileWriter, FileReader, BufferedReader, IOException
import java.text.SimpleDateFormat; // For SimpleDateFormat
import java.util.Date; // For Date and Date.getTime()
import java.util.Scanner;

public class JournalingApp {
    private static final String JOURNAL_FILE = "journal.txt";
    private static final String HTML_EXPORT_FILE = "journal_export.html";
    private static final String PASSWORD = "securepassword";
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);

        while (true) {
            printMenu();
            int choice = userInput.nextInt();
            userInput.nextLine(); // Consume the newline character

            switch (choice) {
                case 1: // For Create a new journal entry
                    createJournalEntry(userInput);
                    break;
                case 2: // For View all journal entries
                    viewJournalEntries();
                    break;
                case 3: // For Edit a journal entry
                    editJournalEntry(userInput);
                    break;
                case 4: // For Delete a journal entry
                    deleteJournalEntry(userInput);
                    break;
                case 5: // For Search entries by keyword
                    searchEntries(userInput);
                    break;
                case 6: // For Export entries to HTML
                    exportToHTML();
                    break;
                case 7: // For Password protect entries
                    passwordProtectEntries(userInput);
                    break;
                case 8: // For Exit
                    System.out.println("Exiting the Journaling Application. Goodbye!");
                    System.exit(0);
                default: // For invalid choice
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n*** Enhanced Journaling App Menu ***");
        System.out.println("1. Create a new journal entry");
        System.out.println("2. View all journal entries");
        System.out.println("3. Edit a journal entry");
        System.out.println("4. Delete a journal entry");
        System.out.println("5. Search entries by keyword");
        System.out.println("6. Export entries to HTML");
        System.out.println("7. Password protect entries");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

    // This method is used to create a new journal entry
    private static void createJournalEntry(Scanner userInput) {
        System.out.println("\nEnter your journal entry (type 'end' on a new line to finish):");

        // Create a new StringBuilder to store the journal entry content
        StringBuilder entryContent = new StringBuilder();

        while (true) {
            String line = userInput.nextLine(); // Read the next line from the user
            // If the user enters 'end' on a new line, stop reading input
            if (line.trim().equalsIgnoreCase("end")) {
                break;
            }
            // Otherwise, append the line to the entry content StringBuilder
            entryContent.append(line).append("\n");
        }

        // Create a timestamp for the journal entry using the current date and time
        String timestamp = DATE_FORMAT.format(new Date());

        // Append the timestamp and entry content to the journal file
        String entry = "\n\n---\n" + timestamp + "\n" + entryContent;

        // Write the entry to the journal file using a FileWriter
        try (FileWriter writer = new FileWriter(JOURNAL_FILE, true)) {
            writer.write(entry); // Write the entry to the file
            System.out.println("\nJournal entry saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving journal entry: " + e.getMessage());
        }
    }

    // This method is used to view all journal entries in the journal file
    private static void viewJournalEntries() {
        // Read the journal file line by line using a BufferedReader and print each line
        try (BufferedReader reader = new BufferedReader(new FileReader(JOURNAL_FILE))) {
            System.out.println("\n*** All Journal Entries ***\n");
            String line; // Stores the current line being read from the file

            // Read each line from the file until the end of the file is reached
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading journal entries: " + e.getMessage());
        }
    }

    // This method is used to edit a journal entry in the journal file
    private static void editJournalEntry(Scanner userInput) {
        viewJournalEntries(); // View all journal entries before editing
        System.out.print("\nEnter the timestamp of the entry you want to edit: ");
        String timestampToEdit = userInput.nextLine(); // Read the timestamp to edit

        // Create a temporary file to store the updated journal entries
        // (without the entry to edit) before renaming it to the original file
        try (BufferedReader reader = new BufferedReader(new FileReader(JOURNAL_FILE));
             FileWriter writer = new FileWriter(JOURNAL_FILE + ".tmp")) {

            String line;
            boolean entryFound = false; // Flag to check if the entry to edit was found

            // Read each line from the original file and write it to the temporary file
            while ((line = reader.readLine()) != null) {
                // Check if the line contains the timestamp to edit (to find the entry)
                if (line.contains(timestampToEdit)) {
                    System.out.println("Enter the updated content for the entry (type 'end' on a new line to finish):");
                    StringBuilder updatedContent = new StringBuilder();

                    while (true) {
                        String updatedLine = userInput.nextLine();
                        // If the user enters 'end' on a new line, stop reading input
                        if (updatedLine.trim().equalsIgnoreCase("end")) {
                            break;
                        }
                        // Otherwise, append the line to the updated content StringBuilder
                        updatedContent.append(updatedLine).append("\n");
                    }

                    // Create a timestamp for the updated entry using the current date and time
                    String updatedEntry = "\n\n---\n" + timestampToEdit + "\n" + updatedContent;
                    writer.write(updatedEntry);
                    entryFound = true;
                    System.out.println("\nJournal entry updated successfully!");
                } else {
                    writer.write(line + "\n"); // Write the line to the temporary file
                }
            }

            // If the entry to edit was not found, print an error message
            if (!entryFound) {
                System.out.println("No entry found with the specified timestamp.");
            }

        } catch (IOException e) {
            System.out.println("Error editing journal entry: " + e.getMessage());
        }

        // Rename the temporary file to the original file
        File originalFile = new File(JOURNAL_FILE);
        File tempFile = new File(JOURNAL_FILE + ".tmp");

        // Rename the temporary file to the original file (to update the journal file)
        if (tempFile.renameTo(originalFile)) {
            System.out.println("Journal file updated successfully.");
        } else {
            System.out.println("Error updating journal file.");
        }
    }

    // This method is used to delete a journal entry from the journal file
    private static void deleteJournalEntry(Scanner userInput) {
        viewJournalEntries(); // View all journal entries before deleting
        System.out.print("\nEnter the timestamp of the entry you want to delete: ");
        String timestampToDelete = userInput.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(JOURNAL_FILE));
             FileWriter writer = new FileWriter(JOURNAL_FILE + ".tmp")) {

            String line;
            boolean entryFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.contains(timestampToDelete)) {
                    entryFound = true;
                    System.out.println("Journal entry deleted successfully!");
                } else {
                    writer.write(line + "\n");
                }
            }

            if (!entryFound) {
                System.out.println("No entry found with the specified timestamp.");
            }

        } catch (IOException e) {
            System.out.println("Error deleting journal entry: " + e.getMessage());
        }

        // Rename the temporary file to the original file
        File originalFile = new File(JOURNAL_FILE);
        File tempFile = new File(JOURNAL_FILE + ".tmp");
        if (tempFile.renameTo(originalFile)) {
            System.out.println("Journal file updated successfully.");
        } else {
            System.out.println("Error updating journal file.");
        }
    }

    private static void searchEntries(Scanner scanner) {
        System.out.print("\nEnter the keyword to search for: ");
        String keyword = scanner.nextLine().toLowerCase();

        try (BufferedReader reader = new BufferedReader(new FileReader(JOURNAL_FILE))) {
            System.out.println("\n*** Search Results for '" + keyword + "' ***\n");
            String line;
            int entryCount = 0;

            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(keyword)) {
                    System.out.println(line);
                    entryCount++;
                }
            }

            if (entryCount == 0) {
                System.out.println("No entries found with the specified keyword.");
            }

        } catch (IOException e) {
            System.out.println("Error searching journal entries: " + e.getMessage());
        }
    }

    private static void exportToHTML() {
        try (BufferedReader reader = new BufferedReader(new FileReader(JOURNAL_FILE));
             FileWriter writer = new FileWriter(HTML_EXPORT_FILE)) {

            writer.write("<html><head><title>Journal Entries</title></head><body>");

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "<br>");
            }

            writer.write("</body></html>");
            System.out.println("Journal entries exported to HTML successfully. Check 'journal_export.html'.");

        } catch (IOException e) {
            System.out.println("Error exporting journal entries to HTML: " + e.getMessage());
        }
    }

    private static void passwordProtectEntries(Scanner scanner) {
        System.out.print("\nEnter the password to protect the entries: ");
        String enteredPassword = scanner.nextLine();

        if (enteredPassword.equals(PASSWORD)) {
            System.out.println("Entries are now password protected.");
        } else {
            System.out.println("Incorrect password. Entries remain unprotected.");
        }
    }
}