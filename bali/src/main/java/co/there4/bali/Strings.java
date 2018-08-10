package co.there4.bali;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.stream.Collectors.joining;
import static co.there4.bali.Checks.require;
import static co.there4.bali.Things.stringOf;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Several string utilities not included in the JDK. Some are in other libraries like Guava or
 * Commons Lang.
 *
 * <p>I implement them because the previously mentioned libraries just duplicate some functions
 * with modern JREs.
 *
 * @author jam
 */
public interface Strings {
    /** Runtime specific end of line. */
    String EOL = getProperty ("line.separator");

    /** Variable prefix for string filtering. */
    String VARIABLE_PREFIX = "${";
    /** Variable sufix for string filtering. */
    String VARIABLE_SUFFIX = "}";

    /**
     * Calls {@link #filter(String, Entry[])} converting the map in entries.
     *
     * @see #filter(String, Entry[])
     */
    static String filter (final String text, final Map<?, ?> parameters) {
        Checks.require (parameters != null);
        Set<? extends Entry<?, ?>> entries = parameters.entrySet ();
        return filter (text, entries.toArray (new Entry<?, ?>[entries.size ()]));
    }

    /**
     * Filters a text substituting each key by its value. The keys format is:
     * <code>${key}</code> and all occurrences are replaced by the supplied value.
     *
     * <p>If a variable does not have a parameter, it is left as it is.
     *
     * @param text The text to filter. Can not be 'null'.
     * @param parameters The map with the list of key/value tuples. Can not be 'null'.
     * @return The filtered text or the same string if no values are passed or found in the text.
     */
    static String filter (final String text, final Entry<?, ?>... parameters) {
        Checks.require (text != null);
        Checks.require (parameters != null);

        String result = text;

        for (Entry<?, ?> parameter : parameters) {
            Object k = parameter.getKey ();
            Object v = parameter.getValue ();
            Checks.require (k != null);
            require (v != null, format ("'%s' value is 'null'", k));

            String key = stringOf (k);
            require (!isEmpty (key), format("key with '%s' value is empty", v));
            String value = stringOf (v);

            result = result.replace (VARIABLE_PREFIX + key + VARIABLE_SUFFIX, value);
        }

        return result;
    }

    /**
     * Utility method to check if a string has a value.
     *
     * @param text String to check.
     * @return True if the string is 'null' or empty.
     */
    static boolean isEmpty (String text) {
        return text == null || text.isEmpty ();
    }

    /**
     * Only a wrapper to avoid having to catch the checked exception.
     *
     * @see String#String(byte[], String)
     */
    static String encode (final byte[] data, final String encoding) {
        try {
            return new String (data, encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * Only a wrapper to avoid having to catch the checked exception.
     *
     * @see String#getBytes(String)
     */
    static byte[] decode (final String text, final String encoding) {
        try {
            return text.getBytes (encoding);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException (e);
        }
    }

    /**
     * Repeat a given text a certain number of times.
     *
     * @param text String to repeat. It can't be 'null'.
     * @param times Number of times to repeat the text. Must be greater than 0.
     * @return The passed text repeated the given times.
     */
    static String repeat (String text, int times) {
        Checks.require (text != null);
        Checks.require (times >= 0);

        StringBuilder buffer = new StringBuilder (text.length () * times);

        for (int ii = 0; ii < times; ii++)
            buffer.append (text);

        return buffer.toString ();
    }

    /**
     * Indents every line with the given padding the number of times specified.
     *
     * @param text Text to indent. Can't be 'null'.
     * @param padding String to repeat at the beginning of each line. Can't be 'null'.
     * @param times Number of times to repeat the padding text. Must be greater than 0.
     * @return Text with every line indented with the given padding the number of times specified.
     */
    static String indent (final String text, final String padding, final int times) {
        Checks.require (text != null);

        String[] lines = text.split (EOL, -1);
        String appendString = repeat (padding, times);
        StringBuilder buffer = new StringBuilder ();

        for (int ii = 0; ii < lines.length - 1; ii++)
            buffer.append (appendString).append (lines[ii]).append (EOL);

        return buffer.append (appendString).append (lines[lines.length - 1]).toString ();
    }

    /**
     * Syntactic sugar for multiline strings.
     *
     * @param lines Array of lines. 'null' lines are ommited.
     * @return The multine strings composed of all lines.
     */
    static String lines (String... lines) {
        return lines == null?
            "" :
            Stream.of(lines)
                .filter (Objects::nonNull)
                .collect (joining (EOL));
    }
}
