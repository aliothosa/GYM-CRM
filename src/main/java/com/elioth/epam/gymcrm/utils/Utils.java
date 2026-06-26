package com.elioth.epam.gymcrm.utils;

import com.elioth.epam.gymcrm.domain.User;
import com.elioth.epam.gymcrm.exception.InvalidEntityException;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Stream;

public class Utils {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }

    public static String usernameGenerator(User user, List<? extends User> userList) {
        long userCount = sameNameUserCount(user, userList.stream());

        if  (userCount == 0) {
            return String.format("%s.%s", user.getFirstName(), user.getLastName());
        }
        return String.format("%s.%s%d", user.getFirstName(), user.getLastName(), userCount);
    }

    public static String usernameGenerator(String firstName, String lastName, long userCount) {
        if  (userCount == 0) {
            return String.format("%s.%s", firstName, lastName);
        }
        return String.format("%s.%s%d", firstName, lastName, userCount);
    }


    public static long sameNameUserCount (User user, Stream<? extends User> stream){
        return stream
                .filter(currentUser ->
                    user.getFirstName().equals(currentUser.getFirstName()) && user.getLastName().equals(currentUser.getLastName())
                )
                .count();
    }


}
