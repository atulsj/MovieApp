package youtubeapidemo.examples.com.movieapp;


class Reviewer {
    private String content;
    private String author;

    Reviewer(String content, String author) {
        this.content = content;
        this.author = author;
    }

    String getContent() {
        return content;
    }

    String getAuthor() {
        return author;
    }
}
