package de.tum.cit.aet.pse;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EasyMockExtension.class)
class NavigationServiceTest {

    // TODO make sure to specify the necessary elements for the mock object pattern
    // and to use the required mock type (STRICT)
    @Mock(MockType.STRICT)
    private RealTimePositionService realTimePositionService;

    @Mock
    private PEV pev;

    @TestSubject
    private NavigationService navigationService = new NavigationService();

    @Test
    void testDestinationReached() {
        Destination destination = new Destination(5, 10, "Home");

        expect(realTimePositionService.getX(pev)).andReturn(5);
        expect(realTimePositionService.getY(pev)).andReturn(10);
        expect(realTimePositionService.getDirection(pev)).andReturn(Direction.NORTH);

        replay(realTimePositionService);

        String instructions = navigationService.getInstructions(pev, destination);
        assertEquals("destination reached", instructions);
    }

    /*
     * getDirectionDistance(...) of
     * NavigationService returns "drive south for Y more kilometers"
     * if the current Y-coordinate is greater than the destination
     * Y-coordinate and the PEV is driving south.
     */

    @Test
    void testDirectionDistance() {
        Destination destination = new Destination(5, 10, "Office");
        expect(realTimePositionService.getDirection(pev)).andReturn(Direction.SOUTH);
        expect(realTimePositionService.getX(pev)).andReturn(5);
        expect(realTimePositionService.getY(pev)).andReturn(15);
        replay(realTimePositionService);
        String distance = navigationService.getDirectionDistance(pev, destination);
        assertEquals("drive south for 5 more kilometers", distance);
    }
    // TODO make sure to initialize the attributes required for the tests

    // TODO implement testDestinationReached()

    // TODO implement testDirectionDistance()
}
