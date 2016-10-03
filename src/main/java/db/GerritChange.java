package db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

/**
 * DB entry for gerrit_change table
 */
@Entity
@Table(name = "gerrit_change")
public class GerritChange {
    @Id
    private Integer id;
    private String subject;
    private String link;
    private String status;
    private String owner;
    private String project;
    private String branch;
    private Date updatedOn;
    private String size;
    private Integer addedSize;
    private Integer deletedSize;
    private String codeReviewScore;
    private String moduleOwnerScore;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getAddedSize() {
        return addedSize;
    }

    public void setAddedSize(Integer addedSize) {
        this.addedSize = addedSize;
    }

    public Integer getDeletedSize() {
        return deletedSize;
    }

    public void setDeletedSize(Integer deletedSize) {
        this.deletedSize = deletedSize;
    }

    public String getCodeReviewScore() {
        return codeReviewScore;
    }

    public void setCodeReviewScore(String codeReviewScore) {
        this.codeReviewScore = codeReviewScore;
    }

    public String getModuleOwnerScore() {
        return moduleOwnerScore;
    }

    public void setModuleOwnerScore(String moduleOwnerScore) {
        this.moduleOwnerScore = moduleOwnerScore;
    }
}
