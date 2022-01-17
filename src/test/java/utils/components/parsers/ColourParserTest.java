package utils.components.parsers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zone.utils.component.parsers.colour.ComponentColourParser;

import java.util.*;

public class ColourParserTest {

    @Test
    public void onClearTest() {
        //SETUP
        String testAgainst = "";
        Component plainComponent = Component.text(testAgainst);

        //ACT
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ComponentColourParser().withTag(testAgainst, plainComponent));

        //ASSERT
    }

    @Test
    public void onWithTagTest() {
        //SETUP
        String testAgainst = "<color name=RED>Test";
        Component plainComponent = Component.text("Test");

        //ACT
        Component result = new ComponentColourParser().withTag(testAgainst, plainComponent);

        //ASSERT
        Assertions.assertNotNull(result.color());
        Assertions.assertEquals(NamedTextColor.RED, result.color());
    }

    @Test
    public void onWithTagEngTest() {
        //SETUP
        String testAgainst = "<colour name=RED>Test";
        Component plainComponent = Component.text("Test");

        //ACT
        Component result = new ComponentColourParser().withTag(testAgainst, plainComponent);

        //ASSERT
        Assertions.assertNotNull(result.color());
        Assertions.assertEquals(NamedTextColor.RED, result.color());
    }

    @Test
    public void onWithInvalidTest() {
        //SETUP
        String testAgainst = "<invalid name=RED>Test";
        Component plainComponent = Component.text("Test");

        //ACT
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ComponentColourParser().withTag(testAgainst, plainComponent));
    }

    @Test
    public void onToTag() {
        //SETUP
        Component toTest = Component.text("Test").color(NamedTextColor.RED);

        //ACT
        String result = new ComponentColourParser().withTag(toTest, "");

        //ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals("<colour 255,85,85>", result);
    }

    @Test
    public void onToInvalidTag() {
        //SETUP
        Component toTest = Component.text("Test");

        //ACT
        String result = new ComponentColourParser().withTag(toTest, "");

        //ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals("", result);
    }

    @Test
    public void onSuggestTestBlank() {
        //SETUP
        String toTest = "";

        //ACT
        @NotNull Collection<String> results = new ComponentColourParser().getSuggestions(toTest);

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
        @NotNull Collection<String> results = new ComponentColourParser().getSuggestions(toTest);

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
        @NotNull Collection<String> results = new ComponentColourParser().getSuggestions(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(3, results.size());
        Assertions.assertEquals(expected, results);
    }

    @Test
    public void onSuggestTestColour() {
        //SETUP
        String toTest = "<colour";
        List<String> expected = Arrays.asList("name=", "255,", "0,");

        //ACT
        @NotNull Collection<String> results = new ComponentColourParser().getSuggestions(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(3, results.size());
        Assertions.assertEquals(expected, results);
    }

    @Test
    public void onSuggestTestNamePart() {
        //SETUP
        String toTest = "<colour na";
        Collection<String> expected = Collections.singleton("name=");

        //ACT
        @NotNull Collection<String> results = new ComponentColourParser().getSuggestions(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(expected, results);
    }

    @Test
    public void onSuggestTestColourNamePart() {
        //SETUP
        String toTest = "<colour name=RE";
        Collection<String> expected = new HashSet<>(Collections.singleton("red"));

        //ACT
        @NotNull Collection<String> results = new ComponentColourParser().getSuggestions(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(expected, results);
    }

    @Test
    public void onSuggestTestFullColourNameIncludesClosing() {
        //SETUP
        String toTest = "<colour name=RED";

        //ACT
        @NotNull Collection<String> results = new ComponentColourParser().getSuggestions(toTest);

        //ASSERT
        Assertions.assertFalse(results.isEmpty());
        Assertions.assertTrue(results.parallelStream().anyMatch(t -> t.endsWith(">")));
    }


}
