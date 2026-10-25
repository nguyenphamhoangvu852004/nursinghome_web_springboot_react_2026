package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlan;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CarePlanEntityTests {

    @Test
    void create(){
        CarePlan c = CarePlan.create(1l,2l,3l);
        assertNull(c.getCurrentVersionId());
        assertNotNull(c.getCurrentVersion());
        assertEquals(1, c.getCurrentVersion().getVersionNumber());
        assertEquals("DRAFT", c.getCurrentVersion().getStatus());
        assertThrows(IllegalStateException.class, c::prepareInitialVersionForPersistence);
        c.setId(10L);
        var version = c.prepareInitialVersionForPersistence();
        assertEquals(10L, version.getCarePlanId());
        assertSame(c, version.getCarePlan());
        assertThrows(IllegalArgumentException.class, () -> c.linkPersistedCurrentVersion(version));
        version.setId(20L);
        c.linkPersistedCurrentVersion(version);
        assertEquals(20L, c.getCurrentVersionId());
        assertSame(version, c.getCurrentVersion());
    }
}
