package gr.pricefox.services;

import gr.pricefox.annotations.Autowired;
import gr.pricefox.annotations.Component;

@Component
public class ergoService implements InsuranceProvicer {

    @Autowired
    CarInsuranceProvider carInsuranceProvider;

    @Override
    public String provideInsurance() {
        System.out.println("Ergo provide Insurance");
        return carInsuranceProvider.provideCarService();
    }
}
