# MovieApp

<div><h3>TODO:</h3></div>
Please before using App add your TMBD key in file build.gradle(Module:app) in place of <ENTER_YOUR_TMDB_KEY>.

 buildTypes.each { 
       it.buildConfigField 'String', 'TMDB_DEVELOPER_KEY', '"<ENTER_YOUR_TMDB_KEY>"'
 }
 
### Description of Android Application:
* An Android app that displays list of movies using TMDB database API key. 
* The app is build using Glide image library.
* The app opens up with the movie poster in grid format. On selecting a movie, it gives descriptions of movie and gives an option of         making a movie favorite though FAB button.
* Preference settings are provided to display movies list based on popularity, favorite, or top rated. 

