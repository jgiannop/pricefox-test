# Pricefox-Test

Dependency Injection (DI) is a design pattern used to implement IoC. It allows the creation of dependent objects outside of a class and provides those objects to a class through different ways. Using DI, we move the creation and binding of the dependent objects outside of the class that depends on them.

The Dependency Injection pattern involves 3 types of classes.

* Client Class: The client class (dependent class) is a class which depends on the service class
* Service Class: The service class (dependency) is a class that provides service to the client class.
* Injector Class: The injector class injects the service class object into the client class.