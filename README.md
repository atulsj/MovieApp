# MovieApp.
App shows movie reviews and trailers of clicked movie from the list.

TODO:
Please before using App add your TMBD key in file build.gradle(Module:app) in place of <ENTER_YOUR_TMDB_KEY>.

buildTypes.each {
        it.buildConfigField 'String', 'TMDB_DEVELOPER_KEY', '"<ENTER_YOUR_TMDB_KEY>"'
    }
