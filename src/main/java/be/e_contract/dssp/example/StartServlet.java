package be.e_contract.dssp.example;

import be.e_contract.dssp.client.DigitalSignatureServiceClient;
import be.e_contract.dssp.client.DigitalSignatureServiceSession;
import be.e_contract.dssp.client.PendingRequestFactory;
import be.e_contract.dssp.client.exception.ApplicationDocumentAuthorizedException;
import be.e_contract.dssp.client.exception.AuthenticationRequiredException;
import be.e_contract.dssp.client.exception.IncorrectSignatureTypeException;
import be.e_contract.dssp.client.exception.UnsupportedDocumentTypeException;
import be.e_contract.dssp.client.exception.UnsupportedSignatureTypeException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/sign")
public class StartServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DigitalSignatureServiceClient client = new DigitalSignatureServiceClient("https://www.e-contract.be/dss-ws/dss");
        byte[] pdf = IOUtils.toByteArray(StartServlet.class.getResourceAsStream("/document.pdf"));
        DigitalSignatureServiceSession session;
        try {
            session = client.uploadDocument("application/pdf", pdf);
        } catch (ApplicationDocumentAuthorizedException | AuthenticationRequiredException | IncorrectSignatureTypeException | UnsupportedDocumentTypeException | UnsupportedSignatureTypeException ex) {
            LOGGER.error("error: " + ex.getMessage(), ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error uploading PDF");
            return;
        }
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute("DSS-SESSION", session);

        String destination = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/landing";
        String pendingRequest = PendingRequestFactory.createPendingRequest(session, destination, "nl");

        httpSession.setAttribute("PENDING-REQUEST", pendingRequest);

        response.sendRedirect("./sign.jsp");
    }
}
