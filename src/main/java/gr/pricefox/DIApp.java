package gr.pricefox;

public class DIApp {
    public static void main(String[] args) {
        DI.startApp(DIApp.class);
        InsuranceClient client=DI.getBean(InsuranceClient.class);
        client.printInsuranceDetails();

    }
}
