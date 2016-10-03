package db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

/**
 * DB entry for gerrit_patch table
 */
//@Entity
//@Table(name = "gerrit_patch")
public class GerritPatch {
//    @Id
    private String patchId;
    private Integer changeId;
    private String author;
    private Date authorDate;
    private String committer;
    private Date committedDate;
    private String size;
    private Integer addedSize;
    private Integer deletedSize;
    private String nextPatchId;

    public String getPatchId() {
        return patchId;
    }

    public void setPatchId(String patchId) {
        this.patchId = patchId;
    }

    public Integer getChangeId() {
        return changeId;
    }

    public void setChangeId(Integer changeId) {
        this.changeId = changeId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getAuthorDate() {
        return authorDate;
    }

    public void setAuthorDate(Date authorDate) {
        this.authorDate = authorDate;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public Date getCommittedDate() {
        return committedDate;
    }

    public void setCommittedDate(Date committedDate) {
        this.committedDate = committedDate;
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

    public String getNextPatchId() {
        return nextPatchId;
    }

    public void setNextPatchId(String nextPatchId) {
        this.nextPatchId = nextPatchId;
    }
}
