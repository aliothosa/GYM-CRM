package com.elioth.epam.gymcrm;

import com.elioth.epam.gymcrm.auth.AuthSession;
import com.elioth.epam.gymcrm.auth.Role;
import com.elioth.epam.gymcrm.domain.Address;
import com.elioth.epam.gymcrm.dto.request.ChangePasswordRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainerRequest;
import com.elioth.epam.gymcrm.dto.request.CreateTrainingRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTraineeRequest;
import com.elioth.epam.gymcrm.dto.request.UpdateTrainerRequest;
import com.elioth.epam.gymcrm.dto.response.CreatedTraineeResponse;
import com.elioth.epam.gymcrm.dto.response.CreatedTrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TraineeResponse;
import com.elioth.epam.gymcrm.dto.response.TrainerResponse;
import com.elioth.epam.gymcrm.dto.response.TrainingResponse;
import com.elioth.epam.gymcrm.facade.GymCrmFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class GymCrmRunner implements CommandLineRunner {

    private final GymCrmFacade facade;
    private final Scanner scanner = new Scanner(System.in);

    @Value("${gymcrm.cli.enabled:true}")
    private boolean cliEnabled;

    @Autowired
    public GymCrmRunner(GymCrmFacade facade) {
        this.facade = facade;
    }

    @Override
    public void run(String... args) {
        if (!cliEnabled) {
            return;
        }

        printBanner();
        printDemoHint();

        boolean running = true;
        while (running) {
            printPublicMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> loginAs(Role.TRAINEE);
                case 2 -> loginAs(Role.TRAINER);
                case 3 -> registerTrainee();
                case 4 -> registerTrainer();
                case 5 -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // ========================= Public menu actions =========================

    private void loginAs(Role role) {
        String username = readString("Username: ");
        String password = readPassword("Password: ");

        try {
            AuthSession session = role == Role.TRAINEE
                    ? facade.loginAsTrainee(username, password)
                    : facade.loginAsTrainer(username, password);

            System.out.println("Login successful. Welcome, " + session.username() + "!");

            if (role == Role.TRAINEE) {
                runTraineeMenu();
            } else {
                runTrainerMenu();
            }
        } catch (RuntimeException ex) {
            System.out.println("Login failed: " + ex.getMessage());
        }
    }

    private void registerTrainee() {
        System.out.println("\n--- Register Trainee ---");
        try {
            CreateTraineeRequest request = readCreateTraineeRequest();
            CreatedTraineeResponse created = facade.createTraineeProfile(request);
            System.out.println("Trainee registered successfully.");
            printCreatedTrainee(created);
        } catch (RuntimeException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void registerTrainer() {
        System.out.println("\n--- Register Trainer ---");
        printTrainingTypeHint();
        try {
            CreateTrainerRequest request = readCreateTrainerRequest();
            CreatedTrainerResponse created = facade.createTrainerProfile(request);
            System.out.println("Trainer registered successfully.");
            printCreatedTrainer(created);
        } catch (RuntimeException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // ========================= Trainee menu =========================

    private void runTraineeMenu() {
        boolean inMenu = true;
        while (inMenu) {
            String username = facade.getCurrentSession().username();
            printSection("TRAINEE MENU", "Logged in as: " + username);
            System.out.println("1. View my profile");
            System.out.println("2. Update my profile");
            System.out.println("3. Delete / deactivate my profile");
            System.out.println("4. View my trainings");
            System.out.println("5. Update my trainers list");
            System.out.println("6. Change my password");
            System.out.println("7. Logout");

            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> runSafely(this::viewTraineeProfile);
                case 2 -> runSafely(this::updateTraineeProfile);
                case 3 -> {
                    if (runTraineeAccountMenu()) {
                        inMenu = false;
                    }
                }
                case 4 -> runSafely(this::viewTraineeTrainings);
                case 5 -> runSafely(this::updateTraineeTrainersList);
                case 6 -> runSafely(this::changeTraineePassword);
                case 7 -> {
                    facade.logout();
                    System.out.println("Logged out.");
                    inMenu = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private boolean runTraineeAccountMenu() {
        System.out.println("\n--- Account actions ---");
        System.out.println("1. Deactivate my profile");
        System.out.println("2. Activate my profile");
        System.out.println("3. Delete my profile permanently");
        System.out.println("4. Back");

        int choice = readInt("Choose an option: ");
        switch (choice) {
            case 1 -> runSafely(() -> {
                facade.deactivateTraineeProfile();
                System.out.println("Profile deactivated.");
            });
            case 2 -> runSafely(() -> {
                facade.activateTraineeProfile();
                System.out.println("Profile activated.");
            });
            case 3 -> {
                try {
                    if (!readBoolean("Delete permanently? This cannot be undone (y/n): ")) {
                        System.out.println("Deletion cancelled.");
                        return false;
                    }
                    facade.deleteTraineeProfile();
                    System.out.println("Profile deleted. You have been logged out.");
                    return true;
                } catch (RuntimeException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
            case 4 -> { /* back */ }
            default -> System.out.println("Invalid option.");
        }
        return false;
    }

    private void viewTraineeProfile() {
        TraineeResponse profile = facade.getTraineeProfile();
        printTrainee(profile);
    }

    private void updateTraineeProfile() {
        System.out.println("Leave a field blank to keep the current value.");
        TraineeResponse current = facade.getTraineeProfile();
        UpdateTraineeRequest request = readUpdateTraineeRequest(current);
        TraineeResponse updated = facade.updateTraineeProfile(request);
        System.out.println("Profile updated:");
        printTrainee(updated);
    }

    private void viewTraineeTrainings() {
        System.out.println("Optional filters (press Enter to skip):");
        LocalDate from = readOptionalLocalDate("From date (yyyy-MM-dd): ");
        LocalDate to = readOptionalLocalDate("To date (yyyy-MM-dd): ");
        String trainerName = readOptionalString("Trainer name contains: ");
        String trainingType = readOptionalString("Training type (e.g. FITNESS): ");

        List<TrainingResponse> trainings = facade.getTraineeTrainingsByCriteria(
                from, to, trainerName, trainingType
        );

        if (trainings.isEmpty()) {
            System.out.println("No trainings found.");
        } else {
            trainings.forEach(this::printTraining);
        }
    }

    private void updateTraineeTrainersList() {
        System.out.println("Trainers not yet assigned to you:");
        List<TrainerResponse> available = facade.getTrainersNotAssignedToTrainee();
        if (available.isEmpty()) {
            System.out.println("(none)");
        } else {
            available.forEach(this::printTrainer);
        }

        List<Long> trainerIds = readLongList("Enter trainer IDs to assign (comma-separated): ");
        facade.updateTraineeTrainersList(trainerIds);
        System.out.println("Trainers list updated.");
    }

    private void changeTraineePassword() {
        String oldPassword = readPassword("Current password: ");
        String newPassword = readPassword("New password: ");
        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword);
        facade.changeTraineePassword(request);
        System.out.println("Password changed successfully.");
    }

    // ========================= Trainer menu =========================

    private void runTrainerMenu() {
        boolean inMenu = true;
        while (inMenu) {
            String username = facade.getCurrentSession().username();
            printSection("TRAINER MENU", "Logged in as: " + username);
            System.out.println("1. View my profile");
            System.out.println("2. Update my profile");
            System.out.println("3. View my trainings");
            System.out.println("4. Add training");
            System.out.println("5. Change my password");
            System.out.println("6. Logout");

            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> runSafely(this::viewTrainerProfile);
                case 2 -> runSafely(this::updateTrainerProfile);
                case 3 -> runSafely(this::viewTrainerTrainings);
                case 4 -> runSafely(this::addTraining);
                case 5 -> runSafely(this::changeTrainerPassword);
                case 6 -> {
                    facade.logout();
                    System.out.println("Logged out.");
                    inMenu = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void viewTrainerProfile() {
        TrainerResponse profile = facade.getTrainerProfile();
        printTrainer(profile);
    }

    private void updateTrainerProfile() {
        System.out.println("Leave a field blank to keep the current value.");
        TrainerResponse current = facade.getTrainerProfile();
        UpdateTrainerRequest request = readUpdateTrainerRequest(current);
        TrainerResponse updated = facade.updateTrainerProfile(request);
        System.out.println("Profile updated:");
        printTrainer(updated);
    }

    private void viewTrainerTrainings() {
        System.out.println("Optional filters (press Enter to skip):");
        LocalDate from = readOptionalLocalDate("From date (yyyy-MM-dd): ");
        LocalDate to = readOptionalLocalDate("To date (yyyy-MM-dd): ");
        String traineeName = readOptionalString("Trainee name contains: ");

        List<TrainingResponse> trainings = facade.getTrainerTrainingsByCriteria(from, to, traineeName);

        if (trainings.isEmpty()) {
            System.out.println("No trainings found.");
        } else {
            trainings.forEach(this::printTraining);
        }
    }

    private void addTraining() {
        AuthSession session = facade.getCurrentSession();
        printTrainingTypeHint();

        Long traineeId = readLong("Trainee ID: ");
        Long trainingTypeId = readLong("Training type ID: ");
        String trainingName = readString("Training name: ");
        LocalDate trainingDate = readLocalDate("Training date (yyyy-MM-dd): ");
        Long duration = readLong("Duration in minutes: ");

        CreateTrainingRequest request = new CreateTrainingRequest(
                traineeId,
                session.id(),
                trainingTypeId,
                trainingName,
                trainingDate,
                duration
        );

        TrainingResponse created = facade.createTraining(request);
        System.out.println("Training created:");
        printTraining(created);
    }

    private void changeTrainerPassword() {
        String oldPassword = readPassword("Current password: ");
        String newPassword = readPassword("New password: ");
        ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword);
        facade.changeTrainerPassword(request);
        System.out.println("Password changed successfully.");
    }

    // ========================= DTO builders =========================

    private CreateTraineeRequest readCreateTraineeRequest() {
        String firstName = readString("First name: ");
        String lastName = readString("Last name: ");
        LocalDate birthDate = readLocalDate("Birth date (yyyy-MM-dd): ");
        Address address = readAddress();
        return new CreateTraineeRequest(firstName, lastName, birthDate, address);
    }

    private CreateTrainerRequest readCreateTrainerRequest() {
        String firstName = readString("First name: ");
        String lastName = readString("Last name: ");
        Long trainingTypeId = readLong("Training type ID: ");
        return new CreateTrainerRequest(firstName, lastName, trainingTypeId);
    }

    private UpdateTraineeRequest readUpdateTraineeRequest(TraineeResponse current) {
        String firstName = readStringOrDefault("First name", current.firstName());
        String lastName = readStringOrDefault("Last name", current.lastName());
        LocalDate birthDate = readLocalDateOrDefault("Birth date (yyyy-MM-dd)", current.birthDate());
        Address address = readAddressOrDefault(current.address());
        return new UpdateTraineeRequest(firstName, lastName, birthDate, address);
    }

    private UpdateTrainerRequest readUpdateTrainerRequest(TrainerResponse current) {
        String firstName = readStringOrDefault("First name", current.firstName());
        String lastName = readStringOrDefault("Last name", current.lastName());
        Long trainingTypeId = readLongOrDefault(
                "Training type ID (current: " + current.trainingTypeId() + ")",
                current.trainingTypeId()
        );
        return new UpdateTrainerRequest(firstName, lastName, trainingTypeId);
    }

    private Address readAddress() {
        String street = readString("Street: ");
        String city = readString("City: ");
        String state = readString("State: ");
        String zipCode = readString("Zip code: ");
        int number = readInt("Street number: ");
        return new Address(street, city, state, zipCode, number);
    }

    private Address readAddressOrDefault(Address current) {
        if (current == null) {
            return readAddress();
        }
        String street = readStringOrDefault("Street", current.getStreet());
        String city = readStringOrDefault("City", current.getCity());
        String state = readStringOrDefault("State", current.getState());
        String zipCode = readStringOrDefault("Zip code", current.getZipCode());
        int number = readIntOrDefault("Street number", current.getNumber());
        return new Address(street, city, state, zipCode, number);
    }

    // ========================= Output helpers =========================

    private void printBanner() {
        System.out.println();
        System.out.println("====================================");
        System.out.println("        GYM CRM DEMO APP");
        System.out.println("====================================");
        System.out.println();
    }

    private void printPublicMenu() {
        printSection("MAIN MENU", null);
        System.out.println("1. Login as Trainee");
        System.out.println("2. Login as Trainer");
        System.out.println("3. Register Trainee");
        System.out.println("4. Register Trainer");
        System.out.println("5. Exit");
        System.out.println();
    }

    private void printSection(String title, String subtitle) {
        System.out.println();
        System.out.println("====================================");
        System.out.println("          " + title);
        System.out.println("====================================");
        if (subtitle != null && !subtitle.isBlank()) {
            System.out.println(subtitle);
        }
        System.out.println();
    }

    private void printDemoHint() {
        System.out.println("Demo seed accounts (password for all: pass123):");
        System.out.println("  Trainees : Emily.Davis, Carlos.Lopez");
        System.out.println("  Trainers : John.Doe, Jane.Smith, Mike.Brown");
        System.out.println("Training types: 1=FITNESS, 2=YOGA, 3=ZUMBA, 4=STRETCHING, 5=RESISTANCE");
        System.out.println();
    }

    private void printTrainingTypeHint() {
        System.out.println("Training types: 1=FITNESS, 2=YOGA, 3=ZUMBA, 4=STRETCHING, 5=RESISTANCE");
    }

    private void printCreatedTrainee(CreatedTraineeResponse created) {
        System.out.println("  Trainee ID : " + created.traineeId());
        System.out.println("  Username   : " + created.username());
        System.out.println("  Password   : " + created.password());
        System.out.println("  (Save these credentials to log in.)");
    }

    private void printCreatedTrainer(CreatedTrainerResponse created) {
        System.out.println("  Trainer ID : " + created.trainerId());
        System.out.println("  Username   : " + created.username());
        System.out.println("  Password   : " + created.password());
        System.out.println("  (Save these credentials to log in.)");
    }

    private void printTrainee(TraineeResponse trainee) {
        System.out.println("  Trainee ID : " + trainee.traineeId());
        System.out.println("  Name       : " + trainee.firstName() + " " + trainee.lastName());
        System.out.println("  Username   : " + trainee.username());
        System.out.println("  Active     : " + trainee.active());
        System.out.println("  Birth date : " + trainee.birthDate());
        if (trainee.address() != null) {
            Address address = trainee.address();
            System.out.println("  Address    : " + address.getStreet() + " " + address.getNumber()
                    + ", " + address.getCity() + ", " + address.getState() + " " + address.getZipCode());
        }
    }

    private void printTrainer(TrainerResponse trainer) {
        System.out.println("  Trainer ID : " + trainer.trainerId());
        System.out.println("  Name       : " + trainer.firstName() + " " + trainer.lastName());
        System.out.println("  Username   : " + trainer.username());
        System.out.println("  Active     : " + trainer.active());
        System.out.println("  Specialty  : " + trainer.trainingTypeName() + " (id=" + trainer.trainingTypeId() + ")");
    }

    private void printTraining(TrainingResponse training) {
        System.out.println("  Training ID   : " + training.trainingId());
        System.out.println("  Name          : " + training.trainingName());
        System.out.println("  Date          : " + training.trainingDate());
        System.out.println("  Duration (min): " + training.duration());
        System.out.println("  Type          : " + training.trainingTypeName());
        System.out.println("  Trainee       : " + training.traineeFirstName() + " " + training.traineeLastName()
                + " (" + training.traineeUsername() + ")");
        System.out.println("  Trainer       : " + training.trainerFirstName() + " " + training.trainerLastName()
                + " (" + training.trainerUsername() + ")");
        System.out.println();
    }

    // ========================= Input helpers =========================

    private void runSafely(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("Value cannot be empty. Please try again.");
        }
    }

    private String readOptionalString(String prompt) {
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        return value.isBlank() ? null : value;
    }

    private String readStringOrDefault(String prompt, String current) {
        System.out.print(prompt + " [" + current + "]: ");
        String value = scanner.nextLine().trim();
        return value.isBlank() ? current : value;
    }

    private String readPassword(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please enter a whole number.");
            }
        }
    }

    private int readIntOrDefault(String prompt, int current) {
        while (true) {
            System.out.print(prompt + " [" + current + "]: ");
            String line = scanner.nextLine().trim();
            if (line.isBlank()) {
                return current;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please enter a whole number or press Enter to keep current.");
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please enter a valid number.");
            }
        }
    }

    private Long readLongOrDefault(String prompt, Long current) {
        while (true) {
            System.out.print(prompt + " [" + current + "]: ");
            String line = scanner.nextLine().trim();
            if (line.isBlank()) {
                return current;
            }
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please enter a valid number or press Enter to keep current.");
            }
        }
    }

    private List<Long> readLongList(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isBlank()) {
                return List.of();
            }
            try {
                return Arrays.stream(line.split(","))
                        .map(String::trim)
                        .filter(part -> !part.isBlank())
                        .map(Long::parseLong)
                        .collect(Collectors.toCollection(ArrayList::new));
            } catch (NumberFormatException ex) {
                System.out.println("Invalid format. Use comma-separated numbers, e.g. 1,2,3");
            }
        }
    }

    private boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) {
                return true;
            }
            if (line.equals("n") || line.equals("no")) {
                return false;
            }
            System.out.println("Please enter y/yes or n/no.");
        }
    }

    private LocalDate readLocalDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid date. Use format yyyy-MM-dd.");
            }
        }
    }

    private LocalDate readLocalDateOrDefault(String prompt, LocalDate current) {
        while (true) {
            System.out.print(prompt + " [" + current + "]: ");
            String line = scanner.nextLine().trim();
            if (line.isBlank()) {
                return current;
            }
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid date. Use format yyyy-MM-dd or press Enter to keep current.");
            }
        }
    }

    private LocalDate readOptionalLocalDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isBlank()) {
                return null;
            }
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid date. Use format yyyy-MM-dd or press Enter to skip.");
            }
        }
    }
}
