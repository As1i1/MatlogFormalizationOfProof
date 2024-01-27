import java.util.function.Predicate;

public abstract class BaseParser {

    public static final char END = 0;
    protected CharSource source;
    protected char current;

    BaseParser(String source) {
        this.source = new StringCharSource(source);
        take();
    }

    protected void skipWhitespace() {
        while (take(Character::isWhitespace)) {
        }
    }

    protected boolean take(char expected) {
        if (test(expected)) {
            take();
            return true;
        } else {
            return false;
        }
    }

    protected void expect(String expected) {
        for (int i = 0; i < expected.length(); i++) {
            if (!take(expected.charAt(i))) {
                throw error("Expected '" + expected + "', found '" + current + "'");
            }
        }
    }

    protected boolean take(Predicate<Character> expected) {
        if (test(expected)) {
            take();
            return true;
        } else {
            return false;
        }
    }

    protected int getPos() {
        return source.getPos();
    }

    protected void setPos(int pos) {
        source.setPos(pos);
        current = source.next();
    }

    protected char take() {
        final char result = current;
        current = source.hasNext() ? source.next() : END;
        return result;
    }

    protected boolean test(Predicate<Character> predicate) {
        return predicate.test(current);
    }

    protected boolean test(char expected) {
        return current == expected;
    }

    protected boolean eof() {
        return take(END);
    }

    protected IllegalArgumentException error(final String message) {
        return source.error(message);
    }

}