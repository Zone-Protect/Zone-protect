package org.zone.utils.component.parsers.colour;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.zone.utils.component.parsers.ComponentPartParser;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentColourParser implements ComponentPartParser {

    private Stream<NamedTextColor> getColours() {
        return Arrays
                .stream(NamedTextColor.class.getDeclaredFields())
                .filter(field -> Modifier.isPublic(field.getModifiers()))
                .filter(field -> field.getType().isAssignableFrom(NamedTextColor.class))
                .map(field -> {
                    try {
                        return (NamedTextColor) field.get(null);
                    } catch (IllegalAccessException e) {
                        //noinspection ReturnOfNull
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }

    @Override
    public boolean hasTag(String tag) {
        return tag.toLowerCase().startsWith("<colour") || tag.toLowerCase().startsWith("<color");
    }

    @Override
    public boolean hasTag(Component component) {
        return component.color() != null;
    }

    @Override
    public @NotNull Component withTag(@NotNull String tag, @NotNull Component component) {
        if (!this.hasTag(tag)) {
            throw new IllegalArgumentException("No colour tag");
        }
        if (tag.toLowerCase().contains("name=")) {
            Optional<NamedTextColor> opColor = this
                    .getColours()
                    .filter(textColor -> tag
                            .toLowerCase()
                            .contains(textColor.toString().toLowerCase()))
                    .findFirst();

            if (opColor.isPresent()) {
                return component.color(opColor.get());
            }
        }

        String[] split = tag.split(",");
        if (split.length != 3) {
            throw new IllegalArgumentException("Unknown arguments of " + tag);
        }

        String redSplit = split[0].trim();
        String greenSplit = split[1].trim();
        String blueSplit = split[2].trim();

        int redLength = 0;
        for (; redLength < redSplit.length(); redLength++) {
            char character = redSplit.charAt(redSplit.length() - 1 - redLength);
            if (Character.isDigit(character)) {
                continue;
            }
            break;
        }

        int blueLength = 0;
        for (; blueLength < blueSplit.length(); blueLength++) {
            char character = blueSplit.charAt(blueLength);
            if (Character.isDigit(character)) {
                continue;
            }
            break;
        }

        String redString = redSplit.substring(redSplit.length() - redLength);
        String blueString = blueSplit.substring(0, blueLength);
        try {
            int red = Integer.parseInt(redString);
            int green = Integer.parseInt(greenSplit);
            int blue = Integer.parseInt(blueString);
            return component.color(TextColor.color(red, green, blue));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unknown number for RGB on '" +
                    tag +
                    "' -> RGB: " +
                    "'" +
                    redString +
                    "', '" +
                    greenSplit +
                    "', '" +
                    blueString +
                    "'");
        }
    }

    @Override
    public @NotNull String withTag(Component component, @NotNull String message) {
        TextColor color = component.color();
        if (color == null) {
            return message;
        }
        return message + "<colour " + color.red() + "," + color.green() + "," + color.blue() + ">";
    }

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull String peek) {
        if (peek.toLowerCase().startsWith("<colour") || peek.toLowerCase().startsWith("<color")) {
            String noSpace = peek.toLowerCase().replaceAll(" ", "");
            if (noSpace.equals("<colour") || noSpace.equals("<color")) {
                return Arrays.asList("name=", "255,", "0,");
            }
            String withoutColourTag = noSpace.startsWith("<colour") ? noSpace.substring(7) : noSpace.substring(
                    6);
            if (withoutColourTag.contains(",") && !withoutColourTag.contains(">")) {
                String[] split = withoutColourTag.split(",");
                int commas = 0;
                for (int a = 0; a < withoutColourTag.length(); a++) {
                    if (withoutColourTag.charAt(a) == ',') {
                        commas++;
                    }
                }
                if (Stream.of(split).allMatch(entry -> {
                    try {
                        Integer.parseInt(entry);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })) {
                    if (split.length == 3 && !split[2].isEmpty()) {
                        return List.of(">");
                    }
                    if (commas == 2) {
                        return List.of("0>", "255>");
                    }
                    return List.of("0,", "255,");
                }
            }
            if (noSpace.startsWith("<colourname=") || noSpace.startsWith("<colorname=")) {
                List<NamedTextColor> cookies = this.getColours().collect(Collectors.toList());
                if (noSpace.equals("<colourname=") || noSpace.equals("<colorname=")) {
                    return cookies
                            .parallelStream()
                            .map(NamedTextColor::toString)
                            .collect(Collectors.toList());
                }
                String colourName = noSpace.startsWith("<colour") ? noSpace.substring(12) : noSpace.substring(
                        11);
                Set<String> colours = cookies
                        .parallelStream()
                        .map(NamedTextColor::toString)
                        .filter(name -> name.toLowerCase().startsWith(colourName))
                        .collect(Collectors.toSet());
                colours.addAll(cookies
                        .parallelStream()
                        .map(NamedTextColor::toString)
                        .filter(name -> name.toLowerCase().equals(colourName))
                        .map(s -> s + ">")
                        .collect(Collectors.toSet()));
                return colours;
            }
            if (noSpace.length() <= 12 &&
                    (noSpace.startsWith("<colourn") || noSpace.startsWith("<colorn"))) {
                return Collections.singleton("name=");
            }
            return Collections.emptyList();
        }
        if ("<color".startsWith(peek.toLowerCase())) {
            return Collections.singleton("<colour");
        }
        return Collections.emptyList();
    }
}
