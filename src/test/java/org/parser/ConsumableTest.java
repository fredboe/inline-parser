package org.parser;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ConsumableTest {
    private final Consumable consumable1 = new Consumable("My name is Fred!");
    private final Consumable consumable2 = new Consumable("Test // hello \n Test",
            Consumable.Ignore.IGNORE_WHITESPACE, Consumable.Ignore.IGNORE_COMMENT);


    @Test
    public void Test_lookingAt_success() {
        Optional<Consumable.Match> optionalMatch1 = consumable1.lookingAt("My name");
        assertTrue(optionalMatch1.isPresent());
        assertEquals(optionalMatch1.get().matched(), "My name");
        assertEquals(consumable1, new Consumable(" is Fred!"));

        Optional<Consumable.Match> optionalMatch2 = consumable1.lookingAt(" is Fred!");
        assertTrue(optionalMatch2.isPresent());
        assertEquals(optionalMatch2.get().matched(), " is Fred!");
        assertEquals(consumable1, new Consumable(""));
    }

    @Test
    public void Test_lookingAt_failure() {
        Optional<Consumable.Match> optionalMatch = consumable1.lookingAt("\\d");
        assertEquals(optionalMatch, Optional.empty());
    }

    @Test
    public void Test_lookingAt_ignore() {
        Optional<Consumable.Match> optionalMatch1 = consumable2.lookingAt("Test");
        assertTrue(optionalMatch1.isPresent());
        assertEquals(optionalMatch1.get().matched(), "Test");
        assertEquals(consumable2, new Consumable("Test"));

        Optional<Consumable.Match> optionalMatch2 = consumable2.lookingAt("Test");
        assertTrue(optionalMatch2.isPresent());
        assertEquals(optionalMatch2.get().matched(), "Test");
        assertEquals(consumable2, new Consumable(""));
    }
}
