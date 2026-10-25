package com.eldercare.modules.careplan_management.careplan_design.controller.v2;

import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.CreateCarePlanRequest;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.CreateCarePlanResponse;
import com.eldercare.modules.careplan_management.careplan_design.service.v2.CarePlanService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarePlanCreationControllerTest {
    private final CarePlanService service = mock(CarePlanService.class);
    private final UserRepository users = mock(UserRepository.class);
    private final CarePlanController controller = new CarePlanController(service, users);

    @BeforeEach
    @AfterEach
    void clearSession() {
        SecurityContextHolder.clearContext();
    }

    private void signIn() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("nurse@example.test", null, List.of()));
        UserEntity user = new UserEntity();
        user.setId(72L);
        when(users.findByEmailAndIsDeletedFalse("nurse@example.test")).thenReturn(Optional.of(user));
    }

    @Test
    void createUsesSessionActorInsteadOfClientSuppliedCreator() {
        signIn();
        CreateCarePlanRequest expected = new CreateCarePlanRequest(25L, 802L, 72L, null, null, null, null);
        when(service.createNewCarePlan(expected)).thenReturn(new CreateCarePlanResponse(1001L));
        var response = controller.createCarePlan(
                new CreateCarePlanRequest(25L, 802L, 999L, null, null, null, 3101L));
        assertEquals(1001L, response.getData().id());
        verify(service).createNewCarePlan(expected);
    }

    @Test
    void unsignedRequestDoesNotCreatePlan() {
        var error = assertThrows(ResponseStatusException.class, () -> controller.createCarePlan(
                new CreateCarePlanRequest(25L, 802L, null, null, null, null, null)));
        assertEquals(401, error.getStatusCode().value());
        verifyNoInteractions(service, users);
        assertEquals(401, controller.handleRequestError(error).getStatusCode().value());
    }

    @Test
    void invalidIdsDoNotReachService() {
        signIn();
        var error = assertThrows(ResponseStatusException.class, () -> controller.createCarePlan(
                new CreateCarePlanRequest(0L, 802L, null, null, null, null, null)));
        assertEquals(400, error.getStatusCode().value());
        verifyNoInteractions(service);
    }
}
