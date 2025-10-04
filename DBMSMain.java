///////////////////////////////////////////////////////////////////////////////////////////////////
//          DBMS Project
///////////////////////////////////////////////////////////////////////////////////////////////////



import java.util.*;

class Student
{
    public int Rno;
    public String Name;
    public int Age;
    public int Marks;

    public static int Generator;

    static
    {
        Generator = 0;
    }

    public Student(String str,int X,int Y)
    {
        this.Rno = ++Generator;
        this.Name = str;
        this.Age = X;
        this.Marks = Y;
    }

    public void Display()
    {
        System.out.println(this.Rno + " " + this.Name + " " + this.Age + " " + this.Marks);
    }
}

class DBMS
{
    public LinkedList <Student> lobj;

    public DBMS()
    {
        lobj = new LinkedList<Student>();
    }

    public void StartDBMS()
    {
        System.out.println(" DBMS started successfully...");
        System.out.println("Table Schema created successfully...");

        String Query;
        String Tokens[];
        Scanner sobj = new Scanner(System.in);
        int TokensCount = 0;

        while(true)
        {
            System.out.print(" DBMS :");
            Query = sobj.nextLine();

            Tokens = Query.split(" ");

            TokensCount = Tokens.length;

            if(TokensCount == 1)
            {
                if("exit".equals(Tokens[0]))
                {
                    System.out.println("Thank You for using  DBMS...");
                    break;
                }
            }
            else if(TokensCount == 2)
            {}
            else if(TokensCount == 3)
            {}
            else if(TokensCount == 4)
            {
                if("select".equals(Tokens[0]))
                {
                    SelectFrom();
                }
            }
            else if(TokensCount == 5)
            {
                if("select".equals(Tokens[0]))
                {
                    if("MAX".equals(Tokens[1]))
                    {
                        System.out.println("Maximum marks are : "+Aggregate_Max());
                    }
                    else if("MIN".equals(Tokens[1]))
                    {
                        System.out.println("Minimum marks are : "+Aggregate_Min());
                    }
                    else if("AVG".equals(Tokens[1]))
                    {
                        System.out.println("Average marks are : "+Aggregate_Avg());
                    }
                    else if("SUM".equals(Tokens[1]))
                    {
                        System.out.println("Sum of marks are : "+Aggregate_Sum());
                    }
                }
            }
            else if(TokensCount == 6)
            {}
            else if(TokensCount == 7)
            {
                if("insert".equals(Tokens[0]))
                {
                    InsertIntoTable(Tokens[4],Integer.parseInt(Tokens[5]),Integer.parseInt(Tokens[6]));
                }
                else if("delete".equals(Tokens[0]))
                {
                    DeleteFrom(Integer.parseInt(Tokens[6]));
                }
            }
        }

    }

    //insert into table student values(_____,______,_________);
    public void InsertIntoTable(String name,int age,int marks)
    {
        Student sobj = new Student(name,age,marks);
        lobj.add(sobj);
    }

    //select * from student
    public void SelectFrom()
    {
        System.out.println("Records from the student table are : ");

        for(Student sref : lobj)
        {
            sref.Display();
        }
    }

    //select * from student where Rno = 11
    public void SelectFrom(int no)
    {
        System.out.println("Records from the student table  are : ");

        for(Student sref : lobj)
        {
            if(sref.Rno == no)
            {
                sref.Display();
                break;
            }
        }
    }

    //select * from student where Name = Rahul
    public void SelectFrom(String str)
    {
        System.out.println("Records from the student table  are : ");

        for(Student sref : lobj)
        {
            if(str.equals(sref.Name))
            {
                sref.Display();
                break;
            }
        }
    }

    //select MAX(marks) from student
    public int Aggregate_Max()
    {
        int iMax = 0;
        for(Student sref : lobj)   
        {
            if(sref.Marks > iMax)
            {
                iMax = sref.Marks;
            }
        }

        return iMax;
    }

    //select MIN(marks) from student
    public int Aggregate_Min()
    {
        Student temp = lobj.get(0);
        int iMin = temp.Marks;

        for(Student sref : lobj)   
        {
            if(sref.Marks < iMin)
            {
                iMin = sref.Marks;
            }
        }

        return iMin;
    }

    //select MIN(marks) from student
    public int Aggregate_Sum()
    {
        int iSum = 0;

        for(Student sref : lobj)   
        {
            iSum = iSum + sref.Marks;
        }
        return iSum;
    }

    //select AVG(marks) from student
    public int Aggregate_Avg()
    {
        int iSum = 0;

        for(Student sref : lobj)   
        {
            iSum = iSum + sref.Marks;
        }
        return (iSum/(lobj.size()));
    }

    //delete from student where Rno = 2
    public void DeleteFrom(int no)
    {
        int i = 0;

        for(Student sref : lobj)
        {
            if(sref.Rno == no)
            {
                lobj.remove(i);
                break;
            }
            i++;
        }
    }

}

class DBMSMain
{
    public static void main(String arg[])
    {
        DBMS obj = new DBMS();
        obj.StartDBMS();

/*
        obj.InsertIntoTable("Rahul",23,89);
        obj.InsertIntoTable("Sagar",22,69);
        obj.InsertIntoTable("Sayali",23,79);
        obj.InsertIntoTable("Amit",20,90);
        obj.InsertIntoTable("Sumit",21,88);

        obj.SelectFrom();
        obj.SelectFrom(4);
        obj.SelectFrom("Rahul");

        System.out.println("Maximum marks are : "+obj.Aggregate_Max());
        System.out.println("Minimum marks are : "+obj.Aggregate_Min());
        System.out.println("Sum of marks is : "+obj.Aggregate_Sum());
        System.out.println("Average of marks is : "+obj.Aggregate_Avg());

        obj.DeleteFrom(4);
        obj.SelectFrom();
*/

    }
}

//Insert Query
//Insert into Student values Rahul 23 67
//  0      1    2       3      4    5  6
//No of tokens : 7

//select query
//select * from student
//0     1   2       3
//No of tokens : 4

//delete from student where Rno = 4
//0        1    2       3    4  5  6
//No of tokens = 7;

//select Max Age from student
//0       1   2    3    4
//No of tokens = 5;