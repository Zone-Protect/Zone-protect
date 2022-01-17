package org.zone.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.util.Nameable;
import org.spongepowered.configurate.ConfigurateException;
import org.zone.Identifiable;
import org.zone.ZonePlugin;
import org.zone.commands.system.ArgumentCommand;
import org.zone.commands.system.context.ErrorContext;
import org.zone.permissions.ZonePermission;
import org.zone.region.Zone;
import org.zone.region.flag.FlagType;
import org.zone.region.flag.entity.player.move.leaving.LeavingFlag;
import org.zone.region.group.Group;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

/**
 * Store house for all messages in this plugin {@link ZonePlugin} to make translation easier
 */
public final class Messages {

    private Messages() {
        throw new RuntimeException("Could not construct class");
    }

    public static Component getPlayerOnlyMessage() {
        return Component.text("Player only command").color(NamedTextColor.RED);
    }

    public static Component getLeftZoneMembersMessage(Identifiable identifiable) {
        return Component
                .text("You left the zone of ")
                .color(NamedTextColor.AQUA)
                .append(Component.text(identifiable.getName()).color(NamedTextColor.GOLD));
    }

    public static Component getZoneSavingError(ConfigurateException ce) {
        return Component.text("Error when saving: " + ce.getMessage()).color(NamedTextColor.RED);
    }

    public static Component getZoneRegionBuilderEnabled() {
        return Component
                .text("Region builder mode enabled. Run ")
                .color(NamedTextColor.AQUA)
                .append(Component.text("'/zone create bounds end'").color(NamedTextColor.GOLD));
    }

    public static Component getDuplicateNameError() {
        return Component.text("Cannot use that name").color(NamedTextColor.RED);
    }

    public static Component getMessageTag() {
        return Component.text("Message: ").color(NamedTextColor.AQUA);
    }

    public static Component getNoMessageSet() {
        return Component.text("No message set by user").color(NamedTextColor.RED);
    }

    public static Component getFlagMessageView(Component message) {
        return getMessageTag().append(message);
    }

    public static Component getUpdatedMessage(Identifiable type) {
        return Component
                .text("Updated ")
                .color(NamedTextColor.AQUA)
                .append(Component.text(type.getName()).color(NamedTextColor.GOLD));
    }

    //Universal Messages end
    //Universal only for some special classes
    public static Component getNotInRegion(Identifiable parent) {
        return Component
                .text("Region must be within '" + parent.getId() + "'")
                .color(NamedTextColor.RED);
    }

    public static Component getFormattedErrorMessage(String e) {
        return Component.text(e).color(NamedTextColor.RED);
    }

    public static Component getUnknownError() {
        return Component.text("Unknown error").color(NamedTextColor.RED);
    }

    public static Component getLoadingZonesStart() {
        return Component.text("|---|Loading Zones|---|").color(NamedTextColor.AQUA);
    }

    public static Component getZonesLoadingFrom(String zonesFolderPath) {
        return Component
                .text("|- Loading from '")
                .color(NamedTextColor.DARK_AQUA)
                .append(Component.text(zonesFolderPath).color(NamedTextColor.WHITE))
                .append(Component.text("'").color(NamedTextColor.DARK_AQUA));
    }

    public static Component getZonesLoadingFail(String keyFilePath) {
        return Component
                .text("Could not load zone of '")
                .color(NamedTextColor.RED)
                .append(Component.text(keyFilePath).color(NamedTextColor.DARK_RED))
                .append(Component
                        .text("'. Below is details on why (this is not a crash)")
                        .color(NamedTextColor.RED));
    }

    public static Component getZonesLoaded(Collection<Zone> zoneCollection) {
        return Component
                .text("|---|Loaded " + zoneCollection.size() + " Zones|---|")
                .color(NamedTextColor.AQUA);
    }

    public static Component getMissingPermissionForCommand(ArgumentCommand argumentCommand) {
        return Component
                .text(" You do not have permission for that command" + ". You require '")
                .color(NamedTextColor.RED)
                .append(Component
                        .text(argumentCommand
                                .getPermissionNode()
                                .map(ZonePermission::getPermission)
                                .orElse("Unknown"))
                        .color(NamedTextColor.DARK_RED))
                .append(Component.text("'").color(NamedTextColor.RED));
    }

    public static Component getNoZoneCreatedError() {
        return Component
                .text("A region needs to be started. Use ")
                .color(NamedTextColor.RED)
                .append(Component.text("'/zone create bounds ...'").color(NamedTextColor.DARK_RED));
    }

    public static Component getFailedToFindParentZone(Zone zone) {
        return Component
                .text("Could not find parent zone of ")
                .color(NamedTextColor.RED)
                .append(Component
                        .text("'" + zone.getParentId().orElse("Unknown") + "'")
                        .color(NamedTextColor.RED));
    }

    public static Component getCreatedZoneMessage(@SuppressWarnings("TypeMayBeWeakened") Zone zone) {
        return Component
                .text("Created a new zone of ")
                .color(NamedTextColor.AQUA)
                .append(Component.text(zone.getName()).color(NamedTextColor.GOLD));
    }

    public static Component getFailedToLoadFlag(@SuppressWarnings("TypeMayBeWeakened") FlagType<?> type) {
        return Component
                .text("Failed to load flag of ")
                .color(NamedTextColor.RED)
                .append(Component
                        .text(type.getId())
                        .color(NamedTextColor.DARK_RED)
                        .decorate(TextDecoration.BOLD));
    }

    public static Component getPageTooLow() {
        return Component.text("Page needs to be 1 or more").color(NamedTextColor.RED);
    }

    public static Component getGroupInfo(@SuppressWarnings("TypeMayBeWeakened") Group group) {
        return getGroupTag().append(Component.text(group.getName()).color(NamedTextColor.GOLD));
    }

    public static Component getTotalInfo(Collection<?> memberIds) {
        return getTotalTag().append(Component.text(memberIds.size()).color(NamedTextColor.GOLD));
    }

    public static Component getPagesInfo(int page) {
        return getPageTag().append(Component.text(page).color(NamedTextColor.GOLD));
    }

    private static Component getGroupTag() {
        return Component.text("Group: ").color(NamedTextColor.AQUA);
    }

    public static Component getTotalTag() {
        return Component.text("Total: ").color(NamedTextColor.AQUA);
    }

    public static Component getPageTag() {
        return Component.text("Page: ").color(NamedTextColor.AQUA);
    }

    public static Component getMemberTag() {
        return Component.text("Members: ").color(NamedTextColor.AQUA);
    }

    public static Component getNameTag() {
        return Component.text("Name: ").color(NamedTextColor.AQUA);
    }

    public static Component getVersionTag() {
        return Component.text("Version: ").color(NamedTextColor.AQUA);
    }

    public static Component getSourceTag() {
        return Component.text("Github: ").color(NamedTextColor.AQUA);
    }

    public static Component getZonesTag() {
        return Component.text("Zones: ").color(NamedTextColor.AQUA);
    }

    public static Component getBalanceTag() {
        return Component.text("Balance: ").color(NamedTextColor.AQUA);
    }

    public static Component getOwnerTag() {
        return Component.text("Owner: ").color(NamedTextColor.AQUA);
    }

    public static Component getZoneNameInfo(Identifiable zone) {
        return Messages.getNameInfo(zone.getName());
    }

    public static Component getMembersInfo(Collection<UUID> members) {
        return getMemberTag().append(Component.text(members.size()).color(NamedTextColor.GOLD));
    }

    public static Component getNameInfo(String pluginName) {
        return getNameTag().append(Component.text(pluginName).color(NamedTextColor.GOLD));
    }

    public static Component getVersionInfo(String pluginVersion) {
        return getVersionTag().append(Component.text(pluginVersion).color(NamedTextColor.GOLD));
    }

    public static Component getSourceInfo(String pluginGithub) {
        return getSourceTag().append(Component.text(pluginGithub).color(NamedTextColor.GOLD));
    }

    public static Component getZonesCountInfo(String amount) {
        return getZonesTag().append(Component.text(amount).color(NamedTextColor.GOLD));
    }

    public static Component getEnterLeavingMessage() {
        return Component.text("Enter leaving message").color(NamedTextColor.RED);
    }

    public static Component getZoneFlagLeavingMessageSet(Component message) {
        return Component
                .text("Leaving message is now: ")
                .color(NamedTextColor.AQUA)
                .append(message);
    }

    public static Component getLeavingMessage(LeavingFlag leavingFlag) {
        return getMessageTag().append(leavingFlag.getLeavingMessage());
    }

    public static Component getBalance(BigDecimal decimal) {
        return getBalanceTag().append(Component
                .text(decimal.toString())
                .color(NamedTextColor.GOLD));

    }

    public static Component getGreetingMessageSet(Component message) {
        return Component
                .text("Zone greetings message set to: ")
                .color(NamedTextColor.AQUA)
                .append(message);
    }

    public static Component getGreetingsMessageRemoved() {
        return Component
                .text("Zone greetings message removed from this zone!")
                .color(NamedTextColor.AQUA);
    }

    public static Component getAlreadyBeingEditedError() {
        return Component.text("Zone is already being edited").color(NamedTextColor.RED);
    }

    public static Component getMustBeWithinZoneToEditError() {
        return Component.text("Must be within the zone to start editing").color(NamedTextColor.RED);
    }

    public static Component getEntryName(Nameable user) {
        return getEntry(user.name());
    }

    public static Component getEntry(String name) {
        return Component.text("- " + name).color(NamedTextColor.AQUA);

    }

    public static Component getBalanceEntry(Currency currency, BigDecimal amount) {
        return Component
                .text("- ")
                .color(NamedTextColor.AQUA)
                .append(currency.symbol())
                .append(Component.text(" " + amount.toString()).color(NamedTextColor.AQUA));
    }

    public static Component getGroupStart(Identifiable zone) {
        return Component
                .text("----====[")
                .color(NamedTextColor.RED)
                .append(Component
                        .text(zone.getName())
                        .color(NamedTextColor.AQUA)
                        .append(Component.text("]====----").color(NamedTextColor.RED)));
    }

    public static Component getName(Identifiable zone) {
        return Component.text(zone.getName()).color(NamedTextColor.AQUA);
    }

    public static Component getError(ErrorContext error) {
        return Component.text(error.error()).color(NamedTextColor.RED);
    }

    public static Component getEnabledTag() {
        return Component.text("Enabled: ").color(NamedTextColor.AQUA);
    }

    public static Component getEnabledInfo(boolean check) {
        return getEnabledTag().append(Component.text(check).color(NamedTextColor.GOLD));
    }
}