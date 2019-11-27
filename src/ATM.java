import java.io.IOException;
import java.util.Scanner;

public class ATM {
    
    private Scanner in;
    private BankAccount activeAccount;
    private Bank bank;
    
    public static final int VIEW = 1;
    public static final int DEPOSIT = 2;
    public static final int WITHDRAW = 3;
    public static final int LOGOUT = 4;
    
    public static final int INVALID = 0;
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2; 
    public static final int OVERFILL = 3; 
    
	public static final String FIRST_NAME_WIDTH = "20";
	public static final String LAST_NAME_WIDTH = "30";
    
    /**
     * Constructs a new instance of the ATM class.
     */
    
    public ATM() {
        in = new Scanner(System.in);
        
        activeAccount = new BankAccount(1234, 123456789, 0, new User("Ryan", "Wilson"));
        
        try {
	    this.bank = new Bank();
        } catch (IOException e) {
	    // cleanup any resources (i.e., the Scanner) and exit
        }
    }
    
    public void startup() {
    	long accountNo = 0;
    	int pin = 0;
    	
        System.out.println("Welcome to the AIT ATM!");
        
        while (true) {
        	System.out.print("Account No.: ");
            String accountNumberString = in.nextLine();
            
            if (accountNumberString.equals("")) {
            	pin = getPin();
            	login(accountNo, pin);
            } else if (accountNumberString.equals("+")) {
            	createAccount();
            } else if (isAccountNumber(accountNumberString)) {
            	accountNo = Long.valueof(accountNumberString);
            	pin = getPin();
            	login(accountNo, pin);
            } else if (accountNumberString.equals("-1")) {
            	accountNo = -1;
            	pin = getPin();
            	login(accountNo, pin);
            } else {
            	pin = getPin();
            	login(accountNo, pin);
            }
//            System.out.print("PIN        : ");
//            int pin = in.nextInt();
//            
//            if (isValidLogin(accountNumberString, pin)) {
//                System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
//                
//                boolean validLogin = true;
//                while (validLogin) {
//                	switch (getSelection()) {
//                    	case VIEW: showBalance(); break;
//                    	case DEPOSIT: deposit(); break;
//                    	case WITHDRAW: withdraw(); break;
//                    	case LOGOUT: validLogin = false; break;
//                    	default: System.out.println("\nInvalid selection.\n"); break;
//                	}
//                }
//            } else {
//            	if (accountNumberString.equals("-1") && pin == -1) {
//            		shutdown();
//            	} else if (accountNumberString.equals("+")) {
//            		createAccount();
//            	} else {
//                    System.out.println("\nInvalid account number and/or PIN.\n");
//                }
//        
//            }
        
        }
    }
    
    public void login(long accountNo, int pin) {
    	if (isValidLogin(accountNo, pin)) {	
        	activeAccount = bank.login(accountNo, pin);
            System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
            boolean validLogin = true;
            
            while (validLogin) {
                switch (getSelection()) {
                    case VIEW: showBalance(); break;
                    case DEPOSIT: deposit(); break;
                    case WITHDRAW: withdraw(); break;
                    case TRANSFER: transfer(); break;
                    case LOGOUT: validLogin = false; in.nextLine(); break;
                    default: System.out.println("\nInvalid selection.\n"); break;
                }
            }
        } else {
            if (accountNo == -1 && pin == -1) {
                shutdown();
            } else {
                System.out.println("\nInvalid account number and/or PIN.\n");
            }
        }
    }
    
    public boolean isAccountNumber(String possibleAccNo) {
    	boolean isNumber = true;
    	for (int i = 0; i < possibleAccNo.length(); i++ ) {
            char char1 = possibleAccNo.charAt(i);
            if (!Character.isDigit(char1)) {
              isNumber = false;
            }
          }
    	return isNumber;
    }
    
    public int getPin() {
    	int pin = 0;
    	System.out.print("PIN        : ");
    	String pinPlaceHolder = in.nextLine();
    	if(pinPlaceHolder.isEmpty()) {
    		pin = 0;
    	}else if(isAccountNumber(pinPlaceHolder)){
    		pin = Integer.valueOf(pinPlaceHolder);
    	}else if(pinPlaceHolder.equals("-1")) {
    		pin = -1;
    	}       	
        return pin;
    }
    
    public boolean isValidLogin(String accountNumberString, int pin) {
        return accountNumberString.equals(activeAccount.getAccountNo()) && pin == activeAccount.getPin();
    }
    
    public int getSelection() {
        System.out.println("[1] View balance");
        System.out.println("[2] Deposit money");
        System.out.println("[3] Withdraw money");
        System.out.println("[4] Logout");
        
        if (in.hasNextInt()) {
        	return in.nextInt();
        } else {
        	in.nextLine();
        	return 6;
        }
    }
    
    public void showBalance() {
        System.out.println("\nCurrent balance: " + activeAccount.getBalance());
    }
    
    public void deposit() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
        
        activeAccount.deposit(amount);
        System.out.println();
    }
    
    public void withdraw() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
        
        activeAccount.withdraw(amount);
        System.out.println();
    }
    
    public void createAccount() {
    	int pin = 0;
    	System.out.print("\nFirst Name: ");
    	String firstName = in.nextLine();
    	
    	if (firstName != null && firstName.length() <= 20 && firstName.length() > 0) {
    		System.out.print("Last Name: ");
        	String lastName = in.nextLine();
        	if (lastName != null && lastName.length() <= 30 && lastName.length() > 0){
        		System.out.print("Pin: ");     
        		String pinPlaceHolder = in.nextLine();
            	if (pinPlaceHolder.isEmpty()) {
            		pin = 0;
            	} else if (isAccountNumber(pinPlaceHolder)) {
            		pin = Integer.valueOf(pinPlaceHolder);
            	}
            	
            	if (pin >= 1000 && pin <= 9999) {
            		newUser = new User(firstName, lastName);
                   	
                   	BankAccount newAccount = bank.createAccount(pin, newUser);
                   	System.out.println("\nThank you. Your account number is " + newAccount.getAccountNo() + ".");
                   	System.out.println("Please login to access your newly created account.\n");
                   	bank.update(newAccount);
                   	bank.save();
            	} else {
           			System.out.println("\nYou pin must be between 1000 and 9999.\n");
           		}         	
        	} else {
        		System.out.println("\nYour last name must be between 1 and 30 characters long\n");
        	}
    	} else {
    		System.out.println("\nYour first name must be between 1 and 20 characters long\n");
    	}
    }
    
    public void shutdown() {
        if (in != null) {
            in.close();
        }
        
        System.out.println("\nGoodbye!");
        System.exit(0);
    }
    
    /*
     * Application execution begins here.
     */
    
    public static void main(String[] args) {
        ATM atm = new ATM();
        
        atm.startup();
    }
}
