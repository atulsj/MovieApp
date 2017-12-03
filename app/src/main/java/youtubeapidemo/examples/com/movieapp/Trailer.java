package youtubeapidemo.examples.com.movieapp;


class Trailer {
    private String name;
    private String key;

    Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }

    String getKey() {
        return key;
    }

    String getName() {
        return name;
    }
}
