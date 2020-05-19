package be.e_contract.dssp.example;

import be.e_contract.dssp.client.VerificationResult;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SessionScoped
@Named("demoController")
public class DemoController implements Serializable {

    private VerificationResult verificationResult;

    public VerificationResult getVerificationResult() {
        return this.verificationResult;
    }

    public void setVerificationResult(VerificationResult verificationResult) {
        this.verificationResult = verificationResult;
    }
}
