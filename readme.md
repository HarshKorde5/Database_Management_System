
# Mini DBMS in Java

This project is a **Mini Database Management System (DBMS)** implemented in Java.  
It simulates basic SQL-like operations such as `INSERT`, `SELECT`, `DELETE`, and aggregate functions (`MAX`, `MIN`, `SUM`, `AVG`) on a single table (`Student`).

---

##  Features

- **Table Creation**  
  - A `Student` table schema with the following attributes:
    - `Rno` (Auto-incremented Roll Number)
    - `Name` (String)
    - `Age` (Integer)
    - `Marks` (Integer)

- **Insert Data**  
  - Insert new records into the table.  
  - Example:  
    ```
    insert into student values Rahul 23 67
    ```

- **Select Queries**  
  - Fetch all records:  
    ```
    select * from student
    ```
  - Fetch by roll number:  
    ```
    select * from student where Rno = 2
    ```
  - Fetch by name:  
    ```
    select * from student where Name = Rahul
    ```

- **Delete Records**  
  - Delete a student record by roll number:  
    ```
    delete from student where Rno = 4
    ```

- **Aggregate Functions**  
  - Maximum marks:  
    ```
    select MAX marks from student
    ```
  - Minimum marks:  
    ```
    select MIN marks from student
    ```
  - Sum of marks:  
    ```
    select SUM marks from student
    ```
  - Average marks:  
    ```
    select AVG marks from student
    ```

- **Exit Command**  
  ```
  exit
  ```


---

##  How to Run

1. **Compile the program**
   ```bash
   javac DBMSMain.java
   ```

2. **Run the DBMS**
   ```bash
   java DBMSMain
   ```

3. **Use commands** (examples below):
   ```
   insert into student values Rahul 23 67
   insert into student values Amit 21 88
   select * from student
   select MAX marks from student
   delete from student where Rno = 1
   exit
   ```

---

## Sample Run

```
DBMS started successfully...
Table Schema created successfully...
DBMS :insert into student values Rahul 23 89
DBMS :insert into student values Amit 21 90
DBMS :select * from student
Records from the student table are :
1 Rahul 23 89
2 Amit 21 90
DBMS :select MAX marks from student
Maximum marks are : 90
DBMS :exit
Thank You for using DBMS...
```

---

## Future Improvements
- Add **UPDATE** query support.  
- Implement **WHERE conditions with multiple attributes**.  
- Add **persistent storage** (save & load from files).  
- Expand support for multiple tables.

---


## Notes
- This project is **for learning purposes only**.  
- It demonstrates how DBMS concepts (DDL, DML, aggregate functions) can be simulated using **Java and data structures** like `LinkedList`.

---


**Contact**

📧 Email: harshkorde05@gmail.com 

🌐 LinkedIn: [linkedin.com/in/harshkorde](https://www.linkedin.com/in/harshkorde)

For any further questions or inquiries, feel free to reach out. We are happy to assist you with any queries.