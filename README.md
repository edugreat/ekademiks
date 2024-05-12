# ekademiks
## Introduction
### This is a basic online academic test platform
### Description
Welcome to our GitHub repository! Here, we house the code for our online test assessment platform tailored for high school students. With easy login access, students can engage in multi-choice assessments spanning various academic levels and subjects. Instant feedback ensures timely evaluation of their performance, fostering continuous learning and growth. Join us in shaping the future of education!
## Technologies used
This project's evolution heavily relies on three main separate concerns - **database management**, **Server side implementation** and **front-end service**. 
Technologies used are shown below
Technology | Implements
-----------|-----------
SQL|Database management
Java's spring boot framework|Backend/server side implementation
angular framework|frontend/client side implementation
Bootstrap & Angular material design | css design management
Typescript | frontend model classes and component

The choice of [spring boot framework](https://spring.io/projects/spring-boot) is based upon its saving time in project developement, easy configurations and its great abstraction implemetations with _minimal code_.
On the otherhand, the [angular framework](https://angular.io/) being _component based_, is a great tool to _knit together_ different chunks of front-end tasks. The choice of [bootstrap](https://getbootstrap.com/) is in its ease to integrate and use in html pages.

## How to use
For local use of the project follow these steps:

1  On your ``git bash command prompt`` execute the following command
   * ``cd [directory]`` to change to your preferred directory
   * ``git init`` to [initialize](https://git-scm.com/docs/git-init) a local git repo in your folder
   * ``git remote add [chosen-name] https://github.com/edugreat/ekademiks.git`` [to add](https://git-scm.com/docs/git-remote) the remote url
   * ``git clone chosen-name`` to [clone](https://git-scm.com/docs/git-clone) to your local machine
   
2 You can skip the step one above by simply
   * downloading the [zip project](https://github.com/edugreat/ekademiks/archive/refs/heads/dev.zip)
   * unzip to your desired folder
   
3 Run the database scripts in the [starter folder](https://github.com/edugreat/ekademiks/blob/dev/starter/database.sql) on your MYSQL workbench or command line interface   
4 Import the spring boot application in the ``ekademiks folder`` into your **Maven supported IDE** and download the ``pom dependencies``
   * Run the spring boot app

5 Open the angular front-end app in the ``frontend folder`` in your preferred [code editor](https://www.softwaretestinghelp.com/best-code-editor/) such as [vs code](https://code.visualstudio.com/)
6 Execute the ``ng serve`` to initialize the app then point to ``http://localhost:4200`` on your web browser to access the application
   * You can as well skip the immediate step above and execute ``ng serve -o`` to initialize and automatically point your browser to access the app
   
 ## Credits
 Many thanks to [Chad Darby](http://t.co/bXQaNWm0S0) for amazing [fullstack courses on udemy](https://www.udemy.com/course/full-stack-angular-spring-boot-tutorial/)
 
 The book [reactive javascript](https://pragprog.com/search/?q=reactive+javascript) is a topnotch to getting started and going in Reactive javascript
