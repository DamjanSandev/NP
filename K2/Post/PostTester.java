package Napredno.K2.Post;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}

class Comment {
    private final String username;
    private final String content;
    private final String id;
    List<Comment> commentReplies;
    private int likes;

    public Comment(String username, String content, String id) {
        this.username = username;
        this.content = content;
        this.id = id;
        likes = 0;
        commentReplies = new ArrayList<>();
    }

    public void addComment(Comment c) {
        commentReplies.add(c);
    }

    public void addLike() {
        likes++;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public List<Comment> getCommentReplies() {
        return commentReplies;
    }

    public int getLikes() {
        return likes + commentReplies.stream().mapToInt(Comment::getLikes).sum();
    }

    public String toString(int indent) {
        String indentC = IntStream.range(0, indent).mapToObj(i -> " ").collect(Collectors.joining(""));
        return String.format("%sComment: %s\n%sWritten by: %s\n%sLikes: %d\n%s", indentC, content, indentC, username, indentC, likes,
                commentReplies.stream().sorted(Comparator.comparing(Comment::getLikes).reversed()).map(comment -> comment.toString(indent + 4)).collect(Collectors.joining(""))
        );

    }
}

class Post {
    List<Comment> comments;

    private final String username;
    private final String postContent;

    public Post(String username, String postContent) {
        this.username = username;
        this.postContent = postContent;
        comments = new ArrayList<>();
    }

    public void addComment(String author, String id, String content, String replyToId) {
        if (replyToId == null) {
            comments.add(new Comment(author, content, id));
        } else {
            Comment c = findComment(replyToId, comments);
            c.addComment(new Comment(author, content, id));

        }

    }

    private Comment findComment(String id, List<Comment> comments) {
        return comments.stream().filter(comment -> comment.getId().equals(id)).findFirst().orElseGet(() -> comments.stream()
                .map(comment -> findComment(id, comment.getCommentReplies())).filter(Objects::nonNull).findFirst().orElse(null));
    }

    public void likeComment(String commentId) {
        Comment c = findComment(commentId, comments);
        c.addLike();
    }

    @Override
    public String toString() {
        return String.format("Post: %s\nWritten by: %s\nComments:\n%s", postContent, username,
                comments.stream().sorted(Comparator.comparing(Comment::getLikes).reversed()).map(comment -> comment.toString(8)).collect(Collectors.joining(""))
        );
    }
}
