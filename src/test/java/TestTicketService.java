import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.constants.ErrorMessages;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.utils.TicketCalculateUtils;
import uk.gov.dwp.uc.pairtest.utils.TicketCheckUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TestTicketService {
    TicketServiceImpl ticketService;
    @Mock
    TicketPaymentService ticketPaymentService;
    @Mock
    SeatReservationService seatReservationService;

    @Before
    public void setUp() {
        ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);
    }

    /**
     * Purchase tickets with an invalid Account ID
     * @result return ACCOUNT_ID_INVALID error messages
     */
    @Test
    public void accountIdInvalid() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        String error = TicketCheckUtils.check(0L, ticketTypeRequest);
        assertEquals(ErrorMessages.ACCOUNT_ID_INVALID.getErrorMessage(), error);
    }

    /**
     * Purchase tickets with an invalid number of ticket
     * @result return TICKET_COUNT_LESS_THAN_MIN error messages
     */
    @Test
    public void ticketNumberLessThanMin() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        String error = TicketCheckUtils.check(1L, ticketTypeRequest);
        assertEquals(ErrorMessages.TICKET_COUNT_LESS_THAN_MIN.getErrorMessage(), error);
    }

    /**
     * Purchase tickets with an invalid number of tickets (greater than maximum)
     * @result return TICKET_COUNT_EXCEED_MAX error messages
     */
    @Test
    public void ticketNumberExceedMax() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 21);
        String error = TicketCheckUtils.check(2L, ticketTypeRequest);
        assertEquals(ErrorMessages.TICKET_COUNT_EXCEED_MAX.getErrorMessage(), error);
    }

    /**
     * Purchase tickets with an invalid number of tickets (greater than maximum)
     * @result return TICKET_COUNT_EXCEED_MAX error messages
     */
    @Test
    public void ticketNumberExceedMaxMultipleType() {
        TicketTypeRequest[] ticketTypeRequests =
                {new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5),
                        new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20)};
        String error = TicketCheckUtils.check(2L, ticketTypeRequests);
        assertEquals(ErrorMessages.TICKET_COUNT_EXCEED_MAX.getErrorMessage(), error);
    }

    /**
     * Purchase tickets without Adult
     * @result return TICKET_WITHOUT_ADULT error messages
     */
    @Test
    public void ticketWithoutAdultChildOnly() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        String error = TicketCheckUtils.check(3L, ticketTypeRequest);
        assertEquals(ErrorMessages.TICKET_WITHOUT_ADULT.getErrorMessage(), error);
    }

    /**
     * Purchase tickets without Adult
     * @result return TICKET_WITHOUT_ADULT error messages
     */
    @Test
    public void ticketWithoutAdultInfantOnly() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        String error = TicketCheckUtils.check(4L, ticketTypeRequest);
        assertEquals(ErrorMessages.TICKET_WITHOUT_ADULT.getErrorMessage(), error);
    }

    /**
     * Purchase tickets without Adult
     * @result return TICKET_WITHOUT_ADULT error messages
     */
    @Test
    public void ticketWithoutAdultInfantChild(){
        TicketTypeRequest[] ticketTypeRequests =
                {new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2),
                        new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2)};
        String error = TicketCheckUtils.check(5L, ticketTypeRequests);
        assertEquals(ErrorMessages.TICKET_WITHOUT_ADULT.getErrorMessage(), error);
    }

    /**
     * Check ticket price calculation
     * @result 20 Adult tickets * 20 = 400
     */
    @Test
    public void ticketPriceCheckAdultOnly() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20);
        int ticketPrice = TicketCalculateUtils.calculateTicketTotalPrice( ticketTypeRequest);
        assertEquals(400, ticketPrice);
    }

    /**
     * Check ticket price calculation
     * @result (2 Adult *20) + (2 Child * 10) = 60
     */
    @Test
    public void ticketPriceCheckAdultChild() {
        TicketTypeRequest[] ticketTypeRequests =
                {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                        new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2)};
        int ticketPrice = TicketCalculateUtils.calculateTicketTotalPrice( ticketTypeRequests);
        assertEquals(60, ticketPrice);
    }

    /**
     * Check ticket price calculation
     * @result (2 Adult *20) + (2 Infant * 0) = 40
     */
    @Test
    public void ticketPriceCheckAdultInfant() {
        TicketTypeRequest[] ticketTypeRequests =
                {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                        new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)};
        int ticketPrice = TicketCalculateUtils.calculateTicketTotalPrice( ticketTypeRequests);
        assertEquals(40, ticketPrice);
    }

    /**
     * Check ticket price calculation
     * @result (2 Adult *20) + (2 Child * 10) + (2 Infant * 0) = 60
     */
    @Test
    public void ticketPriceCheckAdultChildInfant() {
        TicketTypeRequest[] ticketTypeRequests =
                {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                        new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                        new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)};
        int ticketPrice = TicketCalculateUtils.calculateTicketTotalPrice( ticketTypeRequests);
        assertEquals(60, ticketPrice);
    }

    /**
     * Check ticket count
     * @result 13 Adult + 2 Child + 50 Infant (Does not count) = 15
     */
    @Test
    public void ticketCountCheck(){
        TicketTypeRequest[] ticketTypeRequests =
                {new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 50),
                        new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 13)};
        int ticketPrice = TicketCalculateUtils.calculateTotalNumberOfSeats( ticketTypeRequests);
        assertEquals(15, ticketPrice);
    }

    /**
     * Check ticket count
     * @result 13 Adult + 50 Infant (Does not count) = 13
     */
    @Test
    public void ticketCountCheckAdultInfant(){
        TicketTypeRequest[] ticketTypeRequests =
                {new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 50),
                        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 13)};
        int ticketPrice = TicketCalculateUtils.calculateTotalNumberOfSeats( ticketTypeRequests);
        assertEquals(13, ticketPrice);
    }

    /**
     * Purchasing tickets with invalid Account ID
     * @result return ACCOUNT_ID_INVALID error message
     */
    @Test
    public void purchasingTicketWithInvalidAccountIDException() {
        assertThatExceptionOfType(InvalidPurchaseException.class)
                .isThrownBy(() -> ticketService.purchaseTickets(0L,
                        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 21)))
                .withMessage(ErrorMessages.ACCOUNT_ID_INVALID.getErrorMessage());
    }

    /**
     * Purchasing tickets without Adult
     * @result return TICKET_WITHOUT_ADULT error message
     */
    @Test
    public void purchasingTicketWithoutAdultException() {
        assertThatExceptionOfType(InvalidPurchaseException.class)
                .isThrownBy(() -> ticketService.purchaseTickets(2L,
                        new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 20)))
                .withMessage(ErrorMessages.TICKET_WITHOUT_ADULT.getErrorMessage());
    }

    /**
     * Purchasing tickets without Adult
     * @result return TICKET_WITHOUT_ADULT error message
     */
    @Test
    public void purchasingTicketWithoutAdultTwoException() {
        assertThatExceptionOfType(InvalidPurchaseException.class)
                .isThrownBy(() -> ticketService.purchaseTickets(2L,
                        new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 20)))
                .withMessage(ErrorMessages.TICKET_WITHOUT_ADULT.getErrorMessage());
    }

    /**
     * Purchasing tickets
     * @result no error message
     */
    @Test
    public void noException() {
        ticketService.purchaseTickets(1L,
                        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20));
    }
}
