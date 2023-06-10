package logika;

/**
 * Used for storing the state of the GridGo while
 * testing possible moves.
 */
public record GoState(int prevState, int prevPrevState) {
}
