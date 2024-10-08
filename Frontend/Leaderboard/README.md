# Tutorials


ALL tutorials for cs309 will be in this repo.
- Each topic will be on a different branch.
- ~~GIT wiki will be used for documentation.~~

---
## Accessing tutorials 
You can access each tutorial by switching to its respective branch. To do so, you can use your terminal/Git bash. 

1. First, make sure you create a Personal Access Token, [details](https://docs.google.com/document/d/1F9ByRqWt5y2jzo04fn0pHOLoXhgp3EMEtRnIUH3_UhM/edit#heading=h.dvi4on3jn6fo)

2. Then, clone this repository

```bash
git clone https://git.las.iastate.edu/cs309/tutorials.git
```

3. Change the directory to the root of this repository  

```bash
cd </path/to/tutorials/directory>  
```

4. Next, switch to the desired branch. For example, to switch to "springboot_unit1_1_helloworld", use the following command:

```bash
git checkout springboot_unit1_1_helloworld
```

This should switch the local repository to the "springboot_unit1_1_helloworld" branch. For complete list of topics, check the section "Topics" below.

---

## Topics


### :book: UNIT-1

<!-- └── **Setup (TODO)**: 
 * [unit1_setup_guide](https://git.las.iastate.edu/cs309/tutorials/) `| Android Studio | IntelliJ | Postman |`
 -->
└── **Frontend**
* [android_unit1_1_helloworld](https://git.las.iastate.edu/cs309/tutorials/-/tree/android_unit1_1_helloworld) `A "hello world" example for testing environment setup.`   `|last modified: Jan-2024|` 
* [android_unit1_2_intent](https://git.las.iastate.edu/cs309/tutorials/-/tree/android_unit1_2_intent) `A "counter" example for toying with intent (switch between activities)`  `|last modified: Jan-2024|` 
* [android_unit1_3_login_signup](https://git.las.iastate.edu/cs309/tutorials/-/tree/android_unit1_3_login_signup) `A "login & signup" example. Only an example of the interface`    `|last modified: Jan-2024|` 

└── **Backend**
*  [springboot_unit1_1_helloworld](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit1_1_helloworld) `Getting started with SpringBoot`  `|last modified: Jan-2024|` 
*  [springboot_unit1_2_hellopeople](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit1_2_hellopeople) `Sending and Receiving data using GET, POST, PUT and DELETE APIs`    `|last modified: Feb-2024|` 
*  [springboot_unit1_3_petclinic_example](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit1_3_petclinic_example) `|last modified: Jan-2024|` 

---

### :book: UNIT-2

└── **Setup**: 
 * [Setting up MySQL on the remote server
](https://docs.google.com/presentation/d/1uNB26IgH-g3DP01sUwuyE2eOBe_0C5W9ofvTet5tDDY/edit#slide=id.p1)
 * [Deploy .jar file to the server
](https://docs.google.com/presentation/d/1q5ODoDvphSkqv3GXGv_l2odI9tCJ3ht63SvVQ5pjC3o/edit#slide=id.p1)
 * [Connect SpringBoot to MySQL DB
](https://docs.google.com/presentation/d/1ZqOwobX1qySx1zVxj8a_CAs6RzQY4ygqc9Tl4NDlnh0/edit#slide=id.p)

└── **Frontend**
* [android_unit2_1_volley](https://git.las.iastate.edu/cs309/tutorials/tree/android_unit2_1_volley) `example for: string / jsonObj / jsonArray / image  request calls`  `|last modified: Jan-2024|` 
* [android_unit2_2_volley](https://git.las.iastate.edu/cs309/tutorials/tree/android_unit2_2_volley) `example for: jsonObj / jsonArray request calls with data parsed into TextView and ListView`    `|last modified: Jan-2024|` 
* [android_unit2_3_volley](https://git.las.iastate.edu/cs309/tutorials/-/tree/android_unit2_3_volley) `example for: string GET / jsonObj POST request calls`    `|last modified: Jan-2024|` 
<!-- *  [RoundTrip_(with_retrofit2_and_h2)](https://git.las.iastate.edu/cs309/tutorials/-/tree/RoundTrip_(with_retrofit2_and_h2)) `Uses Retrofit2 to load all the Trivias and posts new trivias by json body and path. Video link: https://youtu.be/eUPFmgp6FKk` -->

└── **Backend**
*  [springboot_unit2_1_onetoone  (uses H2 database)](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit2_1_onetoone) `Example one-to-one relation, H2 database usage and CRUDL using SpringBoot`    `|last modified: Feb-2024|` 
*  [springboot_unit2_1_onetoone_mysql](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit2_1_onetoone_mysql) `MySQL database connectivity using spring boot + one-to-one relation + CRUDL using SpringBoot` `|last modified: Feb-2024|` 
*  [springboot_unit2_2_onetomany](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit2_2_onetomany) `Example one-to-many relations + querying using repository + CRUDL using Spring Boot`    `|last modified: Jan-2024|` 
*  [springboot_unit2_3_filerequests](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit2_3_filerequests) `Handling File requests/Multi-Part requests using SpringBoot`  `|last modified: 2022|` 
<!-- *  [RoundTrip_(with_retrofit2_and_h2)](https://git.las.iastate.edu/cs309/tutorials/-/tree/RoundTrip_(with_retrofit2_and_h2)) `Adds new Trivia questions and Answers by path and body and displays all Trivias. Video link: https://youtu.be/CEiUCDWygD0. To see how to plug into this project into MySQL watch this: https://youtu.be/hyFo1_jryL0` -->

---

### :book: UNIT-3

└── **Setup**: 
 * [unit3_setup_guide](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit2_4_swagger_ui) `| Java Doc | Swagger API |`   `|last modified: Dec-2023|` 

└── **Frontend**
* [unit3_1_one_websocket](https://git.las.iastate.edu/cs309/tutorials/-/tree/unit3_1_one_websocket) `Only one websocket server controller` `|last modified: Mar-2024|`
* [unit3_1_websockets](https://git.las.iastate.edu/cs309/tutorials/-/tree/unit3_1_websockets) `Two websocket server controllers`    `|last modified: Mar-2024|` 
* [unit3_2_image_upload](https://git.las.iastate.edu/cs309/tutorials/-/tree/unit3_2_image_handling) `Simple Implementation Mutipart file (image) upload for both ends`  `|last modified: Dec-2023|` 


└── **Backend**
*  [springboot_unit3_1_manytomany](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit3_1_manytomany) `Example many-to-many relations`   `|last modified: 2023|` 
*  [springboot_unit3_2_complex_relations_example](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit3_2_complex_relations_example) `Example many-to-many relations + complex queries using repository + CRUDL using spring boot`    `|last modified: 2023|` 
*  [springboot_unit3_3_manytomany_selfrelations](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit3_3_manytomany_selfrelations) `Self Relations among entities in Spring Boot` `|last modified: 2023|` 

* [unit3_1_one_websocket](https://git.las.iastate.edu/cs309/tutorials/-/tree/unit3_1_one_websocket) `Only one websocket server controller` `|last modified: Mar-2024|`
* [unit3_1_websockets](https://git.las.iastate.edu/cs309/tutorials/-/tree/unit3_1_websockets) `Two websocket server controllers`    `|last modified: Mar-2024|` 
* [unit3_2_websockets](https://git.las.iastate.edu/cs309/tutorials/-/tree/unit3_2_websockets) `backend websocket with storing message`  `|last modified: Dec 2023|` 
* [unit3_2_image_upload](https://git.las.iastate.edu/cs309/tutorials/-/tree/unit3_2_image_handling) `Simple Implementation Mutipart file (image) upload for both ends`  `|last modified: Dec 2023|` 

---

### :book: UNIT-4

└── **Setup**:  
*  [cicd_example](https://git.las.iastate.edu/cs309/tutorials/-/tree/cicd_example) `| CI/CD |`  `|last modified: Dec 2023|` 
*  [unit4_setup_guide](https://git.las.iastate.edu/cs309/tutorials/-/blob/testing/README.md) `| Code Coverage Report |`   `|last modified: April 2024|`

└── **Frontend**
* [testing](https://git.las.iastate.edu/cs309/tutorials/-/blob/testing/Frontend/Testing/app/src/androidTest/java/com/cs309/testing/SystemTest1.java) `testing examples`    `|last modified: April 2024|` 

└── **Backend**
*  [testing](https://git.las.iastate.edu/cs309/tutorials/-/blob/testing/Backend/testing/src/test/java/myapp/TestingSystemTest.java) `testing examples`    `|last modified: April 2024|` 


---

### Legacy
* [android_unit2_4_Phase 1,2,3,4](https://git.las.iastate.edu/cs309/tutorials/tree/android_unit2_4_phases) `Example to send requests using volley(to simple URL, JSON response, and Image)`
* [android_unit3_2_service_and_singleton](https://git.las.iastate.edu/cs309/tutorials/tree/android_unit3_2_service_and_singleton) `Example to Create a singleton class for request queue, and a listner for responses`
*  [springboot_unit3_4_mockito_testing](https://git.las.iastate.edu/cs309/tutorials/-/tree/springboot_unit3_4_mockito_testing)

* [android_unit3_1_mockito_testing](https://git.las.iastate.edu/cs309/tutorials/tree/android_unit3_1_mockito_testing) `Example to test android code with Mockito. UPDATE: Updated java to 11, gradle to 7.0.3, android sdk to 30 and fixed mockito dependencies - should work now`

* [android_tutorials_master](https://git.las.iastate.edu/cs309/tutorials/tree/android_tutorials_master) `Docs for Volley, CI/CD, Mockito and websocket`

*  [springboot_unit3_4_mockito_testing](https://git.las.iastate.edu/cs309/tutorials/tree/springboot_unit3_4_mockito_testing) `Example code to test a springboot application with mockito`
*  [android_unit3_2_service_and_singleton](https://git.las.iastate.edu/cs309/tutorials/tree/android_unit3_2_service_and_singleton) `Example code to test an android application with mockito. UPDATE: updated java to 11, gradle to 7.0.3, junit versions changed, tests fixed to work now with the latest version of Android Studio`
* [unit4_1_websockets](https://git.las.iastate.edu/cs309/tutorials/tree/unit4_1_websockets)
