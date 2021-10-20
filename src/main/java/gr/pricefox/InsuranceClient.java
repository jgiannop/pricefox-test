package gr.pricefox;

import gr.pricefox.annotations.Autowired;
import gr.pricefox.annotations.Component;
import gr.pricefox.services.InsuranceProvicer;

//The Client (Dependant) Class
@Component
public class InsuranceClient {

    //The Service (Dependency) Class
    @Autowired
    private InsuranceProvicer insuranceProvicer;

    public void printInsuranceDetails() {
        String provider = insuranceProvicer.provideInsurance();
        System.out.println("Car insurance Provider: " + provider);
    }
}
