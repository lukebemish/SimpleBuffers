package simplebuffers.util;

public enum FilterState implements SidedState{
    WHITELIST(0),
    BLACKLIST(1),
    RR(2);

    private final int state;

    private FilterState(int state) {
        this.state = state;
    }

    public int getVal() {
        return this.state;
    }

    public static FilterState fromValStatic(int state) {
        switch(state) {
            case 0:
                return WHITELIST;
            case 1:
                return BLACKLIST;
            case 2:
                return RR;
        }
        return WHITELIST;
    }

    public FilterState fromVal(int state) {
        return FilterState.fromValStatic(state);
    }
}
