package gr.pricefox.services;

import gr.pricefox.annotations.Component;

@Component
public class ergoCarService implements CarInsuranceProvider {

    @Override
    public String provideCarService() {
        return "Ergo car Service";
    }
}
