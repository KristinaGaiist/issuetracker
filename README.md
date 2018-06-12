Issue Tracker
==============================================
## Time spent on task
  * 8 hours spent on the study of the material
  * 38 hours spent on the study of the material 

## Building
Run the following command to build the project:
```
mvn clean install
```

## Running
Create connection with database h2. (./h2.sh command).
Run the following command:
```
mvn exec:java
```
Open your web browser and visit http://localhost:1234/ 
You'll see the login page. Enter data: login:admin password:123 (access right - full. It means, that you can add issues, update issues, delete issues, commenting issues, delete or update your comments and search issues).
                                        login:jon password:123 (access right - view and update. It means, that you can add issues, update issues, commenting issues, delete or update your comments and search issues. But you can't delete issues).
                                        login:alex password:123 (access right - only view. It means, that you can commenting issues, delete or update your comments and search issues. But you can't delete issues and update issues).
Also you can singUp (access right - only view).
After you'll see all issues.
