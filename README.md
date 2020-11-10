# Open Trivia App

## Functional Features

- This app involved 3 main functionality or pages which are: 
    - **Home Page**: Displays app name and a spinner for user to choose their question category. Secondly, 2 option button are provided for user to start the game or view the available question numbers 
    - **Game Page**: Displays the question text, difficulty according to the chosen category name. Once an answer is selected, the app will evaluate the answer and display the result immediately. Then, they can choose whether to roll another question or finish with the game
    - **Question Number Page**: Displays the total game number according to the chosen category provided in the spinner options. This involves displaying the total number of pending, verified, rejected questions. 


## Technical Features

- This app is constructed based on MVVM code structure.
- RxJava is used as a reactive programming steps to observe the changes of the collected data instantly.
- Koin is used as a lightweight dependency injection that connects the ViewModel with the related fragments, which used a centralized module as a mediator between these components.
- Navigation Component is applied to help with the application navigation between the fragments. The nav_host_fragment acts as parent navigation to all the fragments.
- The UI of this app is mainly constructed using the Constraint Layout.
- Retrofit library is used for calling the OpenTDB API.
- Logging interceptor is used to log and print out the response from the API call.
- Moshi library is used for JSON mapping which assists in converting the JSON data to Java objects and vice versa.
