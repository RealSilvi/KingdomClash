package it.unibo.model.data;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * A simple enum with every troop present in the game and some
 * utility functions that help handle troop types.
 */
public enum TroopType {
    /**
     * Axe troop.
     */
    AXE,
    /**
     * Sword troop.
     */
    SWORD,
    /**
     * Hammer troop.
     */
    HAMMER,
    /**
     * Mace troop.
     */
    MACE,
    /**
     * Axe defence troop.
     */
    AXE_DEFENCE,
    /**
     * Sword defence troop.
     */
    SWORD_DEFENCE,
    /**
     * Hammer defence troop.
     */
    HAMMER_DEFENCE,
    /**
     * Mace defence troop.
     */
    MACE_DEFENCE;

    private static final Map<TroopType, TroopType> COUNTER_LIST =
            Map.of(TroopType.AXE, TroopType.AXE_DEFENCE,
                    TroopType.SWORD, TroopType.SWORD_DEFENCE,
                    TroopType.HAMMER, TroopType.HAMMER_DEFENCE,
                    TroopType.MACE, TroopType.MACE_DEFENCE);

    private static final Random RN_GENERATOR = new Random();

    /**
     * @return Returns a random troop type everytime this method is called
     */
    public static TroopType getRandomTroop() {

        return Arrays.stream(values())
                .skip(RN_GENERATOR.nextInt(values().length))
                .iterator()
                .next();
    }

    /**
     * @param troopToCheck A specific troop type to check for
     * @return the other troop that counters the given troop
     */
    public static Optional<TroopType> getNullable(final TroopType troopToCheck) {
        if (COUNTER_LIST.containsKey(troopToCheck)) {
            return Optional.of(COUNTER_LIST.get(troopToCheck));
        }
        return COUNTER_LIST.entrySet().stream()
                .filter(troopEntry -> troopEntry.getValue() == troopToCheck)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    /**
     * Checks if the given troop is a defence.
     *
     * @param troopType troop to check
     * @return true if is a defence
     */
    public static boolean isDefense(final TroopType troopType) {
        return !COUNTER_LIST.containsKey(troopType);
    }
}
