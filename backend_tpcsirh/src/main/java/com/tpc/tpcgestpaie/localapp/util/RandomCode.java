package com.tpc.tpcgestpaie.localapp.util;
import java.security.SecureRandom;

/**
 * Utility class for generating random codes in various formats.
 *
 * Examples:
 * randomCode("AAA-999") -> ABC-482
 * randomCode(3,3,"-") -> ABC-9F3
 * randomCode(4,4,"_") -> QWER_8F2A
 */
public class RandomCode {

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String ALPHA_NUMERIC = LETTERS + NUMBERS;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate a random code using a pattern.
     *
     * Pattern rules:
     * A = random letter
     * 9 = random number
     * X = random alphanumeric
     *
     * Example:
     * randomCode("AAA-999") -> ABC-482
     *
     * @param pattern format pattern
     * @return generated code
     */
    public static String randomCode(String pattern) {

        StringBuilder result = new StringBuilder();

        for (char c : pattern.toCharArray()) {

            switch (c) {

                case 'A':
                    result.append(randomChar(LETTERS));
                    break;

                case '9':
                    result.append(randomChar(NUMBERS));
                    break;

                case 'X':
                    result.append(randomChar(ALPHA_NUMERIC));
                    break;

                default:
                    result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * Generate a random code with prefix letters and alphanumeric suffix.
     *
     * Example:
     * randomCode(3,3,"-") -> ABC-9F3
     *
     * @param prefixLength number of letters
     * @param suffixLength number of alphanumeric characters
     * @param separator separator between prefix and suffix
     * @return generated code
     */
    public static String randomCode(int prefixLength, int suffixLength, String separator) {

        String prefix = randomString(prefixLength, LETTERS);
        String suffix = randomString(suffixLength, ALPHA_NUMERIC);

        return prefix + separator + suffix;
    }

    /**
     * Generate a random code using custom charsets.
     *
     * Example:
     * randomCode(3,3,"ABC","012345","-")
     *
     * @param prefixLength prefix length
     * @param suffixLength suffix length
     * @param prefixCharset allowed prefix characters
     * @param suffixCharset allowed suffix characters
     * @param separator separator
     * @return generated code
     */
    public static String randomCode(int prefixLength,
                                    int suffixLength,
                                    String prefixCharset,
                                    String suffixCharset,
                                    String separator) {

        String prefix = randomString(prefixLength, prefixCharset);
        String suffix = randomString(suffixLength, suffixCharset);

        return prefix + separator + suffix;
    }

    /**
     * Generate a random string from a given charset.
     *
     * @param length desired length
     * @param charset characters to use
     * @return random string
     */
    public static String randomString(int length, String charset) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(randomChar(charset));
        }

        return result.toString();
    }

    /**
     * Returns a random character from a charset.
     *
     * @param charset characters allowed
     * @return random character
     */
    private static char randomChar(String charset) {
        return charset.charAt(RANDOM.nextInt(charset.length()));
    }
}
