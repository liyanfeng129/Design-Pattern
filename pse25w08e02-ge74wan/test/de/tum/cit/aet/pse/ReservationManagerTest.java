package de.tum.cit.aet.pse;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * TODO:
 *  To use Mockito in your tests, use @ExtendWith
 *  with the MockitoExtension class.
 */
@ExtendWith(MockitoExtension.class)
class ReservationManagerTest {

    /**
     * You need two instances of LocalDateTime to write your tests.
     * Use these provided instances to make your code more readable.
     */
    private final LocalDateTime from = LocalDateTime.of(2020, 10, 10, 10, 10);
    private final LocalDateTime to = LocalDateTime.of(2020, 10, 10, 10, 11);

    /*
     * TODO:
     *  1. Declare a Rider and ReservationService Mock that will be
     *     used by the SUT ReservationManager
     *  2. Inject the Mocks into the SUT using the method discussed
     *     in the lecture
     *  3. Implement the test as described in the problem statement
     *
     */

    @Mock
    private ReservationService reservationServiceMock;

    @Spy
    @InjectMocks
    private ReservationManager reservationManagerSpy;

    @Captor
    private ArgumentCaptor<PEV> pevArgumentCaptor;

    @Mock
    private Rider riderMock;





    /*
     * This test verifies that lookupAvailablePEVsForTimeFrame method returns
     * a valid set of PEVs for a given time frame.
     *
     * Steps:
     * 1. Mock the ReservationService using @Mock annotation
     * 2. Inject the mock into ReservationManager using @InjectMocks
     * 3. Create a Rider with helmet (as required)
     * 4. Create test start and end times
     * 5. Create expected set of PEVs
     * 6. Configure the mock to return expected PEVs when findAvailablePEVs is called
     *    with the specific time parameters
     * 7. Call lookupAvailablePEVsForTimeFrame on ReservationManager
     * 8. Assert that returned set matches expected set
     * 9. Verify that findAvailablePEVs was called with correct parameters using ArgumentMatchers
     */
    @Test
    void testLookupAvailablePEVsReturnsValidSet() {
        // Create a real Rider and ReservationManager for this test
        DriversLicense license = new DriversLicense(LocalDateTime.MAX, "ABC");
        Rider rider = new Rider("Caio", 27, true, license);
        ReservationManager reservationManager = new ReservationManager(rider, reservationServiceMock);

        PEV pev = new EBike(80, "MUC");
        Set<PEV> pevSet = new HashSet<>();
        pevSet.add(pev);
        when(reservationServiceMock.findAvailablePEVs(from, to)).thenReturn(pevSet);
        Set<PEV> aSet = reservationManager.lookupAvailablePEVsForTimeFrame(from, to);
        assertEquals(pevSet, aSet);
        verify(reservationServiceMock).findAvailablePEVs(from, to);
    }

    /*
     * TODO:
     *  1. Use the ReservationManager and stub the implementation
     *     of lookupAvailablePEVsForTimeFrame()
     *  2. Declare a PEV ArgumentCaptor that is used to check if the
     *     returned PEV matches the one rented by the reserver
     *  3. Implement the test as described in the problem statement
     *
     * Test reserveFittingPEV No results
     * Complete the test case testReserveFittingPEVsReturnsRentedPEV.
     * This test case is supposed to verify
     * that reserveFittingPEV rents the PEV that it returns.
     * Because the implementation of lookupAvailablePEVsForTimeFrame changes all the time,
     * the behavior of this method must be stubbed.
     * Return an arbitrary set of PEVs
     * when lookupAvailablePEVsForTimeFrame is called with any parameters.
     * Verify that lookupAvailablePEVsForTimeFrame
     * is called with the same arguments as the original call
     * to reserveFittingPEV.
     * Also verify, that rent is called on the reserver. Capture the PEV object
     * passed to the reserver using an ArgumentCaptor annotation
     * and assert that it equals the PEV object
     * returned by the original call of reserveFittingPEV.
     */
    @Test
    void testReserveFittingPEVsReturnsRentedPEV() {

        // Create an arbitrary set of PEVs (at least one PEV)
        PEV pev = new EBike(80, "MUC");
        Set<PEV> pevSet = new HashSet<>();
        pevSet.add(pev);
        //  Stub lookupAvailablePEVsForTimeFrame to return the PEV set when called with any parameters
        doReturn(pevSet).when(reservationManagerSpy).lookupAvailablePEVsForTimeFrame(any(), any());
        //  Set the rider mock in the manager spy using setRider()
        reservationManagerSpy.setRider(riderMock);

        //  Call reserveFittingPEV(from, to) and store the returned PEV
        PEV fittingPEV = reservationManagerSpy.reserveFittingPEV(from, to);
        //  Verify that lookupAvailablePEVsForTimeFrame was called with from and to
        verify(reservationManagerSpy).lookupAvailablePEVsForTimeFrame(from, to);
        //  Verify that rent() was called on the rider and capture the PEV argument using the ArgumentCaptor
        verify(riderMock).rent(pevArgumentCaptor.capture(), eq(from), eq(to));
        //  Assert that the captured PEV equals the returned PEV from reserveFittingPEV
        assertEquals(pevArgumentCaptor.getValue(), fittingPEV);

    }
}
