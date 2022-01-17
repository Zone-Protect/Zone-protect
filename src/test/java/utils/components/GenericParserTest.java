package utils.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zone.utils.component.ZoneComponentParser;
import utils.CollectionAssert;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GenericParserTest {

    @Test
    public void testNoFormatToComponent() {
        String message = "test one";

        Component component = ZoneComponentParser.fromString(message);

        Assertions.assertNotNull(component);
        Assertions.assertEquals(message,
                PlainTextComponentSerializer.plainText().serialize(component));
        Assertions.assertNull(component.color());
        Assertions.assertFalse(component.hasStyling());
    }

    @Test
    public void testSingleColourFormatToComponent() {
        String message = "<colour name=RED>test one";

        Component component = ZoneComponentParser.fromString(message);

        Assertions.assertNotNull(component);
        Assertions.assertEquals("test one",
                PlainTextComponentSerializer.plainText().serialize(component));
        Assertions.assertNotNull(component.color());
        Assertions.assertEquals(NamedTextColor.RED, component.color());
        Assertions.assertTrue(component.hasStyling());
    }

    @Test
    public void testPartColourTagSuggestion() {
        String message = "test <colo";

        @NotNull Collection<String> suggestions = ZoneComponentParser.getSuggestion(message);

        CollectionAssert.collectionEquals(List.of("<colour"), suggestions);
    }

    @Test
    public void onSuggestColourRGBPart0() {
        String toTest = "<colour";

        Collection<String> expected = Arrays.asList("255,", "0,", "name=");

        Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        CollectionAssert.collectionEquals(expected, results);
    }

    @Test
    public void onSuggestColourRGBPart1() {
        String toTest = "<colour 255, ";

        Collection<String> expected = Arrays.asList("255,", "0,");

        Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        CollectionAssert.collectionEquals(expected, results);
    }

    @Test
    public void onSuggestColourRGBPart2() {
        String toTest = "<colour 255, 10, ";

        Collection<String> expected = Arrays.asList("255>", "0>");

        Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        CollectionAssert.collectionEquals(expected, results);
    }

    @Test
    public void onSuggestColourRGBPart3() {
        String toTest = "<colour 255, 10, 2";

        Collection<String> expected = List.of(">");

        Collection<String> results = ZoneComponentParser.getSuggestion(toTest);

        CollectionAssert.collectionEquals(expected, results);
    }
}
