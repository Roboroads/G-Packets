package testfixtures.openenum;

import com.fasterxml.jackson.annotation.JsonCreator;
import me.roboroads.gearth.gpackets.support.Unused;
import me.roboroads.gearth.gpackets.support.schema.OpenIntEnum;

import java.util.List;

/**
 * Open enums for the {@code OpenIntEnum}, schema and packet reference tests. They live outside the
 * {@code me.roboroads.gearth.gpackets} scan root, so {@code OpenIntEnumTypesTest} and
 * {@code UnusedMarkerTest} don't gate them. {@link Clash} is broken on purpose.
 */
public final class OpenEnumFixtures {
    private OpenEnumFixtures() {
    }

    /** Declared out of value order, with one constant the client ignores. */
    public static final class Shade extends OpenIntEnum {
        public static final Shade DARK = new Shade(2);
        public static final Shade LIGHT = new Shade(1);
        @Unused("Only old clients draw it")
        @Deprecated
        public static final Shade FADED = new Shade(3);

        private Shade(int value) {
            super(value);
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static Shade of(int value) {
            return of(Shade.class, value, Shade::new);
        }

        public static List<Shade> values() {
            return values(Shade.class);
        }
    }

    public static final class Tone extends OpenIntEnum {
        public static final Tone LOW = new Tone(1);

        private Tone(int value) {
            super(value);
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static Tone of(int value) {
            return of(Tone.class, value, Tone::new);
        }

        public static List<Tone> values() {
            return values(Tone.class);
        }
    }

    /** Package-private, like an open enum an extension keeps to itself. */
    static final class Hidden extends OpenIntEnum {
        public static final Hidden ONE = new Hidden(1);

        private Hidden(int value) {
            super(value);
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static Hidden of(int value) {
            return of(Hidden.class, value, Hidden::new);
        }
    }

    /** {@link Hidden}, for tests outside this package. */
    public static final Class<? extends OpenIntEnum> HIDDEN = Hidden.class;

    /** No of(int): fine for named ids, an error for any other. */
    public static final class NoFactory extends OpenIntEnum {
        public static final NoFactory ONE = new NoFactory(1);

        private NoFactory(int value) {
            super(value);
        }
    }

    /** Two constants share a value. */
    public static final class Clash extends OpenIntEnum {
        public static final Clash FIRST = new Clash(1);
        public static final Clash SECOND = new Clash(1);

        private Clash(int value) {
            super(value);
        }

        public static List<Clash> values() {
            return values(Clash.class);
        }
    }
}
