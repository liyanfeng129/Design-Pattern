package de.tum.cit.aet.pse;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EasyMockExtension.class)
class AdvancedNavigationServiceTest {

    // TODO make sure to specify the necessary elements for the mock object pattern
    // and to use the required mock type (NICE)
    @Mock(MockType.NICE)
    private RealTimePositionService realTimePositionService;

    @Mock
    private PEV pev;

    @TestSubject
    private NavigationService navigationService = new NavigationService();

    /*
     * With the testConnectionLoss() test method, you should test that
     * getInstructions(...) of NavigationService returns "connection lost" if the
     * current values of realTimePositionService are invalid (0 for int | null for
     * other values).
     * Hint: Use replay(realTimePositionService) to return the default values of a
     * NICE mock. Important: This test method must be named correctly!
     */
    @Test
    void testConnectionLoss() {
        Destination destination = new Destination(5, 10, "Home");

        expect(realTimePositionService.getX(pev)).andReturn(0);
        expect(realTimePositionService.getY(pev)).andReturn(0);
        expect(realTimePositionService.getDirection(pev)).andReturn(null);

        replay(realTimePositionService);

        String instructions = navigationService.getInstructions(pev, destination);
        assertEquals("connection lost", instructions);
    }

    /*
     * The testCorrectConnection() test method should demonstrate the use of
     * verify(). In this test you should intentionally raise a verify error by
     * testing the implementation of isCorrectlyConnected(...) in NavigationService,
     * but adding an additionall expected method call that is not executed by the
     * method.
     * Hint: This test case should fail when the verify(realTimePositionService) is
     * called. This is expected. Important: This test method must be named
     * correctly!
     */

    @Test
    void testCorrectConnection() {
        expect(realTimePositionService.getX(pev)).andReturn(5);
        expect(realTimePositionService.getY(pev)).andReturn(10);
        // Intentionally adding an extra expected call that won't be made
        expect(realTimePositionService.getDirection(pev)).andReturn(Direction.NORTH);

        replay(realTimePositionService);

        String isConnected = navigationService.isCorrectlyConnected(pev);
        assertEquals("correctly connected", isConnected);

        // This verify should fail due to the extra expected call
        verify(realTimePositionService);
    }
    // TODO make sure to initialize the attributes required for the tests

    // TODO implement testConnectionLoss()

    // TODO implement testCorrectConnection()
    // Remark: make sure to use the verify() functionality
    // & that the test fails due to a verify error
}
