public class StringCharSource implements CharSource {
    private final String string;
    private int pos;

    public StringCharSource(String string) {
        this.string = string;
    }

    @Override
    public boolean hasNext() {
        return pos < string.length();
    }

    @Override
    public char next() {
        return string.charAt(pos++);
    }

    @Override
    public void setPos(int pos) {
        if (pos >= string.length() || pos < 0) {
            throw new IllegalArgumentException("Cannot set pos: " + pos);
        }
        this.pos = pos;
    }

    @Override
    public int getPos() {
        return pos == 0 ? 0 : pos - 1;
    }

    @Override
    public IllegalArgumentException error(final String message) {
        return new IllegalArgumentException(pos + ": " + message);
    }
}