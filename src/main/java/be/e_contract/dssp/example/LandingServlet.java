package be.e_contract.dssp.example;

import be.e_contract.dssp.client.DigitalSignatureServiceClient;
import be.e_contract.dssp.client.DigitalSignatureServiceSession;
import be.e_contract.dssp.client.SignResponseVerifier;
import be.e_contract.dssp.client.SignatureInfo;
import be.e_contract.dssp.client.VerificationResult;
import be.e_contract.dssp.client.exception.AuthenticationRequiredException;
import be.e_contract.dssp.client.exception.DocumentSignatureException;
import be.e_contract.dssp.client.exception.UnknownDocumentException;
import be.e_contract.dssp.client.exception.UnsupportedDocumentTypeException;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/landing")
public class LandingServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandingServlet.class);

    @Inject
    private DemoController demoController;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession httpSession = request.getSession();
        DigitalSignatureServiceSession session = (DigitalSignatureServiceSession) httpSession.getAttribute("DSS-SESSION");
        String signResponse = request.getParameter("SignResponse");
        try {
            SignResponseVerifier.checkSignResponse(signResponse, session);
        } catch (Exception ex) {
            LOGGER.error("error: " + ex.getMessage(), ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error verifying response: " + ex.getMessage());
            return;
        }

        DigitalSignatureServiceClient client = new DigitalSignatureServiceClient("https://www.e-contract.be/dss-ws/dss");
        byte[] signedDocument;
        try {
            signedDocument = client.downloadSignedDocument(session);
        } catch (UnknownDocumentException ex) {
            LOGGER.error("error: " + ex.getMessage(), ex);
            return;
        }
        VerificationResult verificationResult;
        try {
            verificationResult = client.verify("application/pdf", signedDocument);
        } catch (UnsupportedDocumentTypeException | DocumentSignatureException | AuthenticationRequiredException ex) {
            LOGGER.error("error: " + ex.getMessage(), ex);
            return;
        }
        this.demoController.setVerificationResult(verificationResult);
        httpSession.removeAttribute("DSS-SESSION");
        response.sendRedirect("./result.xhtml");
    }
}
