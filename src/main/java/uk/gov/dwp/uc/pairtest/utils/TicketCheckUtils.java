package uk.gov.dwp.uc.pairtest.utils;

import uk.gov.dwp.uc.pairtest.constants.ErrorMessages;
import uk.gov.dwp.uc.pairtest.constants.TicketDetails;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TicketCheckUtils {

    /**
     * @throws UnsupportedOperationException a utility class cannot be instantiated
     */
    private TicketCheckUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * @param accountId - Account ID of whom purchasing the ticket(s)
     * @param ticketTypeRequests ticket(s) requested {@link TicketTypeRequest}
     * @return error message if ticket check failed
     */
    public static String check(final Long accountId, final TicketTypeRequest... ticketTypeRequests) {
        String error;
            error = accountIdCheck(accountId);
        if (error.isEmpty()) {
            error = ticketCountCheck(ticketTypeRequests);
        }
        if (error.isEmpty()) {
            error = ticketWithAdultCheck(ticketTypeRequests);
        }
        return error;
    }

    /**
     * @param accountId - Account ID of whom purchasing the ticket(s)
     * @return error message if Account ID is invalid (All accounts with an id greater than zero are valid)
     */
    private static String accountIdCheck(final Long accountId) {
        String error = "";
        if (accountId <= 0) {
            error = ErrorMessages.ACCOUNT_ID_INVALID.getErrorMessage();
        }
        return error;
    }

    /**
     * @param ticketTypeRequests ticket(s) requested {@link TicketTypeRequest}
     * @return error message if number of ticket(s) requested are invalid
     * (Only a maximum of 20 tickets that can be purchased at a time.)
     */
    private static String ticketCountCheck(final TicketTypeRequest... ticketTypeRequests) {
        String error = "";
        int ticketCount = Arrays.stream(ticketTypeRequests)
                .map(TicketTypeRequest::getNoOfTickets)
                .mapToInt(Integer::intValue).sum();
        int maxTicketCount = TicketDetails.MAX_TICKET_COUNT.getTicketDetails();
        if (ticketCount < 1) {
            error = ErrorMessages.TICKET_COUNT_LESS_THAN_MIN.getErrorMessage();
        } else if (ticketCount > maxTicketCount) {
            error = ErrorMessages.TICKET_COUNT_EXCEED_MAX.getErrorMessage();
        }
        return error;
    }

    /**
     * @param ticketTypeRequests ticket(s) requested {@link TicketTypeRequest}
     * @return error message if ticket type only contain CHILD or INFANT
     * (Child and Infant tickets cannot be purchased without purchasing an Adult ticket)
     */
    private static String ticketWithAdultCheck(final TicketTypeRequest... ticketTypeRequests) {
        String error = "";
        List<TicketTypeRequest> ticketTypeWithAdult = Arrays.stream(ticketTypeRequests)
                .filter(ticketTypeRequest -> ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.ADULT)
                .filter(ticketTypeRequest -> ticketTypeRequest.getNoOfTickets() > 0).collect(Collectors.toList());
        if (ticketTypeWithAdult.isEmpty()) {
            error = ErrorMessages.TICKET_WITHOUT_ADULT.getErrorMessage();
        }
        return error;
    }
}
