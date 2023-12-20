import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class JournalingApp {
    private static final String JOURNAL_FILE = "journal.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    createJournalEntry(scanner);
                    break;
                case 2:
                    viewJournalEntries();
                    break;
                case 3:
                    editJournalEntry(scanner);
                    break;
                case 4:
                    System.out.println("Exiting the journaling app. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n*** Journaling App Menu ***");
        System.out.println("1. Create a new journal entry");
        System.out.println("2. View all journal entries");
        System.out.println("3. Edit a journal entry");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void createJournalEntry(Scanner scanner) {
        System.out.println("\nEnter your journal entry (type 'end' on a new line to finish):");
        StringBuilder entryContent = new StringBuilder();

        while (true) {
            String line = scanner.nextLine();
            if (line.trim().equalsIgnoreCase("end")) {
                break;
            }
            entryContent.append(line).append("\n");
        }

        String timestamp = DATE_FORMAT.format(new Date());
        String entry = "\n\n---\n" + timestamp + "\n" + entryContent.toString();

        try (FileWriter writer = new FileWriter(JOURNAL_FILE, true)) {
            writer.write(entry);
            System.out.println("\nJournal entry saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving journal entry: " + e.getMessage());
        }
    }

    private static void viewJournalEntries() {
        try (BufferedReader reader = new BufferedReader(new FileReader(JOURNAL_FILE))) {
            System.out.println("\n*** All Journal Entries ***\n");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading journal entries: " + e.getMessage());
        }
    }

    private static void editJournalEntry(Scanner scanner) {
        viewJournalEntries();
        System.out.print("\nEnter the timestamp of the entry you want to edit: ");
        String timestampToEdit = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(JOURNAL_FILE));
             FileWriter writer = new FileWriter(JOURNAL_FILE + ".tmp")) {

            String line;
            boolean entryFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.contains(timestampToEdit)) {
                    System.out.println("Enter the updated content for the entry (type 'end' on a new line to finish):");
                    StringBuilder updatedContent = new StringBuilder();

                    while (true) {
                        String updatedLine = scanner.nextLine();
                        if (updatedLine.trim().equalsIgnoreCase("end")) {
                            break;
                        }
                        updatedContent.append(updatedLine).append("\n");
                    }

                    String updatedEntry = "\n\n---\n" + timestampToEdit + "\n" + updatedContent.toString();
                    writer.write(updatedEntry);
                    entryFound = true;
                    System.out.println("\nJournal entry updated successfully!");
                } else {
                    writer.write(line + "\n");
                }
            }

            if (!entryFound) {
                System.out.println("No entry found with the specified timestamp.");
            }

        } catch (IOException e) {
            System.out.println("Error editing journal entry: " + e.getMessage());
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
}