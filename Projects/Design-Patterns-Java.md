---
# Java Design Patterns Implementation
---

## 1. Singleton Pattern

**Type:** Creational

Ensures that a class has only one instance and provides a global point of access to it. In this implementation, we use lazy initialization to create the `User` object only when it is needed.

```java
// User.java
package SingletonDP;
import java.util.UUID;

public class User {
    String name;
    String id;
    
    // Static variable reference of single_instance
    public static User userObject = new User("Rishav");

    private User(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }

    // Static method to create instance of Singleton class
    public static User createUser(String name) {
        if (userObject == null) {
            userObject = new User(name);
        }
        return userObject;
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', id='" + id + "'}";
    }
}

// demoSingleton.java
package SingletonDP;

public class demoSingleton {
    public static void main(String[] arg) {
        // Both calls return the same object instance
        User user1 = User.createUser("Kabir");
        System.out.println(user1);
        System.out.println("Hashcode: " + user1.hashCode());

        User user2 = User.createUser("Vicky");
        System.out.println(user2);
        System.out.println("Hashcode: " + user2.hashCode());
    }
}

```

---

## 2. Factory Pattern

**Type:** Creational

Defines an interface for creating an object, but lets subclasses decide which class to instantiate. It promotes loose coupling by separating the creation logic from the client code.

```java
// Developer.java (Interface)
package Factory;
public interface Developer {
    int getSalary();
}

// WebDeveloper.java
package Factory;
public class WebDeveloper implements Developer {
    int salary = 120000;
    @Override
    public int getSalary() {
        System.out.println("Web developer salary:" + salary);
        return salary;
    }
}

// AndroidDeveloper.java
package Factory;
public class AndroidDeveloper implements Developer {
    int salary = 200000;
    @Override
    public int getSalary() {
        System.out.println("Android Developer Salary: " + salary);
        return salary;
    }
}

// DeveloperFactory.java
package Factory;
public class DeveloperFactory {
    public static Developer createDeveloper(String type) {
        if (type.equals("web")) {
            return new WebDeveloper();
        }
        if (type.equals("android")) {
            return new AndroidDeveloper();
        }
        return null;
    }
}

// demoFactory.java
package Factory;
public class demoFactory {
    public static void main(String[] arg) {
        Developer dev1 = DeveloperFactory.createDeveloper("web");
        dev1.getSalary();

        Developer dev2 = DeveloperFactory.createDeveloper("android");
        dev2.getSalary();
    }
}

```

---

## 3. Abstract Factory Pattern

**Type:** Creational

Provides an interface for creating families of related or dependent objects without specifying their concrete classes. It is essentially a factory of factories.

```java
// Interfaces
public interface Employee {
    void getEmployeeDetails();
}

public interface AbstractEmployeeFactory {
    Employee createEmployee();
}

// Concrete Employees
class Teacher implements Employee {
    public void getEmployeeDetails() { System.out.println("Getting Teacher Details..."); }
}
class Manager implements Employee {
    public void getEmployeeDetails() { System.out.println("Getting Manager Details..."); }
}
class Developer implements Employee {
    public void getEmployeeDetails() { System.out.println("Getting Developer Details..."); }
}

// Concrete Factories
class TeacherFactory implements AbstractEmployeeFactory {
    public Employee createEmployee() { return new Teacher(); }
}
class ManagerFactory implements AbstractEmployeeFactory {
    public Employee createEmployee() { return new Manager(); }
}
class DeveloperFactory implements AbstractEmployeeFactory {
    public Employee createEmployee() { return new Developer(); }
}

// Factory Producer
class EmployeeFactory {
    public static Employee createEmployeeObject(AbstractEmployeeFactory factory) {
        return factory.createEmployee();
    }
}

// Demo
public class demoAbstractFactory {
    public static void main(String[] arg) {
        Employee e1 = EmployeeFactory.createEmployeeObject(new TeacherFactory());
        e1.getEmployeeDetails();

        Employee e2 = EmployeeFactory.createEmployeeObject(new ManagerFactory());
        e2.getEmployeeDetails();
        
        Employee e3 = EmployeeFactory.createEmployeeObject(new DeveloperFactory());
        e3.getEmployeeDetails();
    }
}

```
---
### Factory Method vs. Abstract Factory

| Feature | Factory Method | Abstract Factory |
| --- | --- | --- |
| **Primary Focus** | Creates **one** specific type of product. | Creates **families** of related or dependent products. |
| **How it Works** | Uses **Inheritance**. Subclasses override a method to decide which object to create. | Uses **Composition**. A separate factory object is passed to the client, which uses it to create products. |
| **Complexity** | **Simpler**. Great for when you only need to decouple the creation of one type of object. | **More Complex**. Involves creating multiple interfaces and classes for different product families. |
| **Flexibility** | Good for adding **new product types** (just add a new subclass). | Good for switching between **entire families** of products (e.g., Mac vs. Windows UI widgets) at runtime. |
| **Number of Methods** | Typically involves a **single method** (e.g., `createDeveloper()`). | Involves **multiple methods** for different products (e.g., `createButton()`, `createCheckbox()`). |
| **Analogy** | A **Visa Card** factory. It produces Credit Cards or Debit Cards (both are cards). | A **Furniture** factory. It produces Chairs, Tables, and Sofas (a whole set) in either "Victorian" or "Modern" style. |
| **When to use** | When a class doesn't know what exact sub-class it needs to create. | When your system needs to enforce that products created together must be compatible (e.g., a Mac button must go with a Mac scrollbar). |

### Summary of Differences

* **Factory Method** is about **one method** creating **one object**.
* *Example:* `DeveloperFactory.createDeveloper("web")` -> returns a `WebDeveloper`.


* **Abstract Factory** is about **an object** (the factory) creating **a suite of objects**.
* *Example:* `GUIFactory.createButton()` AND `GUIFactory.createMenu()` -> If the factory is `WinFactory`, you get a Windows Button *and* a Windows Menu.

---

## 4. Builder Pattern

**Type:** Creational

Separates the construction of a complex object from its representation, allowing the same construction process to create different representations. It is particularly useful when an object has many optional parameters.

```java
// User.java
package Builder;
import java.util.UUID;

public class User {
    private final String id;
    private final String name;
    private final int age;

    private User(UserBuilder userBuilder) {
        this.age = userBuilder.userAge;
        this.id = UUID.randomUUID().toString();
        this.name = userBuilder.username;
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', name='" + name + "', age=" + age + '}';
    }

    // Static Inner Builder Class
    public static class UserBuilder {
        String username;
        int userAge;

        public UserBuilder setAge(int age) {
            this.userAge = age;
            return this;
        }

        public UserBuilder setName(String name) {
            this.username = name;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}

// Usage Example
// User user = new User.UserBuilder().setName("John").setAge(25).build();

```

---

## 5. Iterator Pattern

**Type:** Behavioral

Provides a way to access the elements of an aggregate object sequentially without exposing its underlying representation.

```java
// Iterator Interface
public interface IteratorFun {
    boolean hasNext();
    User next();
}

// Concrete Iterator
class UserIterator implements IteratorFun {
    private List<User> userList;
    private int size;
    private int index = 0;

    public UserIterator(List<User> list) {
        this.userList = list;
        this.size = list.size();
    }
    public boolean hasNext() { return index < size; }
    public User next() { return userList.get(index++); }
}

// Aggregate Class
class UserManagement {
    static List<User> userList = new LinkedList<>();

    public static User createUser(String name) {
        User user = new User(name);
        userList.add(user);
        return user;
    }
    public static UserIterator getUserIterator() {
        return new UserIterator(userList);
    }
    public static List<User> getUserList() { return userList; }
}

// Demo
public class demoIterator {
    public static void main(String[] arg){
        UserManagement.createUser("Vicky");
        UserManagement.createUser("Vikram");
        
        UserIterator iterator = UserManagement.getUserIterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}

```

---

## 6. Observer Pattern

**Type:** Behavioral

Defines a one-to-many dependency between objects so that when one object (the Subject) changes state, all its dependents (Observers) are notified and updated automatically.

```java
// Interfaces
interface Subject {
    void subscribe(User user);
    void unSubscribe(User user);
    void uploadVideo(String videoTitle);
    void notifyUsers(String videoTitle);
}

interface Observer {
    void notified(String videoTitle, String channelName);
}

// Concrete Observer
class User implements Observer {
    String username;
    
    public User(String username) { this.username = username; }
    public String getUsername() { return username; }

    @Override
    public void notified(String videoTitle, String channelName) {
        System.out.println("Hey " + this.getUsername() + ", new video: " + videoTitle 
            + " on channel: " + channelName);
    }
}

// Concrete Subject
class YouTube implements Subject {
    public final String channelName;
    public final List<User> SUBSCRIBER_LIST = new ArrayList<>();

    public YouTube(String name) { this.channelName = name; }

    @Override
    public void subscribe(User user) {
        SUBSCRIBER_LIST.add(user);
    }
    @Override
    public void unSubscribe(User user) {
        SUBSCRIBER_LIST.remove(user);
    }
    @Override
    public void uploadVideo(String videoTitle) {
        notifyUsers(videoTitle);
    }
    @Override
    public void notifyUsers(String videoTitle) {
        for (User user : SUBSCRIBER_LIST) {
            user.notified(videoTitle, this.channelName);
        }
    }
}

// Demo
public class demoObserver {
    public static void main(String[] arg) {
        User vicky = new User("Vicky");
        User amit = new User("Amit");

        Subject channel1 = new YouTube("The Learning Cafe");
        channel1.subscribe(vicky);
        channel1.subscribe(amit);
        
        channel1.uploadVideo("Observer Design Pattern");
    }
}

```

---

Here are the other frequently asked and highly practical design patterns: **Strategy**, **Adapter**, **Decorator**, and **Command**.

These are formatted exactly like the previous ones, ready for your `README.md`.

---

## 7. Strategy Pattern

**Type:** Behavioral

Enables selecting an algorithm at runtime. Instead of implementing a single algorithm directly, code receives run-time instructions as to which in a family of algorithms to use.

```java
// Strategy Interface
package Strategy;

public interface PaymentStrategy {
    void pay(int amount);
}

// Concrete Strategy 1
package Strategy;

public class CreditCardStrategy implements PaymentStrategy {

    private String name;
    private String cardNumber;

    public CreditCardStrategy(String name, String cardNumber) {
        this.name = name;
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(int amount) {
        System.out.println(amount + " paid with Credit/Debit Card. Name: " + name);
    }
}

// Concrete Strategy 2
package Strategy;

public class PayPalStrategy implements PaymentStrategy {

    private String emailId;
    private String password;

    public PayPalStrategy(String email, String pwd) {
        this.emailId = email;
        this.password = pwd;
    }

    @Override
    public void pay(int amount) {
        System.out.println(amount + " paid using PayPal. Account: " + emailId);
    }
}

// Context Class
package Strategy;

public class ShoppingCart {

    public void checkout(int amount, PaymentStrategy paymentStrategy) {
        paymentStrategy.pay(amount);
    }
}

// Demo
package Strategy;

public class demoStrategy {

    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();

        // Paying via PayPal
        cart.checkout(1500, new PayPalStrategy("myemail@example.com", "mypwd"));

        // Paying via Credit Card
        cart.checkout(2000, new CreditCardStrategy("Rishav", "123456789"));
    }
}

```

---

## 8. Adapter Pattern

**Type:** Structural

Allows objects with incompatible interfaces to collaborate. It acts as a wrapper between two objects. It catches calls for one object and transforms them to format and interface recognizable by the second object.

```java
// Target Interface (What the client expects)
package Adapter;

public interface LightningPhone {
    void recharge();
    void useLightning();
}

// Adaptee (The incompatible interface)
package Adapter;

public interface MicroUsbPhone {
    void recharge();
    void useMicroUsb();
}

// Concrete Adaptee
package Adapter;

public class AndroidPhone implements MicroUsbPhone {
    @Override
    public void recharge() {
        System.out.println("Recharge started");
    }

    @Override
    public void useMicroUsb() {
        System.out.println("MicroUsb connected");
    }
}

// Concrete Target
package Adapter;

public class IPhone implements LightningPhone {
    private boolean connector;

    @Override
    public void recharge() {
        if (connector) {
            System.out.println("Recharge started");
            System.out.println("Recharge finished");
        } else {
            System.out.println("Connect Lightning first");
        }
    }

    @Override
    public void useLightning() {
        connector = true;
        System.out.println("Lightning connected");
    }
}

// Adapter Class
package Adapter;

public class LightningToMicroUsbAdapter implements MicroUsbPhone {
    
    private final LightningPhone lightningPhone;

    public LightningToMicroUsbAdapter(LightningPhone lightningPhone) {
        this.lightningPhone = lightningPhone;
    }

    @Override
    public void recharge() {
        lightningPhone.recharge();
    }

    @Override
    public void useMicroUsb() {
        System.out.println("MicroUsb connected");
        lightningPhone.useLightning();
    }
}

// Demo
package Adapter;

public class demoAdapter {

    public static void main(String[] args) {
        AndroidPhone android = new AndroidPhone();
        IPhone iPhone = new IPhone();

        System.out.println("--- Recharging Android with MicroUsb ---");
        rechargeMicroUsbPhone(android);

        System.out.println("--- Recharging iPhone with Lightning ---");
        iPhone.useLightning();
        iPhone.recharge();

        System.out.println("--- Recharging iPhone with Adapter ---");
        LightningToMicroUsbAdapter adapter = new LightningToMicroUsbAdapter(iPhone);
        rechargeMicroUsbPhone(adapter);
    }

    static void rechargeMicroUsbPhone(MicroUsbPhone phone) {
        phone.useMicroUsb();
        phone.recharge();
    }
}

```

---

## 9. Decorator Pattern

**Type:** Structural

Allows adding new behaviors to objects dynamically by placing them inside special wrapper objects. This is a flexible alternative to subclassing for extending functionality (e.g., adding toppings to a pizza or milk to coffee).

```java
// Component Interface
package Decorator;

public interface Coffee {
    String getDescription();
    double getCost();
}

// Concrete Component
package Decorator;

public class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Simple Coffee";
    }

    @Override
    public double getCost() {
        return 50.0;
    }
}

// Base Decorator
package Decorator;

public abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;

    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost();
    }
}

// Concrete Decorator 1
package Decorator;

public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Milk";
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 20.0;
    }
}

// Concrete Decorator 2
package Decorator;

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Sugar";
    }

    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 5.0;
    }
}

// Demo
package Decorator;

public class demoDecorator {
    public static void main(String[] args) {
        // Order: Simple Coffee + Milk + Sugar
        Coffee myCoffee = new SimpleCoffee();
        myCoffee = new MilkDecorator(myCoffee);
        myCoffee = new SugarDecorator(myCoffee);

        System.out.println("Order: " + myCoffee.getDescription());
        System.out.println("Cost: " + myCoffee.getCost());
    }
}

```

---

## 10. Command Pattern

**Type:** Behavioral

Turns a request into a stand-alone object that contains all information about the request. This transformation lets you pass requests as method arguments, queue them, or support undoable operations.

```java
// Command Interface
package Command;

public interface Command {
    void execute();
}

// Receiver (The object that does the actual work)
package Command;

public class Light {
    public void turnOn() {
        System.out.println("Light is ON");
    }
    public void turnOff() {
        System.out.println("Light is OFF");
    }
}

// Concrete Command (Turn On)
package Command;

public class TurnOnLightCommand implements Command {
    private Light light;

    public TurnOnLightCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOn();
    }
}

// Concrete Command (Turn Off)
package Command;

public class TurnOffLightCommand implements Command {
    private Light light;

    public TurnOffLightCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOff();
    }
}

// Invoker (Remote Control)
package Command;

public class RemoteControl {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton() {
        command.execute();
    }
}

// Demo
package Command;

public class demoCommand {
    public static void main(String[] args) {
        Light livingRoomLight = new Light();
        
        Command lightOn = new TurnOnLightCommand(livingRoomLight);
        Command lightOff = new TurnOffLightCommand(livingRoomLight);

        RemoteControl remote = new RemoteControl();

        // Turn Light On
        remote.setCommand(lightOn);
        remote.pressButton();

        // Turn Light Off
        remote.setCommand(lightOff);
        remote.pressButton();
    }
}

```

---

