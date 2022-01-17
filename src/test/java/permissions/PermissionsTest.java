package permissions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zone.permissions.ZonePermissions;

public class PermissionsTest {

    @Test
    public void testNoUpper() {
        for (ZonePermissions permission : ZonePermissions.values()) {
            String permissionNode = permission.getPermission();
            for (int i = 0; i < permissionNode.length(); i++) {
                Assertions.assertFalse(Character.isUpperCase(permissionNode.charAt(i)),
                        "Node found upper character in " + permission.name());
            }
        }
    }

    @Test
    public void testNoDupes() {
        for (ZonePermissions permission : ZonePermissions.values()) {
            for (ZonePermissions compare : ZonePermissions.values()) {
                if (permission == compare) {
                    continue;
                }
                Assertions.assertNotEquals(permission.getPermission(),
                        compare.getPermission(),
                        "Found duped node in both " + permission.name() + " and " + compare.name());

            }
        }
    }
}
