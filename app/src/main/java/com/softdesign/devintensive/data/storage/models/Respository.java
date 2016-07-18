package com.softdesign.devintensive.data.storage.models;

import com.softdesign.devintensive.data.network.res.UserModelRes;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Класс сущности Respository для связи с БД
 */
@Entity(active = true, nameInDb = "REPOSITORIES")
public class Respository {

    @Id
    private Long id;

    @NotNull
    @Unique
    private String remoteId;

    private String repositoryName;

    private String userRemoteId;

    public Respository(UserModelRes.Repo respositoryRes, String userId) {
        this.repositoryName = respositoryRes.getGit();
        this.remoteId = userId;
        this.remoteId = respositoryRes.getId();
    }

    public String getRepositoryName() {
        return repositoryName;
    }
}
