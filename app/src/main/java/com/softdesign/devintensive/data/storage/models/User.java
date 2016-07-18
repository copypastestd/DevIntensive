package com.softdesign.devintensive.data.storage.models;

import com.softdesign.devintensive.data.network.res.UserListRes;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;

/**
 * Класс сущности User для связи с БД
 */
@Entity(active = true, nameInDb = "USERS")
public class User {

    @Id
    private Long id;

    @NotNull
    @Unique
    private String remoteId;

    private String photo;

    @NotNull
    @Unique
    private String fullName;

    @NotNull
    @Unique
    private String searchName;

    private int rating;

    private int codeLines;

    private int projects;

    private String bio;

    @ToMany(joinProperties = {
            @JoinProperty(name = "remoteId", referencedName = "userRemoteId")
    })
    private List<Respository> respositories;

    public User(UserListRes.UserData userRes) {

        this.remoteId = userRes.getId();
        this.photo = userRes.getPublicInfo().getPhoto();
        this.fullName = userRes.getFullName();
        this.searchName = userRes.getFullName().toUpperCase();
        this.rating = userRes.getProfileValues().getRating();
        this.codeLines = userRes.getProfileValues().getLinesCode();
        this.projects = userRes.getProfileValues().getProjects();
        this.bio = userRes.getPublicInfo().getBio();
    }

    public String getPhoto() {
        return photo;
    }

    public String getFullName() {
        return fullName;
    }

    public int getRating() {
        return rating;
    }

    public int getCodeLines() {
        return codeLines;
    }

    public int getProjects() {
        return projects;
    }

    public String getBio() {
        return bio;
    }

    public List<Respository> getRespositories() {
        return respositories;
    }
}
