package com.example.tatar.by.dao;

import com.example.tatar.by.constants.PostCategory;
import com.example.tatar.by.constants.PostGenre;
import com.example.tatar.by.constants.UserRole;
import com.example.tatar.by.model.Comment;
import com.example.tatar.by.model.Person;
import com.example.tatar.by.model.Post;
import com.example.tatar.by.util.GenresConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostDAO {

    private final GenresConverter converter;

    private final static String URL = "jdbc:postgresql://147.45.138.79:5432/default_db";

    private final static String USERNAME = "gen_user";

    private final static String PASSWORD = "WNdFVlzL,?G9jK";

    private static Connection connection;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Autowired
    public PostDAO(GenresConverter converter) {
        this.converter = converter;
    }

    //ДОБАВЬ РЕПОСТ
    public List<Post> getByGenres(List<PostGenre> genres, boolean reverse) throws SQLException {
        List<Post> result = new ArrayList<>();
        String stringGenres = converter.convertToDatabaseColumn(genres);
        String query;
        if (reverse) {
            query = "SELECT usr.id, usr.name, usr.email, usr.phone, usr.role, usr.telegram_username, post.id, post.text, post.created_at, post.likes, post.is_edited, post.category, post.genres FROM post " +
                    "INNER JOIN usr ON user_id = usr.id " +
                    "WHERE post.genres NOT LIKE ?";
        } else {
            query = "SELECT usr.id, usr.name, usr.email, usr.phone, usr.role, usr.telegram_username, post.id, post.text, post.created_at, post.likes, post.is_edited, post.category, post.genres FROM post " +
                    "INNER JOIN usr ON user_id = usr.id " +
                    "WHERE post.genres LIKE ?";
        }
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, "%" + stringGenres + "%");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            Post post = setPost(resultSet);
            Person person = setPerson(resultSet, 1, 2, 3, 4, 5, 6);
            List<Comment> comments = setComments(post.getId());
            post.setComments(comments);
            post.setPerson(person);
            System.out.println(post);
            result.add(post);
        }
        return result;
    }

    private List<Comment> setComments(int postId) throws SQLException {
        String query = "SELECT comment.id, comment.text, comment.likes, comment.created_at, post.id, usr.id, usr.name, usr.email, usr.phone, usr.role, usr.telegram_username FROM comment INNER JOIN post ON comment.post_id = post.id " +
                "INNER JOIN usr ON comment.user_id = usr.id";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        List<Comment> comments = new ArrayList<>();
        while (resultSet.next()) {
            int resultPostId = resultSet.getInt(5);
            if (postId == resultPostId) {
                int id = resultSet.getInt(1);
                String text = resultSet.getString(2);
                int likes = resultSet.getInt(3);
                LocalDateTime createdAt = resultSet.getObject(4, LocalDateTime.class);
                Person person = setPerson(resultSet, 6, 7, 8, 9, 10, 11);
                Comment comment = new Comment(id, text, likes, person, createdAt);
                comments.add(comment);
            }
        }
        return comments;
    }

    private Person setPerson(ResultSet resultSet, int idIndex, int nameIndex, int emailIndex, int phoneIndex, int roleIndex, int teleramUsernameIndex) throws SQLException {
        int id = resultSet.getInt(idIndex);
        String name = resultSet.getString(nameIndex);
        String email = resultSet.getString(emailIndex);
        String phone = resultSet.getString(phoneIndex);
        UserRole role = UserRole.valueOf(resultSet.getString(roleIndex));
        String telegramUsername = resultSet.getString(teleramUsernameIndex);
        return new Person(id, name, email, phone, telegramUsername, role);
    }

    private Post setPost(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(7);
        String text = resultSet.getString("text");
        LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
        int likes = resultSet.getInt("likes");
        boolean isEdited = resultSet.getBoolean("is_edited");
        PostCategory category = PostCategory.valueOf(resultSet.getString("category"));
        List<PostGenre> resultGenres = converter.convertToEntityAttribute(resultSet.getString("genres"));
        return new Post(id, text, createdAt, likes, isEdited, category, resultGenres);
    }

}
