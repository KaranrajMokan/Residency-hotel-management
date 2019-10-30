/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgpackage;

/**
 *
 * @author karanrajmokan
 */

import java.io.*;
import static java.lang.Thread.sleep;
import java.util.*;
import java.util.concurrent.*;


class Restaurant {
	Meal meal;
        ArrayList <Customers> details;
        private int size;
	ExecutorService exec = Executors.newCachedThreadPool();
	WaitPerson waitPerson = new WaitPerson(this,details);
	Chef chef = new Chef(this,size);
	public Restaurant(int sizes) {
                this.size=sizes;
                System.out.println(this.size);
		exec.execute(chef);
		exec.execute(waitPerson);
	}
}

class Meal {
	private final int orderNum;

	public Meal(int orderNum) {
		this.orderNum = orderNum;
	}

	public String toString() {
		return "Meal " + orderNum;
	}
}

class WaitPerson implements Runnable {
	private Restaurant restaurant;
        private ArrayList <Customers> details;
        static int count=0;
        
	public WaitPerson(Restaurant r,ArrayList <Customers> Cust) {
                this.details=Cust;
		restaurant = r;
	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				synchronized (this) {
					while (restaurant.meal == null)
						wait(); // ... for the chef to produce a meal
				}
				System.out.println(" got " + restaurant.meal);
				synchronized (restaurant.chef) {
					restaurant.meal = null;
					restaurant.chef.notifyAll(); // Ready for another
				}
			}
		} catch (InterruptedException e) {
			System.out.println("WaitPerson interrupted");
		}
	}
}

class Chef implements Runnable {
	private Restaurant restaurant;
        private ArrayList <Customers> details;
	private int count = 0;
        private int size;
	public Chef(Restaurant r,int sizes) {
		restaurant = r;
                this.size=sizes;
	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				synchronized (this) {
					while (restaurant.meal != null)
						wait(); // ... for the meal to be taken
				}
				if (++count == 10) {
					System.out.println("Out of food, closing");
					restaurant.exec.shutdownNow();
				}
				System.out.println("Order up! ");
				synchronized (restaurant.waitPerson) {
					restaurant.meal = new Meal(count);
					restaurant.waitPerson.notifyAll();
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}
		} catch (InterruptedException e) {
			System.out.println("Chef interrupted");
		}
	}
}

class Food implements Serializable
{
    int itemno;
    String itemname;
    int quantity;  
    float price;
   
    Food(int itemno,int quantity)
    {
        this.itemno=itemno;
        this.quantity=quantity;
        switch(itemno)
        {
            case 1: price=quantity*120;
                    this.itemname = "Biryani";
                    break;
            case 2: price=quantity*90;
                    this.itemname = "Pulaov";
                    break;
            case 3: price=quantity*80;
                    this.itemname = "Noodles";
                    break;
            case 4: price=quantity*100;
                    this.itemname = "Fried Rice";
                    break;
        }
    }
}

class Singleroom implements Serializable
{
    String name;
    String contact;
    int age;
    String gender;  
    ArrayList <Food> food =new ArrayList<>();

    Singleroom(String name,String contact,int age, String gender)
    {
        this.name=name;
        this.contact=contact;
        this.age = age;
        this.gender=gender;
    }
}

class Doubleroom extends Singleroom implements Serializable
{
    String name2;
    String contact2;
    int age2;
    String gender2;      

    Doubleroom(String name,String contact,int age,String gender,String name2,String contact2,int age2, String gender2)
    {
        super(name,contact,age,gender);
        this.name2=name2;
        this.contact2=contact2;
        this.age2 = age2;
        this.gender2=gender2;
    }
}

class Customers implements Comparable
{
    String name;
    String contact;
    int age;
    String gender;
    List <Integer> roomno = new ArrayList();
    int frequency;
   
    Customers(String name, String contact, int age, String gender, int roomno)
    {
        this.name = name;
        this.contact = contact;
        this.age = age;
        this.gender = gender;
        this.roomno.add(roomno);
        this.frequency = 1;
    }  
   
    public String toString()
    {
        System.out.println("Name        :"+this.name);
        System.out.println("Contact     :"+this.contact);
        System.out.println("Age         :"+this.age);
        System.out.println("Gender      :"+this.gender);
        System.out.println("Room Number :"+this.roomno);
        System.out.println("Frequency   :"+this.frequency);
        return "";
    }
   
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Customers)
        {
            Customers temp = (Customers) o;        
            if(this.name.equals(temp.name) && this.contact.equals(temp.contact) && this.gender.equals(temp.gender) && this.age == temp.age)
                return true;
        }
        return false;
    }
   
    public int compareTo (Object o)
    {
        Customers c = (Customers)o;
        return this.name.compareTo(c.name);
           
    }
}

class NotAvailable extends Exception
{
    @Override
    public String toString()
    {
        return "Not Available!";
    }
}

class holder implements Serializable
{
    Doubleroom arr1[] = new Doubleroom[10]; //Luxury
    Doubleroom arr2[] = new Doubleroom[20]; //Deluxe
    Singleroom arr3[] = new Singleroom[10]; //Luxury
    Singleroom arr4[] = new Singleroom[20]; //Deluxe
}


class SortbyAge implements Comparator<Customers>
{

    public int compare(Customers c1, Customers c2)
    {
        if(c1.age < c2.age)
            return -1;
        if(c1.age > c2.age)
            return 1;
        return 0;
    }
}

class SortbyFrequency implements Comparator<Customers>
{

    public int compare(Customers c1, Customers c2)
    {
        if(c1.frequency < c2.frequency)
            return -1;
        if(c1.frequency > c2.frequency)
            return 1;
        return 0;
    }
}

class Hotel
{
    static holder ob = new holder();
    static Scanner sc = new Scanner(System.in);
    static ArrayList <Customers> Cust = new ArrayList();
   
    ArrayList<Customers> getCustList(){
        return this.Cust;
    }
    static void CustDetails(int i,int rn)
    {
        String name,contact,gender,name2 = null,contact2 = null,gender2 = null;
        int age,age2 = 0;
        int flag=0;
        System.out.print("\nEnter customer name: ");
        name = sc.next();
        System.out.print("Enter contact number: ");
        contact=sc.next();
        System.out.print("Enter customer age");
        age = sc.nextInt();
        System.out.print("Enter gender: ");
        gender = sc.next();
        if(i<3)
        {
            System.out.print("Enter second customer name: ");
            name2 = sc.next();
            System.out.print("Enter contact number: ");
            contact2=sc.next();
            System.out.print("Enter customer age");
            age2 = sc.nextInt();
            System.out.print("Enter gender: ");
            gender2 = sc.next();
        }      
       
        switch (i)
        {
            case 1: ob.arr1[rn]=new Doubleroom(name,contact,age,gender,name2,contact2,age2,gender2);
                    Customers c1 = new Customers(name,contact,age,gender,rn+1);
                    Customers c2 = new Customers(name2,contact2,age2,gender2,rn+1);
                    for(Customers l: Cust)
                    {
                        flag=0;
                        if(l.equals(c1))
                        {    
                            l.frequency+=1;
                            l.roomno.add(rn+1);
                            flag=1;
                            break;
                        }
                    }
                    if(flag!=1)
                        Cust.add(c1);
                   
                    for(Customers l: Cust)
                    {
                        flag=0;
                        if(l.equals(c2))
                        {    
                            l.frequency+=1;
                            l.roomno.add(rn+1);
                            flag=1;
                            break;
                        }
                    }
                    if(flag!=1)
                        Cust.add(c2);
                   
                    break;
            case 2: ob.arr2[rn]=new Doubleroom(name,contact,age,gender,name2,contact2,age2,gender2);
                    Customers c3 = new Customers(name,contact,age,gender,rn+11);
                    Customers c4 = new Customers(name2,contact2,age2,gender2,rn+11);            
                    for(Customers l: Cust)
                    {
                        flag=0;
                        if(l.equals(c3))
                        {    
                            l.frequency+=1;
                            l.roomno.add(rn+11);
                            flag=1;
                            break;
                        }
                    }
                    if(flag!=1)
                        Cust.add(c3);
                   
                    for(Customers l: Cust)
                    {
                        flag=0;
                        if(l.equals(c4))
                        {    
                            l.frequency+=1;
                            l.roomno.add(rn+11);
                            flag=1;
                            break;
                        }
                    }                    
                    if(flag!=1)
                        Cust.add(c4);
                   
                    break;
            case 3: ob.arr3[rn]=new Singleroom(name,contact,age,gender);
                    Customers c5 = new Customers(name,contact,age,gender,rn+31);            

                    for(Customers l: Cust)
                    {
                        flag=0;
                        if(l.equals(c5))
                        {    
                            l.frequency+=1;
                            l.roomno.add(rn+31);
                            flag=1;
                            break;
                        }
                    }
                    if(flag!=1)
                        Cust.add(c5);
                    break;
            case 4: ob.arr4[rn]=new Singleroom(name,contact,age,gender);
                    Customers c6 = new Customers(name,contact,age,gender,rn+41);
                    for(Customers l: Cust)
                    {
                        flag=0;
                        if(l.equals(c6))
                        {    
                            l.frequency+=1;
                            l.roomno.add(rn+41);
                            flag=1;
                            break;
                        }
                    }
                    if(flag!=1)                    
                        Cust.add(c6);
                   
                    break;
            default:System.out.println("Wrong option");
                    break;
        }
    }
   
    static void displayCustomers()
    {
        if(Cust.size()<=0)
            System.out.println("There are NO customers");
        else
        {
            for(Customers x: Cust)
            {
                System.out.println(x);
            }
        }
    }
   
    static void bookroom(int i)
    {
        int j;
        int rn;
        System.out.println("\nChoose room number from : ");
        switch (i)
        {
            case 1:
                for(j=0;j<ob.arr1.length;j++)
                {
                    if(ob.arr1[j]==null)
                    {
                        System.out.print(j+1+",");
                    }
                }
                System.out.print("\nEnter room number: ");
               
                try
                {
                    rn=sc.nextInt();
                    rn--;
                    if(ob.arr1[rn]!=null)
                        throw new NotAvailable();
                    CustDetails(i,rn);
                }
                catch(NotAvailable e)
                {
                    System.out.println("Invalid Option");
                    return;
                }
                break;
           
            case 2:
                for(j=0;j<ob.arr2.length;j++)
                {
                    if(ob.arr2[j]==null)
                    {
                        System.out.print(j+11+",");
                    }
                }
                System.out.print("\nEnter room number: ");
               
                try
                {
                    rn=sc.nextInt();
                    rn=rn-11;
                    if(ob.arr2[rn]!=null)
                        throw new NotAvailable();
                    CustDetails(i,rn);
                }
                catch(Exception e)
                {
                    System.out.println("Invalid Option");
                    return;
                }
                break;
           
            case 3:
                for(j=0;j<ob.arr3.length;j++)
                {
                    if(ob.arr3[j]==null)
                    {
                        System.out.print(j+31+",");
                    }
                }
                System.out.print("\nEnter room number: ");
                try
                {
                    rn=sc.nextInt();
                    rn=rn-31;
                    if(ob.arr3[rn]!=null)
                        throw new NotAvailable();
                    CustDetails(i,rn);
                }
                catch(Exception e)
                {
                    System.out.println("Invalid Option");
                    return;
                }
                break;
           
            case 4:
                for(j=0;j<ob.arr4.length;j++)
                {
                    if(ob.arr4[j]==null)
                    {
                        System.out.print(j+41+",");
                    }
                }
                System.out.print("\nEnter room number: ");
               
                try
                {
                    rn=sc.nextInt();
                    rn=rn-41;
                    if(ob.arr4[rn]!=null)
                        throw new NotAvailable();
                    CustDetails(i,rn);
                }
                catch(Exception e)
                {
                   System.out.println("Invalid Option");
                    return;
                }
                break;
           
            default:
                System.out.println("Enter valid option");
                break;
        }
        System.out.println("Room Booked");
    }
   
    static void features(int i)
    {
        switch (i)
        {
            case 1: System.out.println("Number of double beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:4000 ");
                    break;
            case 2: System.out.println("Number of double beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:3000  ");
                    break;
            case 3: System.out.println("Number of single beds : 1\nAC : Yes\nFree breakfast : Yes\nCharge per day:2200  ");
                    break;
            case 4: System.out.println("Number of single beds : 1\nAC : No\nFree breakfast : Yes\nCharge per day:1200 ");
                    break;
            default:System.out.println("Enter valid option");
                    break;
        }
    }
   
    static void availability(int i)
    {
      int j,count=0;
        switch (i)
        {
            case 1:
                for(j=0;j<10;j++)
                {
                    if(ob.arr1[j]==null)
                        count++;
                }
                break;
               
            case 2:
                for(j=0;j<ob.arr2.length;j++)
                {
                    if(ob.arr2[j]==null)
                        count++;
                }
                break;
               
            case 3:
                for(j=0;j<ob.arr3.length;j++)
                {
                    if(ob.arr3[j]==null)
                        count++;
                }
                break;
               
            case 4:
                for(j=0;j<ob.arr4.length;j++)
                {
                    if(ob.arr4[j]==null)
                        count++;
                }
                break;
               
            default:
                System.out.println("Enter valid option");
                break;
        }
        System.out.println("Number of rooms available : "+count);
    }
   
    static void bill(int rn,int rtype)
    {
        double amount=0,fprice=0;
        String list[]={"Biryani","Pulaov","Noodles","Fried Rice"};
        System.out.println("\n*******");
        System.out.println(" Bill");
        System.out.println("*******");
               
        switch(rtype)
        {
            case 1:
                amount+=4000;
                System.out.println("\nRoom Charge - "+4000);
                System.out.println("\n===============");

                if((ob.arr1[rn].food.size())<0)
                    System.out.println("There are no FOOD charges");
                else
                {
                    System.out.println("Item   Quantity    Price");
                    System.out.println("-------------------------");
                    for(Food obb:ob.arr1[rn].food)
                    {
                        fprice+=obb.price;
                        String format = "%-10s%-10s%-10s%n";
                        System.out.printf(format,list[obb.itemno-1],obb.quantity,obb.price );
                    }
                    System.out.println("Food Charges:- "+fprice);
                    System.out.println("===============");
                    amount+=fprice;
                }
                break;
               
            case 2:
                amount+=3000;
                System.out.println("Room Charge - "+3000);
                System.out.println("\n===============");

                if((ob.arr2[rn].food.size())<0)
                    System.out.println("There are no FOOD charges");
                else
                {
                    System.out.println("Item   Quantity    Price");
                    System.out.println("-------------------------");
                    for(Food obb:ob.arr2[rn].food)
                    {
                        fprice+=obb.price;
                        String format = "%-10s%-10s%-10s%n";
                        System.out.printf(format,list[obb.itemno-1],obb.quantity,obb.price );
                    }
                    System.out.println("Food Charges:- "+fprice);
                    System.out.println("===============");
                    amount+=fprice;
                }
                break;
               
            case 3:
                amount+=2200;
                System.out.println("Room Charge - "+2200);
                System.out.println("\n===============");

                if((ob.arr3[rn].food.size())<0)
                    System.out.println("There are no FOOD charges");
                else
                {                
                    System.out.println("Item   Quantity    Price");
                    System.out.println("-------------------------");
                    for(Food obb:ob.arr3[rn].food)
                    {
                        fprice+=obb.price;
                        String format = "%-10s%-10s%-10s%n";
                        System.out.printf(format,list[obb.itemno-1],obb.quantity,obb.price );
                    }
                    System.out.println("Food Charges:- "+fprice);
                    System.out.println("===============");
                    amount+=fprice;
                }
                break;
               
            case 4:
                amount+=1200;
                System.out.println("Room Charge - "+1200);
                System.out.println("\n===============");
               
                if((ob.arr4[rn].food.size())<0)
                    System.out.println("There are no FOOD charges");
                else
                {
                    System.out.println("Item   Quantity    Price");
                    System.out.println("-------------------------");
                    for(Food obb:ob.arr4[rn].food)
                    {
                        fprice+=obb.price;
                        String format = "%-10s%-10s%-10s%n";
                        System.out.printf(format,list[obb.itemno-1],obb.quantity,obb.price );
                    }
                    System.out.println("Food Charges:- "+fprice);
                    System.out.println("===============");
                    amount+=fprice;
                }
                break;
               
            default:
                System.out.println("Not valid");
        }
        System.out.println("\nTotal Amount- "+amount);
    }
   
    static void checkout(int rn,int rtype)
    {
        int i,j;
        char w;
        List <Integer> t = new ArrayList();
        switch (rtype)
        {
            case 1:              
                if(ob.arr1[rn]!=null)
                {
                    System.out.println("Room used by "+ob.arr1[rn].name+" and "+ob.arr1[rn].name2);
                    for(Customers l: Cust)
                    {
                        for(Integer x: l.roomno)
                        {
                            if(x == rn+1)
                                t.add(Cust.indexOf(l));
                        }
                    }

                    Customers temp2 = Cust.get(t.get(1));
                    Customers temp4 = Cust.get(t.get(0));

                    Customers temp =  new Customers(ob.arr1[rn].name,ob.arr1[rn].contact,ob.arr1[rn].age,ob.arr1[rn].gender,rn+1);
                    Customers temp3 =  new Customers(ob.arr1[rn].name2,ob.arr1[rn].contact2,ob.arr1[rn].age2,ob.arr1[rn].gender2,rn+1);
                   
                    if(temp2.frequency > 1)
                    {
                        temp2.frequency=temp2.frequency-1;
                        Integer a = rn+1;
                        temp2.roomno.remove(a);
                        Cust.add(temp2);
                    }
                    Cust.remove(temp);
                   
                    if(temp4.frequency > 1)
                    {
                        temp4.frequency=temp4.frequency-1;
                        Integer a = rn+1;
                        temp4.roomno.remove(a);
                        Cust.add(temp4);
                    }
                    Cust.remove(temp3);  
                   
                    t.clear();
                }                
                else
                {    
                    System.out.println("Empty Already");
                    return;
                }
                System.out.println("Do you want to checkout ?(y/n)");
                w=sc.next().charAt(0);
                if(w=='y'|| w=='Y')
                {
                    bill(rn,rtype);
                    ob.arr1[rn]=null;
                    System.out.println("Checked out succesfully");
                }
                break;
               
            case 2:
                if(ob.arr2[rn]!=null)
                {
                    System.out.println("Room used by "+ob.arr2[rn].name+" and "+ob.arr2[rn].name2);                
                    for(Customers l: Cust)
                    {
                        for(Integer x: l.roomno)
                        {
                            if(x == rn+11)
                                t.add(Cust.indexOf(l));
                        }
                    }
                   
                    Customers temp2 = Cust.get(t.get(1));
                    Customers temp4 = Cust.get(t.get(0));

                    Customers temp =  new Customers(ob.arr1[rn].name,ob.arr1[rn].contact,ob.arr1[rn].age,ob.arr1[rn].gender,rn+11);
                    Customers temp3 =  new Customers(ob.arr1[rn].name2,ob.arr1[rn].contact2,ob.arr1[rn].age2,ob.arr1[rn].gender2,rn+11);
                   
                    if(temp2.frequency > 1)
                    {
                        temp2.frequency=temp2.frequency-1;
                        Integer a = rn+11;
                        temp2.roomno.remove(a);
                        Cust.add(temp2);
                    }
                    Cust.remove(temp);
                   
                    if(temp4.frequency > 1)
                    {
                        temp4.frequency=temp4.frequency-1;
                        Integer a = rn+11;
                        temp4.roomno.remove(a);
                        Cust.add(temp4);
                    }
                    Cust.remove(temp3);                      
                   
                    t.clear();
                }    
                else
                {    
                    System.out.println("Empty Already");
                    return;
                }
                System.out.println("Do you want to checkout ?(y/n)");
                w=sc.next().charAt(0);
                if(w=='y'|| w=='Y')
                {
                    bill(rn,rtype);
                    ob.arr2[rn]=null;
                    System.out.println("Checked out succesfully");
                }
                break;
               
            case 3:
                if(ob.arr3[rn]!=null)
                {
                    System.out.println("Room used by "+ob.arr3[rn].name);                
                    Customers temp =  new Customers(ob.arr3[rn].name,ob.arr3[rn].contact,ob.arr3[rn].age,ob.arr3[rn].gender,rn+31);
                    j = Cust.indexOf(temp);
                    Customers temp2 = Cust.get(j);
                    if(temp2.frequency > 1)
                    {
                        temp2.frequency=temp2.frequency-1;
                        Integer a = rn+31;
                        temp2.roomno.remove(a);
                        Cust.remove(temp);
                        Cust.add(temp2);
                    }
                    else
                        Cust.remove(temp);
                }
                else
                {    
                    System.out.println("Empty Already");
                    return;
                }
                System.out.println("Do you want to checkout ? (y/n)");
                w=sc.next().charAt(0);
                if(w=='y'|| w=='Y')
                {
                    bill(rn,rtype);
                    ob.arr3[rn]=null;
                    System.out.println("Checked out succesfully");
                }
                break;
               
            case 4:
                if(ob.arr4[rn]!=null)
                {
                    System.out.println("Room used by "+ob.arr4[rn].name);                
                    Customers temp =  new Customers(ob.arr4[rn].name,ob.arr4[rn].contact,ob.arr4[rn].age,ob.arr4[rn].gender,rn+41);
                    j = Cust.indexOf(temp);
                    Customers temp2 = Cust.get(j);
                    if(temp2.frequency > 1)
                    {
                        temp2.frequency=temp2.frequency-1;
                        Integer a = rn+41;
                        temp2.roomno.remove(a);
                        Cust.remove(temp);
                        Cust.add(temp2);
                    }
                    else
                        Cust.remove(temp);
                }
                else
                {    
                    System.out.println("Empty Already");
                    return;
                }
                System.out.println("Do you want to checkout ? (y/n)");
                w=sc.next().charAt(0);
                if(w=='y'||w=='Y')
                {
                    bill(rn,rtype);
                    ob.arr4[rn]=null;
                    System.out.println("Checked out succesfully");
                }
                break;
               
            default:
                System.out.println("\nEnter valid option : ");
                break;
        }
    }
   
    static void order(int rn,int rtype)
    {
        int i,q;
        char wish;
        String temp;
        try
        {
            System.out.println("\n==========\n   Menu:  \n==========\n\n1.Biryani\tRs.120\n2.Pulaov\tRs.90\n3.Noodles\tRs.80\n4.Fried Rice\tRs.100\n");
            do
            {
                i = sc.nextInt();
                System.out.print("Quantity- ");
                q=sc.nextInt();
           
                switch(rtype)
                {
                    case 1: ob.arr1[rn].food.add(new Food(i,q));
                            break;
                    case 2: ob.arr2[rn].food.add(new Food(i,q));
                            break;
                    case 3: ob.arr3[rn].food.add(new Food(i,q));
                            break;
                    case 4: ob.arr4[rn].food.add(new Food(i,q));
                            break;                                                
                }
               
                System.out.println("Do you want to order anything else ? (y/n)");
                temp = sc.next();
                wish = temp.charAt(0);
            } while(wish=='y'|| wish=='Y');  
        }
        catch(NullPointerException e)
        {
            System.out.println("\nRoom is not booked");
        }
        catch(Exception e)
        {
            System.out.println("Cannot be done");
        }
    }
   
}

class write implements Runnable
{
    holder ob;
    write(holder ob)
    {
        this.ob=ob;
    }
   
    @Override
    public void run()
    {
        try
        {
            FileOutputStream fout=new FileOutputStream("data");
            ObjectOutputStream oos=new ObjectOutputStream(fout);
            oos.writeObject(ob);
        }
        catch(Exception e)
        {
            System.out.println("Error in writing "+e);
        }            
    }
   
}

public class Package {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try
        {          
            File f = new File("data");
            if(f.exists())
            {
                FileInputStream fin=new FileInputStream(f);
                ObjectInputStream ois=new ObjectInputStream(fin);
                Hotel.ob=(holder)ois.readObject();
            }
       
            Scanner sc = new Scanner(System.in);
            int ch,ch2,ch3;
            char wish;
            x:
            do
            {
                System.out.println("\nEnter your choice :\n1.Display room details\n2.Display room availability \n3.Book\n4.Order room service food\n5.Checkout\n6.Customer Details\n7.Restaurant\n8.Exit\n");
                ch = sc.nextInt();
                switch(ch)
                {
                    case 1:    
                        System.out.println("\nChoose room type :\n1.Luxury Double Room \n2.Deluxe Double Room \n3.Luxury Single Room \n4.Deluxe Single Room\n");
                        ch2 = sc.nextInt();
                        Hotel.features(ch2);
                        break;
                       
                    case 2:
                        System.out.println("\nChoose room type :\n1.Luxury Double Room \n2.Deluxe Double Room \n3.Luxury Single Room\n4.Deluxe Single Room\n");
                        ch2 = sc.nextInt();
                        Hotel.availability(ch2);
                        break;
                   
                    case 3:
                        System.out.println("\nChoose room type :\n1.Luxury Double Room \n2.Deluxe Double Room \n3.Luxury Single Room\n4.Deluxe Single Room\n");
                        ch2 = sc.nextInt();
                        Hotel.bookroom(ch2);                    
                        break;
                       
                    case 4:
                        System.out.print("Room Number -");
                        ch2 = sc.nextInt();
                        if(ch2>60)
                            System.out.println("Room doesn't exist");
                        else if(ch2>40)
                            Hotel.order(ch2-41,4);
                        else if(ch2>30)
                            Hotel.order(ch2-31,3);
                        else if(ch2>10)
                            Hotel.order(ch2-11,2);
                        else if(ch2>0)
                            Hotel.order(ch2-1,1);
                        else
                            System.out.println("Room doesn't exist");
                        break;
                       
                    case 5:                
                        System.out.print("Room Number -");
                        ch2 = sc.nextInt();
                        if(ch2>60)
                            System.out.println("Room doesn't exist");
                        else if(ch2>40)
                            Hotel.checkout(ch2-41,4);
                        else if(ch2>30)
                            Hotel.checkout(ch2-31,3);
                        else if(ch2>10)
                            Hotel.checkout(ch2-11,2);
                        else if(ch2>0)
                            Hotel.checkout(ch2-1,1);
                        else
                            System.out.println("Room doesn't exist");
                        break;
                   
                    case 6:
                        System.out.println("Sorted(1) or Unsorted(2)");
                        ch2 = sc.nextInt();
                        if(ch2 == 1)
                        {
                            System.out.println("\n1.Sort by name,\n2.Sort by age,\n3.Sort by Frequency");
                            ch3 = sc.nextInt();
                            if(ch3==1)
                                Collections.sort(Hotel.Cust);
                            else if(ch3==2)
                                Collections.sort(Hotel.Cust,new SortbyAge());
                            else if(ch3==3)
                                Collections.sort(Hotel.Cust,new SortbyFrequency());
                            Hotel.displayCustomers();
                        }
                        else if(ch2 == 2)
                            Hotel.displayCustomers();
                        break;
                       
                    case 7:
                        //System.out.println(Hotel.Cust.size());
                        Restaurant t=new Restaurant(5);
                        sleep(1000);
                        break;
                    case 8:
                        break x;
               
                }
           
                System.out.println("\nContinue : (y/n)");
                wish=sc.next().charAt(0);
                if(!(wish=='y'||wish=='Y'||wish=='n'||wish=='N'))
                {
                    System.out.println("Invalid Option");
                    System.out.println("\nContinue : (y/n)");
                    wish=sc.next().charAt(0);
                }
           
            }while(wish=='y'||wish=='Y');    
       
            Thread t=new Thread(new write(Hotel.ob));
            t.start();
        }        
        catch(Exception e)
        {
            System.out.println("Not a valid input");
        }            
    }
}


