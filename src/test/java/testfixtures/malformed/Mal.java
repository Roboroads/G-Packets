package testfixtures.malformed;

/**
 * Compiled as-is, then copied by HandlerScannerTest with {@code Mal$X} renamed to {@code Mal_X}: a
 * nested class whose name doesn't start with its outer class's name and a {@code $}, as Scala and
 * Groovy can produce.
 */
public class Mal {
    public static class X {
    }
}
