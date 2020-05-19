package be.e_contract.dssp.example;

import be.e_contract.dssp.client.DigitalSignatureServiceClient;
import be.e_contract.dssp.client.DigitalSignatureServiceSession;
import be.e_contract.dssp.client.SignResponseVerifier;
import be.e_contract.dssp.client.VerificationResult;
import be.e_contract.dssp.client.exception.AuthenticationRequiredException;
import be.e_contract.dssp.client.exception.ClientRuntimeException;
import be.e_contract.dssp.client.exception.DocumentSignatureException;
import be.e_contract.dssp.client.exception.KeyLookupException;
import be.e_contract.dssp.client.exception.SubjectNotAuthorizedException;
import be.e_contract.dssp.client.exception.UnknownDocumentException;
import be.e_contract.dssp.client.exception.UnsupportedDocumentTypeException;
import be.e_contract.dssp.client.exception.UserCancelException;
import be.e_contract.dssp.client.spi.Base64DecodingException;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

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
        } catch (ClientRuntimeException | KeyLookupException | SubjectNotAuthorizedException | UserCancelException
                | Base64DecodingException | IOException | JAXBException
                | MarshalException | XMLSignatureException | ParserConfigurationException | SAXException ex) {
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
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error downloading signed document: " + ex.getMessage());
            return;
        }
        VerificationResult verificationResult;
        try {
            verificationResult = client.verify("application/pdf", signedDocument);
        } catch (UnsupportedDocumentTypeException | DocumentSignatureException | AuthenticationRequiredException ex) {
            LOGGER.error("error: " + ex.getMessage(), ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error verifying signed document: " + ex.getMessage());
            return;
        }
        this.demoController.setVerificationResult(verificationResult);
        httpSession.removeAttribute("DSS-SESSION");
        response.sendRedirect("./result.xhtml");
    }
}
