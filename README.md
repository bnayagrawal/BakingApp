# BakingApp
Udacity Android Developer Nano Degree I final Project.

About this app
--------------
* Retrives a list of recipes from given url (By Udacity).
* On Clicking on a recipe it shows the ingredients and steps to cook.
* On tablet if a step is clicked it shows in the right panel of the screen.
* On phone if a step is clicked it launches a new activity which displays video instruction.
* This app also has a widget. If added on homescreen shows the selected recipes' ingredients.
* The widget will update ingredients when user selects a recipe in the app.
* The source code has espresso unit tests.

This app makes use of the following external libraries and resources
--------------------------------------------------------------------
* Recipe data is provided by Udacity.
* Retrofit http library (With GSON converter) http://square.github.io/retrofit/
* GSON library for serializing json to POJO https://github.com/google/gson 
* Image caching and loading framework https://github.com/bumptech/glide 
* View binding using ButterKnife https://github.com/JakeWharton/butterknife
* RecyclerView animations by https://github.com/wasabeef/recyclerview-animators
* ExoPlayer for playing video https://github.com/google/ExoPlayer

To companies (I am for hire). This app features or uses the following components of android
--------------------------------------------------------------------------------------------
* This app handles Activity and Fragment lifeCycles.
* This app maintains instanceState on configuration change (Activity and Fragment).
* Passing data between activities and fragments.
* SQLite Database, Content Providers (Offline data persistance).
* Fragments and re-use of the same fragment in different activities.
* Desiging layouts for different screen sizes(Phone and tablet) and orientation (Landscape)
* Widgets (RemoteViews, RemoteViewsFactory)
* Loading data from network, Loading and caching Image form network.
* Streaming video using ExoPlayer.
* JSON data handling and serialization.
* Services, pendingIntents.
* RecyclerViews, CardViews, CustomListAdapter.
* Performing tasks off the UI thread.
* Animations. Error handling and debugging.
* Espresso Unit Tests.

Some screenshots (Tablet)
-------------------------
![Screenshot](https://i.imgur.com/IMztPc3.png)
![Screenshot](https://i.imgur.com/Rf0YAak.png)
![Screenshot](https://i.imgur.com/59lx6fy.png)
![Screenshot](https://i.imgur.com/6CaUI6r.png)
