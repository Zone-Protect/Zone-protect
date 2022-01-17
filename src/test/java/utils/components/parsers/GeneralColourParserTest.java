package utils.components.parsers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zone.utils.component.ZoneComponentParser;
import utils.CollectionAssert;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GeneralColourParserTest {

    @Test
    public void onClearTest() {
        //SETUP
        String testAgainst = "";

        //ACT
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ZoneComponentParser.fromString(testAgainst));

        //ASSERT
    }

    @Test
    public void onWithTagTest() {
        //SETUP
        String testAgainst = "<color name=RED>Test";

        //ACT
        Component result = ZoneComponentParser.fromString(testAgainst);

        //ASSERT
        Assertions.assertNotNull(result.color());
        Assertions.assertEquals(NamedTextColor.RED, result.color());
    }

    @Test
    public void onWithTagEngTest() {
        //SETUP
        String testAgainst = "<colour name=RED>Test";

        //ACT
        Component result = ZoneComponentParser.fromString(testAgainst);

        //ASSERT
        Assertions.assertNotNull(result.color());
        Assertions.assertEquals(NamedTextColor.RED, result.color());
    }

    @Test
    public void onWithInvalidTest() {
        //SETUP
        String testAgainst = "<invalid name=RED>Test";

        //ACT
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ZoneComponentParser.fromString(testAgainst));
    }

    @Test
    public void onToTag() {
        //SETUP
        Component toTest = Component.text("Test").color(NamedTextColor.RED);

        //ACT
        String result = ZoneComponentParser.toString(toTest);

        //ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals("<colour 255,85,85>Test", result);
    }

    @Test
    public void onToInvalidTag() {
        //SETUP
        Component toTest = Component.text("Test");

        //ACT
        String result = ZoneComponentParser.toString(toTest);

        //ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Test", result);
    }

    @Test
    public void onSuggestTestBlank() {
        //SETUP
        String toTest = "";

        //ACT
        @NotNull Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("<colour", results.iterator().next());
    }

    @Test
    public void onSuggestTestHalf() {
        //SETUP
        String toTest = "<colo";

        //ACT
        @NotNull Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("<colour", results.iterator().next());
    }

    @Test
    public void onSuggestTestColor() {
        //SETUP
        String toTest = "<color";
        List<String> expected = Arrays.asList("name=", "255,", "0,");

        //ACT
        @NotNull Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        CollectionAssert.collectionEquals(expected, results);
    }

    @Test
    public void onSuggestTestColour() {
        //SETUP
        String toTest = "<colour";
        List<String> expected = Arrays.asList("name=", "255,", "0,");

        //ACT
        @NotNull Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        CollectionAssert.collectionEquals(expected, results);
    }

    @Test
    public void onSuggestTestNamePart() {
        //SETUP
        String toTest = "<colour na";
        Collection<String> expected = Collections.singleton("name=");

        //ACT
        @NotNull Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        CollectionAssert.collectionEquals(expected, results);
    }

    @Test
    public void onSuggestTestColourNamePart() {
        //SETUP
        String toTest = "<colour name=RE";
        Collection<String> expected = Collections.singleton("red");

        //ACT
        @NotNull Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(1, results.size());
        CollectionAssert.collectionEquals(expected, results);
    }

    @Test
    public void onSuggestTestFullColourNameIncludesClosing() {
        //SETUP
        String toTest = "<colour name=RED";

        //ACT
        @NotNull Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertTrue(results.parallelStream().anyMatch(t -> t.endsWith(">")));
    }

    @Test
    public void onSuggestRGBPart0() {
        String toTest = "<colour";

        Collection<String> expected = Arrays.asList("255,", "0,", "name=");

        Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        CollectionAssert.collectionEquals(expected, results);
    }

    @Test
    public void onSuggestRGBPart1() {
        String toTest = "<colour 255, ";

        Collection<String> expected = Arrays.asList("255,", "0,");

        Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        CollectionAssert.collectionEquals(expected, results);
    }

    @Test
    public void onSuggestRGBPart2() {
        String toTest = "<colour 255, 10, ";

        Collection<String> expected = Arrays.asList("255>", "0>");

        Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        CollectionAssert.collectionEquals(expected, results);
    }

    @Test
    public void onSuggestRGBPart3() {
        String toTest = "<colour 255, 10, 2";

        Collection<String> expected = List.of(">");

        Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        CollectionAssert.collectionEquals(expected, results);
    }
}
